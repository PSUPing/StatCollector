package edu.drexel.StatCollector.domain;

public class Memory {
    // Standard Memory
    private Integer memoryTotal = Integer.MIN_VALUE;
    private Integer memoryFree = Integer.MIN_VALUE;

    // Dalvik Memory
    private Integer dalvikPrivateDirty = Integer.MIN_VALUE;
    private Integer dalvikSharedDirty = Integer.MIN_VALUE;
    private Integer dalvikPSS = Integer.MIN_VALUE;

    // Native Memory
    private Integer nativePrivateDirty = Integer.MIN_VALUE;
    private Integer nativeSharedDirty = Integer.MIN_VALUE;
    private Integer nativePSS = Integer.MIN_VALUE;
    private Long nativeAllocHeap = Long.MIN_VALUE;
    private Long nativeFreeHeap = Long.MIN_VALUE;
    private Long nativeHeap = Long.MIN_VALUE;

    // Other Memory
    private Integer otherPrivateDirty = Integer.MIN_VALUE;
    private Integer otherSharedDirty = Integer.MIN_VALUE;
    private Integer otherPSS = Integer.MIN_VALUE;

    // Standard Memory
    public Integer getMemoryTotal() { return memoryTotal; }
    public void setMemoryTotal(Integer memTotal) { memoryTotal = memTotal; }

    public Integer getMemoryFree() { return memoryFree; }
    public void setMemoryFree(Integer memFree) { memoryFree = memFree; }

    // Dalvik Memory
    public Integer getDalvikPrivateDirty() { return dalvikPrivateDirty; }
    public void setDalvikPrivateDirty(Integer dalvikPD) { dalvikPrivateDirty = dalvikPD; }

    public Integer getDalvikPrivateShared() { return dalvikSharedDirty; }
    public void setDalvikPrivateShared(Integer dalvikSD) { dalvikSharedDirty = dalvikSD; }

    public Integer getDalvikPSS() { return dalvikPSS; }
    public void setDalvikPSS(Integer dalvikPSSMem) { dalvikPSS = dalvikPSSMem; }

    // Native Memory
    public Integer getNativePrivateDirty() { return nativePrivateDirty; }
    public void setNatviePrivateDirty(Integer nativePD) { nativePrivateDirty = nativePD; }

    public Integer getNativePrivateShared() { return nativeSharedDirty; }
    public void setNativePrivateShared(Integer nativeSD) { nativeSharedDirty = nativeSD; }

    public Integer getNativePSS() { return nativePSS; }
    public void setNativePSS(Integer nativePSSMem) { dalvikPSS = nativePSSMem; }

    public Long getNativeAllocHeap() { return nativeAllocHeap; }
    public void setNativeAllocHeap(Long nativeAH) { nativeAllocHeap = nativeAH; }

    public Long getNativeFreeHeap() { return nativeFreeHeap; }
    public void setNativeFreeHeap(Long nativeFH) { nativeFreeHeap = nativeFH; }

    public Long getNativeHeap() { return nativeHeap; }
    public void setNativeHeap(Long nativeH) { nativeHeap = nativeH; }

    // Other Memory
    public Integer getOtherPrivateDirty() { return otherPrivateDirty; }
    public void setOtherPrivateDirty(Integer otherPD) { otherPrivateDirty = otherPD; }

    public Integer getOtherPrivateShared() { return otherSharedDirty; }
    public void setOtherPrivateShared(Integer otherSD) { otherSharedDirty = otherSD; }

    public Integer getOtherPSS() { return otherPSS; }
    public void setOtherPSS(Integer otherPSSMem) { otherPSS = otherPSSMem; }
}