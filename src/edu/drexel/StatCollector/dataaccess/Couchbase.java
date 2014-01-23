package edu.drexel.StatCollector.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.couchbase.cblite.*;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;

import edu.drexel.StatCollector.StatsToCollect;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import org.ektorp.*;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

public class Couchbase {
    public static final String TAG = "StatCollector";
    public static final String DATABASE_NAME = "examiner";
    public static final String dDocName = "get";
    public static final String dDocId = "_design/" + dDocName;
    public static final String regQuery = "get_regs";
    public static final String DATABASE_URL = "http://10.0.2.2:4984";  // 10.0.2.2 == Android Simulator equivalent of 127.0.0.1
    public static String filesDir;

    public static Context baseContext;
    public static CBLServer server;
    public static HttpClient httpClient;
    public static CouchDbConnector couchDbConnector;
    public static CouchDbInstance dbInstance;

    protected static ReplicationCommand pushReplicationCommand;
    protected static ReplicationCommand pullReplicationCommand;

    {
        CBLURLStreamHandlerFactory.registerSelfIgnoreError();
    }

    public Couchbase() {
    }

    public static void startCB() {
        try {
            System.setProperty("viewmode", "development"); // before the connection to Couchbase
            server = new CBLServer(filesDir);

            if(httpClient != null) {
                httpClient.shutdown();
            }

            httpClient = new CBLiteHttpClient(server);
            dbInstance = new StdCouchDbInstance(httpClient);

            EktorpAsyncTask startupTask = new EktorpAsyncTask() {

                @Override
                protected void doInBackground() {
                    couchDbConnector = dbInstance.createConnector(DATABASE_NAME, true);
                }

                @Override
                protected void onSuccess() {
                    startReplications();
                }
            };

            startupTask.execute();
        }
        catch (MalformedURLException malURLEx) {
            Log.e(TAG, malURLEx.getMessage().toString());
        }
        catch (IOException ioEx) {
            Log.e(TAG, ioEx.getMessage().toString());
        }
    }

    public static JsonNode makeCBStatObj (StatsToCollect statObj) {
        ObjectNode stat = JsonNodeFactory.instance.objectNode();

        stat.put("_id", UUID.randomUUID().toString());
        stat.put("thread_count", statObj.getThreadCount());
        stat.put("vm_memory", statObj.getVMMemory());
        stat.put("date_time", statObj.getDateTime());

        return stat;
    }

    public static void createStat (JsonNode node) {
        final JsonNode createNode = node;

        EktorpAsyncTask createTask = new EktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                couchDbConnector.create(createNode);
            }

            @Override
            protected void onSuccess() {
                startReplications();
            }
        };

        createTask.execute();
    }

    public static RegressionInfo getRegression() {
        RegressionInfo regInfo = new RegressionInfo();
        CBLDatabase db = server.getDatabaseNamed(DATABASE_NAME);
        CBLView view = db.getViewNamed(String.format("%s/%s", dDocName, regQuery));

        view.setMapReduceBlocks(new CBLViewMapBlock() {
            @Override
            public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
                Object idCol = document.get("regression");
                if(idCol != null) {
                    emitter.emit(idCol.toString(), document);
                }
            }
        }, null, "1.0");

        EktorpAsyncTask createTask = new EktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                ViewQuery viewQuery = new ViewQuery().designDocId(dDocId).viewName(regQuery);
                ViewResult viewResult = couchDbConnector.queryView(viewQuery);

                // Log.e()
                Log.e(TAG, "Number of Rows: " + String.valueOf(viewResult.getRows().size()));

                for (ViewResult.Row reg : viewResult.getRows()) {
                    Log.e(TAG, "Linear Regression - " + reg.getValue());
                }
            }

            @Override
            protected void onSuccess() {
                startReplications();
            }
        };

        createTask.execute();

        return regInfo;
    }

    private static void startReplications() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(baseContext);
        String defaultDatabaseUrl = DATABASE_URL + "/" + DATABASE_NAME;

        pushReplicationCommand = new ReplicationCommand.Builder()
                .source(DATABASE_NAME)
                .target(prefs.getString("sync_url", defaultDatabaseUrl))
                .continuous(true)
                .build();

        EktorpAsyncTask pushReplication = new EktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                dbInstance.replicate(pushReplicationCommand);
            }
        };

        pushReplication.execute();

        pullReplicationCommand = new ReplicationCommand.Builder()
                .source(prefs.getString("sync_url", defaultDatabaseUrl))
                .target(DATABASE_NAME)
                .continuous(true)
                .build();

        EktorpAsyncTask pullReplication = new EktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                dbInstance.replicate(pullReplicationCommand);
            }
        };

        pullReplication.execute();
    }
}