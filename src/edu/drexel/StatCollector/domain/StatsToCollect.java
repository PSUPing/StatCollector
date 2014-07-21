package edu.drexel.StatCollector.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class StatsToCollect {
    private String _id;
    private Long calcWaitTime = 0L;
    private String dateTime;
    public boolean logCPU = false;
    public boolean logDalvik = false;
    public boolean logMem = false;
    public boolean logNetwork = false;
    public String runName;
    public Integer battery = 0;
    public Memory memNode;
    public CPU cpuNode;
    public Dalvik dalvikNode;
    public Network networkNode;
    public HashMap<Integer, String> apps = new HashMap<Integer, String>();

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public StatsToCollect() {
        Calendar cal = Calendar.getInstance();

        _id = UUID.randomUUID().toString();
        setDateTime(cal.getTime());
        memNode = new Memory();
        cpuNode = new CPU();
        dalvikNode = new Dalvik();
        networkNode = new Network();
    }

    public String getID() { return _id; }

    public Long getCalcWaitTime() { return calcWaitTime; }
    public void setCalcWaitTime(Long waitTime) { calcWaitTime = waitTime; }

    public Date getDateTimeAsDate() {
        try {
            return dateFormat.parse(dateTime);
        }
        catch (ParseException parseEx) {
            System.out.println(parseEx.getMessage().toString());
            System.exit(1);
        }

        return null;
    }

    public String getDateTime() { return dateTime; }

    public void setDateTime(String dtTime) { dateTime = dtTime; }
    public void setDateTime(Date dtTime) { dateTime = dateFormat.format(dtTime); }
}