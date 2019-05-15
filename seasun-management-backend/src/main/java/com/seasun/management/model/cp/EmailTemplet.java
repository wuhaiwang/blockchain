package com.seasun.management.model.cp;

public class EmailTemplet {
    private Integer id;

    private String subject;

    private String mainBody;

    private String mailTo;

    private String mailCC;

    private Byte autoSend;

    private Integer sendFrequency;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getMainBody() {
        return mainBody;
    }

    public void setMainBody(String mainBody) {
        this.mainBody = mainBody == null ? null : mainBody.trim();
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo == null ? null : mailTo.trim();
    }

    public String getMailCC() {
        return mailCC;
    }

    public void setMailCC(String mailCC) {
        this.mailCC = mailCC == null ? null : mailCC.trim();
    }

    public Byte getAutoSend() {
        return autoSend;
    }

    public void setAutoSend(Byte autoSend) {
        this.autoSend = autoSend;
    }

    public Integer getSendFrequency() {
        return sendFrequency;
    }

    public void setSendFrequency(Integer sendFrequency) {
        this.sendFrequency = sendFrequency;
    }
}