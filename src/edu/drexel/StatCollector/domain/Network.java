package edu.drexel.StatCollector.domain;

import android.net.TrafficStats;
import android.util.Log;
import edu.drexel.StatCollector.Utils;

import java.util.HashMap;

public class Network {
    private static final String TAG = Utils.TAG + Network.class.getSimpleName();
    private Long prevTx = 0L;
    private Long prevRx = 0L;
    public Long currTx = 0L;
    public Long currRx = 0L;
    public HashMap<Integer, Long> appTraffic = new HashMap<Integer, Long>();

    public void getCurrTx() {
        Long curr = TrafficStats.getTotalTxBytes() - prevTx;

        prevTx = TrafficStats.getTotalTxBytes();
        currTx = curr;
    }

    public void getCurrRx() {
        Long curr = TrafficStats.getTotalRxBytes() - prevRx;

        prevRx = TrafficStats.getTotalRxBytes();
        currRx = curr;
    }

    public Long getAppTx(Integer appId) {
        Long prevAppTx = appTraffic.get(appId);
        Long currAppTx = TrafficStats.getUidTxBytes(appId);

        if (prevAppTx == null)
            prevAppTx = 0L;

        if (currAppTx == null)
            currAppTx = 0L;

        appTraffic.put(appId, currAppTx);

        return currAppTx - prevAppTx;
    }

    public Long getAppRx(Integer appId) {
        Long prevAppRx = appTraffic.get(appId);
        Long currAppRx = TrafficStats.getUidRxBytes(appId);

        if (prevAppRx == null)
            prevAppRx = 0L;

        if (currAppRx == null)
            currAppRx = 0L;

        appTraffic.put(appId, currAppRx);

        return currAppRx - prevAppRx;
    }
}