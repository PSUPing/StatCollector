package edu.drexel.StatCollector;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;
import edu.drexel.StatCollector.dataaccess.Couchbase;

public class StatViewActivity extends Activity {
    private static final String TAG = StatViewActivity.class.getSimpleName();

    {
        CBLURLStreamHandlerFactory.registerSelfIgnoreError();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.e(TAG, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        Couchbase.filesDir = getFilesDir().getAbsolutePath();
        Couchbase.baseContext = getBaseContext();
        Couchbase.startCB();

        startService(new Intent(this, StatCollectorService.class));
    }
}