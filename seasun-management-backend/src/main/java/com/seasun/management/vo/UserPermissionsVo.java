package com.seasun.management.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserPermissionsVo {
    List<String> orgPerms;
    List<String> finPerms;
    List<String> perfPerms;
    List<String> commPerms;
    List<String> pmPerms;
    List<String> cpPerms;

    public List<String> getCpPerms() {
        return cpPerms;
    }

    public void setCpPerms(List<String> cpPerms) {
        this.cpPerms = cpPerms;
    }

    public List<String> getPmPerms() {
        return pmPerms;
    }

    public void setPmPerms(List<String> pmPerms) {
        this.pmPerms = pmPerms;
    }

    public List<String> getOrgPerms() {
        return orgPerms;
    }

    public void setOrgPerms(List<String> orgPerms) {
        this.orgPerms = orgPerms;
    }

    public List<String> getFinPerms() {
        return finPerms;
    }

    public void setFinPerms(List<String> finPerms) {
        this.finPerms = finPerms;
    }

    public List<String> getPerfPerms() {
        return perfPerms;
    }

    public void setPerfPerms(List<String> perfPerms) {
        this.perfPerms = perfPerms;
    }

    public List<String> getCommPerms() {
        return commPerms;
    }

    public void setCommPerms(List<String> commPerms) {
        this.commPerms = commPerms;
    }

    public UserPermissionsVo add(UserPermissionsVo target) {
        this.finPerms.addAll(target.getFinPerms());
        this.orgPerms.addAll(target.getOrgPerms());
        this.perfPerms.addAll(target.getPerfPerms());
        this.commPerms.addAll(target.getCommPerms());
        this.pmPerms.addAll(target.getPmPerms());
        this.cpPerms.addAll(target.getCpPerms());

        Set<String> finSet = new HashSet<>(finPerms);
        Set<String> orgSet = new HashSet<>(orgPerms);
        Set<String> perfSet = new HashSet<>(perfPerms);
        Set<String> commSet = new HashSet<>(commPerms);
        Set<String> pmSet = new HashSet<>(pmPerms);
        Set<String> cpSet = new HashSet<>(cpPerms);

        this.finPerms = new ArrayList<>(finSet);
        this.orgPerms = new ArrayList<>(orgSet);
        this.perfPerms = new ArrayList<>(perfSet);
        this.commPerms = new ArrayList<>(commSet);
        this.pmPerms = new ArrayList<>(pmSet);
        this.cpPerms = new ArrayList<>(cpSet);
        return this;
    }
}
