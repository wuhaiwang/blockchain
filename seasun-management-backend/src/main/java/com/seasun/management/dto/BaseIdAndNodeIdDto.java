package com.seasun.management.dto;

import com.seasun.management.controller.response.CommonResponse;

public class BaseIdAndNodeIdDto extends CommonResponse {

    private Long id;

    private Long nodeId;


    public BaseIdAndNodeIdDto() {
        super();
    }

    public BaseIdAndNodeIdDto(Integer code, String message) {
        super(code,message);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }
}
