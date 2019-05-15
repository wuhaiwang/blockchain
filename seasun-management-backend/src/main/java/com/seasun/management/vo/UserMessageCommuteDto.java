package com.seasun.management.vo;

import com.seasun.management.model.UserMessage;

public class UserMessageCommuteDto extends UserMessage {

    private String receiverName;
    private String senderName;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
