package com.seasun.management.vo.dataCenter;


import java.util.List;

public class RealTimeOnlineDataVo {

    private DailyVo daily;
    private List<OnlineChargeVo> onlineCharge;
    private List<OnlineChargeSumVo> onlineChargeSum;
    private StatOnlineNumVo statOnlineNum;

    public DailyVo getDaily() {
        return daily;
    }

    public void setDaily(DailyVo daily) {
        this.daily = daily;
    }

    public StatOnlineNumVo getStatOnlineNum() {
        return statOnlineNum;
    }

    public void setStatOnlineNum(StatOnlineNumVo statOnlineNum) {
        this.statOnlineNum = statOnlineNum;
    }

    public List<OnlineChargeVo> getOnlineCharge() {
        return onlineCharge;
    }

    public void setOnlineCharge(List<OnlineChargeVo> onlineCharge) {
        this.onlineCharge = onlineCharge;
    }

    public List<OnlineChargeSumVo> getOnlineChargeSum() {
        return onlineChargeSum;
    }

    public void setOnlineChargeSum(List<OnlineChargeSumVo> onlineChargeSum) {
        this.onlineChargeSum = onlineChargeSum;
    }
}
