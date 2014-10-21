package edu.drexel.StatCollector;

import android.content.ComponentName;
import android.content.Intent;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import android.widget.Toast;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;
import edu.drexel.StatCollector.dataaccess.Couchbase;

public class StatViewActivity extends Activity {
    public static final String RUN_NAME = "runName";
    private static final String TAG = StatViewActivity.class.getSimpleName();
    private boolean start = true;
    private TextView runName;
    private TextView sysStatus;
    private TextView ipAddr;
    private TextView port;
    private TextView dbName;
    private Button statButton;
    private CheckBox chkLogCPU;
    private CheckBox chkLogMem;
    private CheckBox chkLogDalvik;
    private CheckBox chkLogNetwork;

    {
        CBLURLStreamHandlerFactory.registerSelfIgnoreError();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get screen objects
        sysStatus = (TextView)findViewById(R.id.sysStatus);
        runName = (TextView)findViewById(R.id.runName);
        statButton = (Button)findViewById(R.id.startStats);
        chkLogCPU = (CheckBox)findViewById(R.id.logCPU);
        chkLogMem = (CheckBox)findViewById(R.id.logMem);
        chkLogDalvik = (CheckBox)findViewById(R.id.logDalvik);
        chkLogNetwork = (CheckBox)findViewById(R.id.logNetwork);
        ipAddr = (TextView)findViewById(R.id.ipAddr);
        port = (TextView)findViewById(R.id.port);
        dbName = (TextView)findViewById(R.id.dbName);

        try {
            ApplicationInfo ai = getBaseContext().getPackageManager().getApplicationInfo(getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            Couchbase.databaseURL = bundle.getString("ipAddress");
            Integer tempPort = bundle.getInt("port");
            Couchbase.databasePort = tempPort.toString();
            Couchbase.databaseName = bundle.getString("dbName");

            chkLogCPU.setChecked(bundle.getBoolean("logCPU"));
            chkLogMem.setChecked(bundle.getBoolean("logMem"));
            chkLogDalvik.setChecked(bundle.getBoolean("logDalvik"));
            chkLogNetwork.setChecked(bundle.getBoolean("logNetwork"));
            ipAddr.setText(Couchbase.databaseURL);
            port.setText(Couchbase.databasePort);
            dbName.setText(Couchbase.databaseName);
            sysStatus.setText("Ready - http://" + Couchbase.databaseURL + ":" + Couchbase.databasePort + "/" + Couchbase.databaseName);
        }
        catch (PackageManager.NameNotFoundException nnfEx) {
            Log.e(TAG, nnfEx.getMessage().toString());
        }

        statButton.setOnClickListener(new View.OnClickListener() {
            Intent svcIntent = new Intent(getBaseContext(), StatCollectorService.class);

            public void onClick(View v) {
                if (runName.getText().toString().trim().equals("")) {
                    sysStatus.setText("A name for the run must be provided");
                }
                else {
                    if (start) {
                        Log.e(TAG, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                        sysStatus.setText("StatCollector service running...");
                        start = false;
                        statButton.setText("Stop Collecting");
                        String nameOfRun = runName.getText().toString();
                        runName.setEnabled(false);
                        chkLogCPU.setEnabled(false);
                        chkLogMem.setEnabled(false);
                        chkLogDalvik.setEnabled(false);
                        chkLogNetwork.setEnabled(false);

                        svcIntent.putExtra(RUN_NAME, nameOfRun);
                        svcIntent.putExtra("logCPU", chkLogCPU.isChecked());
                        svcIntent.putExtra("logMem", chkLogMem.isChecked());
                        svcIntent.putExtra("logDalvik", chkLogDalvik.isChecked());
                        svcIntent.putExtra("logNetwork", chkLogNetwork.isChecked());
                        startService(svcIntent);
                    }
                    else {
                        stopService(svcIntent);

                        try {
                            ApplicationInfo ai = getBaseContext().getPackageManager().getApplicationInfo(getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
                            Bundle bundle = ai.metaData;

                            start = true;
                            statButton.setText("Start Collecting");
                            runName.setEnabled(true);
                            chkLogCPU.setEnabled(true);
                            chkLogMem.setEnabled(true);
                            chkLogDalvik.setEnabled(true);
                            chkLogNetwork.setEnabled(true);
                            runName.setText("");
                            sysStatus.setText("Ready - " + Couchbase.databaseURL + ":" + Couchbase.databasePort + "/" + Couchbase.databaseName);
                        }
                        catch (PackageManager.NameNotFoundException nnfEx) {
                            Log.e(TAG, nnfEx.getMessage().toString());
                        }
                    }
                }
            }
        });

        // Start Couchbase
        Couchbase.filesDir = getFilesDir().getAbsolutePath();
        Couchbase.baseContext = getBaseContext();
        Couchbase.startCB();
    }
}