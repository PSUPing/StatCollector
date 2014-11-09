package edu.drexel.StatCollector;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;

import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;

import edu.drexel.StatCollector.dataaccess.Couchbase;
import edu.drexel.StatCollector.domain.StatsToCollect;
import edu.drexel.StatCollector.sensors.SystemReads;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatCollectorService extends Service {
    private static final String TAG = Utils.TAG + StatCollectorService.class.getSimpleName();
    private static final Handler handler = new Handler();
//    private Date finishDate = new Date();
    private Debug.InstructionCount icount = new Debug.InstructionCount();
    private IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private Intent actIntent;
    private String nameOfRun;
    private ArrayList<StatsToCollect> statList = new ArrayList<StatsToCollect>();
    private int startPos = 0;
    private int endPos = 0;
    private boolean collectSensors = false;
    private boolean logCPU = false;
    private boolean logDalvik = false;
    private boolean logMem = false;
    private boolean logNetwork = false;

    private Runnable captureStats = new Runnable() {
        @Override
        public void run() {
            try {
//                Intent batteryStatus = registerReceiver(null, iFilter);
//                Integer level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//                Integer status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                StatsToCollect stat = new StatsToCollect();
                Long captureWaitTime = 100L;

                Log.e(TAG, "Delayed write: " + String.valueOf(statList.size()));

                stat.runName = nameOfRun;
                stat.logCPU = logCPU;
                stat.logDalvik = logDalvik;
                stat.logMem = logMem;
                stat.logNetwork = logNetwork;

                icount.resetAndStart();

                if (logCPU) {
                    SystemReads sysRead = new SystemReads();

                    stat.cpuNode.setThreadAllocCount(Debug.getThreadAllocCount());
                    stat.cpuNode.setThreadAllocSize(Debug.getThreadAllocSize());
                    stat.cpuNode.setProcs(sysRead.getProcessCount());
                    stat.cpuNode.setProcsRunning(sysRead.getRunProcCount());
                }

                if (logDalvik) {
                    stat.dalvikNode.setClassesLoaded(Debug.getLoadedClassCount());
                    stat.dalvikNode.setGlobalClassInit(Debug.getGlobalClassInitCount());
                    stat.dalvikNode.setTotalMthdInvoc(getGlobalMethodInvoc());
                    // stat.dalvikNode.setTotalGlobalExec(getGlobalInstrCount()); Still not working
                }

                if (logMem) {
                    Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
                    SystemReads sysRead = new SystemReads();

                    stat.memNode.setMemoryTotal(sysRead.getVMMemoryTotal());
                    stat.memNode.setMemoryFree(sysRead.getVMMemoryFree());

                    stat.memNode.setDalvikPrivateDirty(memInfo.dalvikPrivateDirty);
                    stat.memNode.setDalvikPrivateShared(memInfo.dalvikSharedDirty);
                    stat.memNode.setDalvikPSS(memInfo.dalvikPss);

                    stat.memNode.setNatviePrivateDirty(memInfo.nativePrivateDirty);
                    stat.memNode.setNativePrivateShared(memInfo.nativeSharedDirty);
                    stat.memNode.setNativePSS(memInfo.nativePss);

                    stat.memNode.setNativeAllocHeap(Debug.getNativeHeapAllocatedSize());
                    stat.memNode.setNativeFreeHeap(Debug.getNativeHeapFreeSize());
                    stat.memNode.setNativeHeap(Debug.getNativeHeapSize());

                    stat.memNode.setOtherPrivateDirty(memInfo.otherPrivateDirty);
                    stat.memNode.setOtherPrivateShared(memInfo.otherSharedDirty);
                    stat.memNode.setOtherPSS(memInfo.otherPss);
                }

                if (logNetwork) {
                    stat.networkNode.getCurrTx();
                    stat.networkNode.getCurrRx();

                    /*for (ApplicationInfo appInfo : getPackageManager().getInstalledApplications(0)) {
                        stat.apps.put(appInfo.uid, appInfo.packageName);
                        stat.networkNode.getAppTx(appInfo.uid);
                        stat.networkNode.getAppRx(appInfo.uid);
                    }*/
                }

                stat.setCalcWaitTime(captureWaitTime);
                Log.e(TAG, "Delayed write: " + String.valueOf(statList.size()));
                statList.add(stat);
                Log.e(TAG, "Delayed write: " + statList.get(endPos).runName);
                endPos++;

//                Couchbase.createStat(Couchbase.makeCBStatObj(stat));

                if (collectSensors)
                    handler.postDelayed(captureStats, captureWaitTime);

/*                if (status == BatteryManager.BATTERY_STATUS_CHARGING)
                    captureWaitTime = 5000L;
                else
                    captureWaitTime = 10000L;

                // Reset the instructions and start the counter over again
                if (level > 30 || status == BatteryManager.BATTERY_STATUS_CHARGING) {
//                    Calendar cal = Calendar.getInstance();

                    stat.setCalcWaitTime(captureWaitTime);
                    Couchbase.createStat(Couchbase.makeCBStatObj(stat));

                    if (cal.getTime().compareTo(finishDate) < 0) {
                        icount.resetAndStart();
                        handler.postDelayed(captureStats, captureWaitTime);
                    }
                }
                else {
                    stat.battery = level;
                    stat.setCalcWaitTime(captureWaitTime);
                    Couchbase.createStat(Couchbase.makeCBStatObj(stat));
                }*/
            }
            catch (Exception ex) {
                Log.e(TAG, "Delayed write: " + ex.getMessage());
            }
        }
    };

    private Runnable persistStats = new Runnable() {
        @Override
        public void run() {
            try {
                Long persistWaitTime = 60000L;

                if (startPos <= endPos) {
                    for (int x = startPos; x < endPos; x++) {
                        Couchbase.createStat(Couchbase.makeCBStatObj(statList.get(x)));
                    }

                    startPos = endPos + 1;
                }

                if (collectSensors)
                    handler.postDelayed(persistStats, persistWaitTime);
                else
                    Couchbase.stopCB();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    };

    {
        CBLURLStreamHandlerFactory.registerSelfIgnoreError();
    }

    @Override
    public void onCreate() {
        super.onCreate();

/*        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            logCPU = bundle.getBoolean("logCPU");
            logDalvik = bundle.getBoolean("logDalvik");
            logMem = bundle.getBoolean("logMem");
            logNetwork = bundle.getBoolean("logNetwork");
        }
        catch (PackageManager.NameNotFoundException nnfEx) {
            Log.e(TAG, "Wrong name: " + nnfEx.getMessage());
        }*/

        // Figure out when this should be finished
/*        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.HOUR, 3);
        finishDate = cal.getTime();*/
        icount.resetAndStart();
        collectSensors = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Get the run name
        actIntent = intent;
        nameOfRun = actIntent.getStringExtra(StatViewActivity.RUN_NAME);
        logCPU = actIntent.getBooleanExtra("logCPU", false);
        logMem = actIntent.getBooleanExtra("logMem", false);
        logDalvik = actIntent.getBooleanExtra("logDalvik", false);
        logNetwork = actIntent.getBooleanExtra("logNetwork", false);

//        icount.resetAndStart();
//        collectSensors = true;

        // Start the capture and persistence processes
        persistStats.run();
        captureStats.run();

        Log.e(TAG, "Name of Run (" + StatViewActivity.RUN_NAME + "): " + nameOfRun);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        collectSensors = false;
        handler.removeCallbacks(captureStats);
        handler.removeCallbacks(persistStats);
        persistStats.run();
        Log.e(TAG, "Stop Service");
        super.onDestroy();
    }

/*    private void logMalware(StatsToCollect stats) {
        RegressionInfo regInfo = Couchbase.getRegression();
        Double yTestVal = regInfo.getM() * Double.parseDouble(stats.getThreadCount()) + regInfo.getB();

        if (yTestVal >= (Double.parseDouble(stats.getVMMemory()) * 1.10))
            Log.e(TAG, "Malware found! Y: " + stats.getVMMemory() + " Y(Expected): " + yTestVal + " M: " + regInfo.getM() + " X: " + stats.getThreadCount() + " B: " + regInfo.getB());
        else
            Log.e(TAG, "No malware found! Y: " + stats.getVMMemory() + " Y(Expected): " + yTestVal + " M: " + regInfo.getM() + " X: " + stats.getThreadCount() + " B: " + regInfo.getB());
    }*/

    private Integer getGlobalInstrCount() {
        try {
            if (icount.collect())
                return icount.globalTotal();
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return Integer.MIN_VALUE;
    }

    private Integer getGlobalMethodInvoc() {
        try {
            if (icount.collect())
                return icount.globalMethodInvocations();
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return Integer.MIN_VALUE;
    }

    public IBinder onBind(Intent intent){
        return null;
    }
}