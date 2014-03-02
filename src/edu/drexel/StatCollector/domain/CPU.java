package edu.drexel.StatCollector.domain;

public class CPU {
    // Processes
    private Integer procs = Integer.MIN_VALUE;
    private Integer procsRunning = Integer.MIN_VALUE;

    // Threads
    private Integer threadAllocCount = Integer.MIN_VALUE;
    private Integer threadAllocSize = Integer.MIN_VALUE;

    // Processes
    public Integer getProcs() { return procs; }
    public void setProcs(Integer processes) { procs = processes; }

    public Integer getProcsRunning() { return procsRunning; }
    public void setProcsRunning(Integer procsRunCount) { procsRunning = procsRunCount; }

    // Threads
    public Integer getThreadAllocCount() { return threadAllocCount; }
    public void setThreadAllocCount(Integer allocThreadCount) { threadAllocCount = allocThreadCount; }

    public Integer getThreadAllocSize() { return threadAllocSize; }
    public void setThreadAllocSize(Integer allocThreadSize) { threadAllocSize = allocThreadSize; }
}