package edu.drexel.StatCollector;

import android.app.Service;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;

import edu.drexel.StatCollector.dataaccess.Couchbase;
import edu.drexel.StatCollector.dataaccess.RegressionInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class StatCollectorService extends Service {
    private static final String TAG = StatCollectorService.class.getSimpleName();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final Handler handler = new Handler();
    private static Integer currCount = 0;
    private static Integer lastCount = 0;
    private static Integer lastRunProcCount = 0;
    private Intent actIntent;
    private Debug.InstructionCount icount = new Debug.InstructionCount();
    private Runnable captureStats = new Runnable() {
        @Override
        public void run() {
            try {
                StatsToCollect stat = new StatsToCollect(getThreadCount().toString(), getVMMemory().toString(), Calendar.getInstance().getTime());
//                StatsToCollect stat = new StatsToCollect(getProcessCount().toString(), getRunProcCount().toString(), Calendar.getInstance().getTime());
//                Couchbase.createStat(Couchbase.makeCBStatObj(stat));
                logMalware(stat);

                handler.postDelayed(captureStats, 60000);
            }
            catch (Exception ex) {
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

        icount.resetAndStart();
        captureStats.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        actIntent = intent;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public Integer getClassCount() {
        return Debug.getGlobalClassInitCount();
    }

    public Double getHeapSize() {
        return new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
    }

    private void logMalware(StatsToCollect stats) {
        RegressionInfo regInfo = Couchbase.getRegression();
        Double yTestVal = regInfo.getM() * Double.parseDouble(stats.getThreadCount()) + regInfo.getB();

        if (yTestVal >= (Double.parseDouble(stats.getVMMemory()) * 1.10))
            Log.e(TAG, "Malware found! Y: " + stats.getVMMemory() + " Y(Expected): " + yTestVal + " M: " + regInfo.getM() + " X: " + stats.getThreadCount() + " B: " + regInfo.getB());
        else
            Log.e(TAG, "No malware found! Y: " + stats.getVMMemory() + " Y(Expected): " + yTestVal + " M: " + regInfo.getM() + " X: " + stats.getThreadCount() + " B: " + regInfo.getB());
    }

    public Integer getThreadCount() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

        Log.e(TAG, "Thread Size: " + threadSet.size());

        return threadSet.size();
    }

    public Integer getVMMemory() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
            String totalMemory = "";
            String freeMemory = "";
            Integer totalMem = 0;
            Integer freeMem = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                Log.e(TAG, line);
                String[] pair = line.split(":");

                if (pair[0].trim().equals("MemTotal")) {
                    totalMemory = pair[1].trim().substring(0, pair[1].trim().length() - 2).trim();
                    totalMem = Integer.parseInt(totalMemory);
                }
                else if (pair[0].trim().equals("MemFree")) {
                    freeMemory = pair[1].trim().substring(0, pair[1].trim().length() - 2).trim();
                    freeMem = Integer.parseInt(freeMemory);

                    break;
                }
            }

            Log.e(TAG, "Total Memory: " + totalMemory + " Free Memory: " + freeMemory);

            return totalMem - freeMem;
        }
        catch (IOException ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return 0;
    }

    public Integer getProcessCount() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] pair = line.split(" ");

                if (pair[0].trim().equals("processes")) {
                    currCount = Integer.parseInt(pair[1]);
                    Integer iterim = currCount - lastCount;
                    lastCount = currCount;

                    return iterim;
                }
            }
        }
        catch (IOException ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return 0;
    }

    public Integer getRunProcCount() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] pair = line.split(" ");

                if (pair[0].trim().equals("procs_running"))
                    lastRunProcCount = Integer.parseInt(pair[1]);
                    return lastRunProcCount;
            }
        }
        catch (IOException ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return 0;
    }

/*
    public Integer getGlobalInstrCount() {
        try {
            if (icount.collect())
                Log.e(TAG, String.valueOf(icount.globalTotal()));
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return icount.globalTotal();
    }

    public Integer getThreadCount() {
        return Debug.getThreadAllocCount();
    }

*/

    public IBinder onBind(Intent intent){
        return null;
    }
}