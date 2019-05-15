package com.seasun.management.dto;

import com.seasun.management.model.DumpDay;

public class AppDumpDayDto extends DumpDay {
    private String dateStr;
    private String dumpProbability;
    private String dumpMachineProbability;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getDumpProbability() {
        return dumpProbability;
    }

    public void setDumpProbability(String dumpProbability) {
        this.dumpProbability = dumpProbability;
    }

    public String getDumpMachineProbability() {
        return dumpMachineProbability;
    }

    public void setDumpMachineProbability(String dumpMachineProbability) {
        this.dumpMachineProbability = dumpMachineProbability;
    }
}
