package edu.drexel.StatCollector;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StatsToCollect {
    private String _id;
    private String thread_count;
    private String vm_memory;
//    private String proc_count;
//    private String run_proc_count;
    private String date_time;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public StatsToCollect(String threadCount, String vmMemory, String dateTime) {
        // String procCount, String runProcCount,
        _id = UUID.randomUUID().toString();
        thread_count = threadCount;
        vm_memory = vmMemory;
//        proc_count = procCount;
//        run_proc_count = runProcCount;
        date_time = dateTime;
    }

    public StatsToCollect(String threadCount, String vmMemory, Date dateTime) {
        _id = UUID.randomUUID().toString();
        thread_count = threadCount;
        vm_memory = vmMemory;
//        proc_count = procCount;
//        run_proc_count = runProcCount;
        date_time = dateFormat.format(dateTime);
    }

    public String getID() {
        return _id;
    }

/*    public String getProcCount() {
        return proc_count;
    }

    public void setProcCount(String procCount) {
        proc_count = procCount;
    }

    public String getRunProcCount() {
        return run_proc_count;
    }

    public void setRunProcCount(String runProcCount) {
        run_proc_count = runProcCount;
    }*/

    public String getThreadCount() {
        return thread_count;
    }

    public void setThreadCount(String threadCount) {
        thread_count = threadCount;
    }

    public String getVMMemory() {
        return vm_memory;
    }

    public void setVMMemory(String vmMemory) {
        vm_memory = vmMemory;
    }

    public String getDateTime() {
        return date_time;
    }

    public Date getDateTimeAsDate() {
        try {
            return dateFormat.parse(date_time);
        }
        catch (ParseException parseEx) {
            System.out.println(parseEx.getMessage().toString());
            System.exit(1);
        }

        return null;
    }

    public void setDateTime(String dateTime) {
        date_time = dateTime;
    }

    public void setDateTime(Date dateTime) {
        date_time = dateFormat.format(dateTime);
    }
}