package com.seasun.management.dto;

import com.seasun.management.model.Floor;

public class FloorDto extends Floor {

    private Long total;

    private String assistantName;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }
}
