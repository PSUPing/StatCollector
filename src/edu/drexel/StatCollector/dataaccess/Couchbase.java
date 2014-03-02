package edu.drexel.StatCollector.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.Log;

import com.couchbase.cblite.*;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;

import edu.drexel.StatCollector.domain.StatsToCollect;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import org.ektorp.*;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

public class Couchbase {
    public static final String TAG = "StatCollector";
    public static final String DATABASE_NAME = "examiner";
    public static final String dDocName = "get";
    public static final String dDocId = "_design/" + dDocName;
    public static final String regQuery = "get_regs";
    public static final String DATABASE_URL = "http://192.168.100.11:4984";  // 10.0.2.2 == Android Simulator equivalent of 127.0.0.1
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
        ObjectNode statNode = JsonNodeFactory.instance.objectNode();

        statNode.put("_id", statObj.getID());
        statNode.put("calc_wait", statObj.getCalcWaitTime());
        statNode.put("date_time", statObj.getDateTime());

        if (statObj.battery > 0)
            statNode.put("battery_level", statObj.battery);

        // Memory Node
        if (statObj.logCPU) {
            ObjectNode cpuNode = statNode.putObject("cpu");

            if (statObj.cpuNode.getThreadAllocCount() > 0)
                cpuNode.put("thread_alloc_count", statObj.cpuNode.getThreadAllocCount());

            if (statObj.cpuNode.getThreadAllocSize() > 0)
                cpuNode.put("thread_alloc_size", statObj.cpuNode.getThreadAllocSize());

            if (statObj.cpuNode.getProcs() > 0)
                cpuNode.put("proc_count", statObj.cpuNode.getProcs());

            if (statObj.cpuNode.getProcsRunning() > 0)
                cpuNode.put("proc_running", statObj.cpuNode.getProcsRunning());
        }

        // Memory Node
        if (statObj.logMem) {
            ObjectNode memNode = statNode.putObject("memory");

            // System Memory
            if (statObj.memNode.getMemoryTotal() > 0)
                memNode.put("mem_total", statObj.memNode.getMemoryTotal());

            if (statObj.memNode.getMemoryFree() > 0)
                memNode.put("mem_free", statObj.memNode.getMemoryFree());

            // Dalvik
            if (statObj.memNode.getDalvikPrivateDirty() > 0L)
                memNode.put("dalvik_private_dirty", statObj.memNode.getDalvikPrivateDirty());

            if (statObj.memNode.getDalvikPrivateShared() > 0L)
                memNode.put("dalvik_shared_dirty", statObj.memNode.getDalvikPrivateShared());

            if (statObj.memNode.getDalvikPSS() > 0L)
                memNode.put("dalvik_pss", statObj.memNode.getDalvikPSS());

            // Native
            if (statObj.memNode.getNativePrivateDirty() > 0L)
                memNode.put("native_private_dirty", statObj.memNode.getNativePrivateDirty());

            if (statObj.memNode.getNativePrivateShared() > 0L)
                memNode.put("native_shared_dirty", statObj.memNode.getNativePrivateShared());

            if (statObj.memNode.getNativePSS() > 0L)
                memNode.put("native_pss", statObj.memNode.getNativePSS());

            if (statObj.memNode.getNativeAllocHeap() > 0L)
                memNode.put("native_allocated_heap", statObj.memNode.getNativeAllocHeap());

            if (statObj.memNode.getNativeFreeHeap() > 0L)
                memNode.put("native_free_heap", statObj.memNode.getNativeFreeHeap());

            if (statObj.memNode.getNativeHeap() > 0L)
                memNode.put("native_heap", statObj.memNode.getNativeHeap());

            // Other
            if (statObj.memNode.getOtherPrivateDirty() > 0L)
                memNode.put("other_private_dirty", statObj.memNode.getOtherPrivateDirty());

            if (statObj.memNode.getOtherPrivateShared() > 0L)
                memNode.put("other_shared_dirty", statObj.memNode.getOtherPrivateShared());

            if (statObj.memNode.getOtherPSS() > 0L)
                memNode.put("other_pss", statObj.memNode.getOtherPSS());
        }

        // CPU Node
        if (statObj.logNetwork) {
/*
            ObjectNode appNode = statNode.putObject("apps");
            Iterator networkIt = statObj.networkNode.appTraffic.entrySet().iterator();
            Iterator appIt = statObj.apps.entrySet().iterator();

            while (appIt.hasNext()) {
                Map.Entry pairs = (Map.Entry)appIt.next();
                appNode.put(pairs.getKey().toString(), pairs.getValue().toString());
                appIt.remove();
            }
*/
            ObjectNode networkNode = statNode.putObject("network");

            if (statObj.networkNode.currTx > 0)
                networkNode.put("total_tx", statObj.networkNode.currTx);

            if (statObj.networkNode.currRx > 0)
                networkNode.put("total_rx", statObj.networkNode.currRx);
/*
            while (networkIt.hasNext()) {
                Map.Entry pairs = (Map.Entry)networkIt.next();
                networkNode.put(pairs.getKey().toString(), pairs.getValue().toString());
                networkIt.remove();
            }
*/
        }

        // Dalvik Node
        if (statObj.logDalvik) {
            ObjectNode dalvikNode = statNode.putObject("dalvik");

            if (statObj.dalvikNode.getClassesLoaded() > 0)
                dalvikNode.put("classes_loaded", statObj.dalvikNode.getClassesLoaded());

            if (statObj.dalvikNode.getGlobalClassInit() > 0)
                dalvikNode.put("global_class_init", statObj.dalvikNode.getGlobalClassInit());

            if (statObj.dalvikNode.getTotalMthdInvoc() > 0)
                dalvikNode.put("total_methods_invoc", statObj.dalvikNode.getTotalMthdInvoc());
        }

        return statNode;
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