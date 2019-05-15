package com.seasun.management.model.cp;

import java.util.Date;

public class ReqissueHistory {
    private Long id;

    private Integer issueId;

    private Integer userAction;

    private Date onDate;

    private Integer byWho;

    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public Integer getUserAction() {
        return userAction;
    }

    public void setUserAction(Integer userAction) {
        this.userAction = userAction;
    }

    public Date getOnDate() {
        return onDate;
    }

    public void setOnDate(Date onDate) {
        this.onDate = onDate;
    }

    public Integer getByWho() {
        return byWho;
    }

    public void setByWho(Integer byWho) {
        this.byWho = byWho;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }
}