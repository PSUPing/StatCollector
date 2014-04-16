package edu.drexel.StatCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;
import edu.drexel.StatCollector.dataaccess.Couchbase;
import edu.drexel.StatCollector.sensors.SystemCalls;

import java.io.File;

public class StatViewActivity extends Activity {
    private static final String TAG = StatViewActivity.class.getSimpleName();
    private SystemCalls sysCalls = new SystemCalls();

    {
        CBLURLStreamHandlerFactory.registerSelfIgnoreError();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.e(TAG, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        TextView test = (TextView)findViewById(R.id.tempText);
        test.setText("");

        Couchbase.filesDir = getFilesDir().getAbsolutePath();
        Couchbase.baseContext = getBaseContext();
        Couchbase.startCB();

        startService(new Intent(this, StatCollectorService.class));
    }

    private boolean isInteger(String str) {
        if (str == null) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!java.lang.Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}