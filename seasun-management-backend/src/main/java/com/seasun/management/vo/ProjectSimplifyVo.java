package com.seasun.management.vo;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class ProjectSimplifyVo extends ProjectVo{

    private String orderCenterAddList;

    private MultipartFile attachment;

    private String reason;

    public String getOrderCenterAddList() {
        return orderCenterAddList;
    }

    public void setOrderCenterAddList(String orderCenterAddList) {
        this.orderCenterAddList = orderCenterAddList;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ProjectSimplifyVo{" +
                "orderCenterAddList='" + orderCenterAddList + '\'' +
                ", attachment=" + (Objects.isNull(attachment) ? null : attachment.getOriginalFilename()) +
                ", reason='" + reason + '\'' +
                '}';
    }
}
