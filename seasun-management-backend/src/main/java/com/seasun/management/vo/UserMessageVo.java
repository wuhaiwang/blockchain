package com.seasun.management.vo;

import java.util.List;

public class UserMessageVo {

    private Long totalPages;

    private List<UserMessageCommuteDto> messageList;

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<UserMessageCommuteDto> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<UserMessageCommuteDto> messageList) {
        this.messageList = messageList;
    }
}
