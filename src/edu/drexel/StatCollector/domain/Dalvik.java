package edu.drexel.StatCollector.domain;

public class Dalvik {
    private Integer globalClassInit = Integer.MIN_VALUE;
    private Integer classesLoaded = Integer.MIN_VALUE;
    private Integer totalMthdInvoc = Integer.MIN_VALUE;
    private Integer totalGlobalExec = Integer.MIN_VALUE;

    public Integer getGlobalClassInit() { return globalClassInit; }
    public void setGlobalClassInit(Integer glblClassInit) { globalClassInit = glblClassInit; }

    public Integer getClassesLoaded() { return classesLoaded; }
    public void setClassesLoaded(Integer clsLoaded) { classesLoaded = clsLoaded; }

    public Integer getTotalMthdInvoc() { return totalMthdInvoc; }
    public void setTotalMthdInvoc(Integer totMethodInvoc) { totalMthdInvoc = totMethodInvoc; }

    public Integer getTotalGlobalExec() { return totalGlobalExec; }
    public void setTotalGlobalExec(Integer totGlobalExec) { totalGlobalExec = totGlobalExec; }
}