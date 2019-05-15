package com.seasun.management.vo;

import com.seasun.management.dto.UserFeedbackDto;

import java.util.List;


public class UserFeedbackVo {

    private Long totalPages;

    private List<UserFeedbackDto> userFeedbackList;

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<UserFeedbackDto> getUserFeedbackList() {
        return userFeedbackList;
    }

    public void setUserFeedbackList(List<UserFeedbackDto> userFeedbackList) {
        this.userFeedbackList = userFeedbackList;
    }
}
