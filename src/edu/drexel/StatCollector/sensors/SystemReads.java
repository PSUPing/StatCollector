package edu.drexel.StatCollector.sensors;

import android.util.Log;
import edu.drexel.StatCollector.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SystemReads {
    private static final String TAG = Utils.TAG + SystemReads.class.getSimpleName();
    private static Integer currCount = 0;
    private static Integer lastCount = 0;
    private static Integer lastRunProcCount = 0;

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

    public Integer getVMMemoryTotal() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
            String totalMemory = "";
            String line;

            while ((line = reader.readLine()) != null) {
                Log.e(TAG, line);
                String[] pair = line.split(":");

                if (pair[0].trim().equals("MemTotal")) {
                    totalMemory = pair[1].trim().substring(0, pair[1].trim().length() - 2).trim();
                    return Integer.parseInt(totalMemory);
                }
            }
        }
        catch (IOException ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return 0;
    }

    public Integer getVMMemoryFree() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
            String freeMemory = "";
            String line;

            while ((line = reader.readLine()) != null) {
                Log.e(TAG, line);
                String[] pair = line.split(":");

                if (pair[0].trim().equals("MemFree")) {
                    freeMemory = pair[1].trim().substring(0, pair[1].trim().length() - 2).trim();
                    return Integer.parseInt(freeMemory);
                }
            }
        }
        catch (IOException ex) {
            Log.e(TAG, ex.getMessage().toString());
        }

        return 0;
    }
}
