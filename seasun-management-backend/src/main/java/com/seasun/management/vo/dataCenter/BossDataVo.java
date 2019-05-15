package com.seasun.management.vo.dataCenter;


import com.seasun.management.dto.dataCenter.StatOnlineAccountDto;

import java.util.List;

public class BossDataVo {
    private List<StatOnlineAccountDto> currentData;//实时在线人数当前
    private List<StatOnlineAccountDto> previousData;//实时在线人数前一天
    private List<BossOnlineSumVo> onlineChargeSum;//实时充值
    private List<BossOnlineSumVo> productIncome;//新品总收入
    private List<BossOnlineSumVo> productSales;//新品总售卖件数
    private List<BossOnlineSumVo> incrementConsume;//增值消耗累计
    private List<BossDailyVo> dailyItems;//日报数据
    private List<BossOnlineGameVo> dailyGameItems;//详细的售卖数据

    public List<BossOnlineGameVo> getDailyGameItems() {
        return dailyGameItems;
    }

    public void setDailyGameItems(List<BossOnlineGameVo> dailyGameItems) {
        this.dailyGameItems = dailyGameItems;
    }

    public List<StatOnlineAccountDto> getCurrentData() {
        return currentData;
    }

    public void setCurrentData(List<StatOnlineAccountDto> currentData) {
        this.currentData = currentData;
    }

    public List<StatOnlineAccountDto> getPreviousData() {
        return previousData;
    }

    public void setPreviousData(List<StatOnlineAccountDto> previousData) {
        this.previousData = previousData;
    }

    public List<BossOnlineSumVo> getOnlineChargeSum() {
        return onlineChargeSum;
    }

    public void setOnlineChargeSum(List<BossOnlineSumVo> onlineChargeSum) {
        this.onlineChargeSum = onlineChargeSum;
    }

    public List<BossOnlineSumVo> getProductIncome() {
        return productIncome;
    }

    public void setProductIncome(List<BossOnlineSumVo> productIncome) {
        this.productIncome = productIncome;
    }

    public List<BossOnlineSumVo> getProductSales() {
        return productSales;
    }

    public void setProductSales(List<BossOnlineSumVo> productSales) {
        this.productSales = productSales;
    }

    public List<BossOnlineSumVo> getIncrementConsume() {
        return incrementConsume;
    }

    public void setIncrementConsume(List<BossOnlineSumVo> incrementConsume) {
        this.incrementConsume = incrementConsume;
    }

    public List<BossDailyVo> getDailyItems() {
        return dailyItems;
    }

    public void setDailyItems(List<BossDailyVo> dailyItems) {
        this.dailyItems = dailyItems;
    }
}