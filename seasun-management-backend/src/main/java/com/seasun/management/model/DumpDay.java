package com.seasun.management.model;

import java.util.Date;

public class DumpDay {
    private Integer id;

    private Date date;

    private String project;

    private String version;

    private String versionex;

    private Integer dumpCount;

    private Integer dumpMachineCount;

    private Integer activeMachineCount;

    private Integer sessionCount;

    private Integer dumpTypeCount;

    public interface Project {
        String JX3 = "JX3";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project == null ? null : project.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getVersionex() {
        return versionex;
    }

    public void setVersionex(String versionex) {
        this.versionex = versionex == null ? null : versionex.trim();
    }

    public Integer getDumpCount() {
        return dumpCount;
    }

    public void setDumpCount(Integer dumpCount) {
        this.dumpCount = dumpCount;
    }

    public Integer getDumpMachineCount() {
        return dumpMachineCount;
    }

    public void setDumpMachineCount(Integer dumpMachineCount) {
        this.dumpMachineCount = dumpMachineCount;
    }

    public Integer getActiveMachineCount() {
        return activeMachineCount;
    }

    public void setActiveMachineCount(Integer activeMachineCount) {
        this.activeMachineCount = activeMachineCount;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public Integer getDumpTypeCount() {
        return dumpTypeCount;
    }

    public void setDumpTypeCount(Integer dumpTypeCount) {
        this.dumpTypeCount = dumpTypeCount;
    }
}