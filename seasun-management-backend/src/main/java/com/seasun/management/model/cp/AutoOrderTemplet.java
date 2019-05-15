package com.seasun.management.model.cp;

public class AutoOrderTemplet extends AutoOrderTempletKey {
    private Integer id;

    private String client;

    private String workRequirements;

    private String theFormatWorks;

    private String acceptanceCriteria;

    private String remark;

    private String other;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client == null ? null : client.trim();
    }

    public String getWorkRequirements() {
        return workRequirements;
    }

    public void setWorkRequirements(String workRequirements) {
        this.workRequirements = workRequirements == null ? null : workRequirements.trim();
    }

    public String getTheFormatWorks() {
        return theFormatWorks;
    }

    public void setTheFormatWorks(String theFormatWorks) {
        this.theFormatWorks = theFormatWorks == null ? null : theFormatWorks.trim();
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria == null ? null : acceptanceCriteria.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other == null ? null : other.trim();
    }
}