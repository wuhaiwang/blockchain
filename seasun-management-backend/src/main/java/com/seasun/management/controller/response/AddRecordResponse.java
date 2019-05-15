package com.seasun.management.controller.response;

public class AddRecordResponse extends CommonResponse {
    private Long newId;

    public AddRecordResponse(int code, String message, Long id) {
        super(code, message);

        this.newId = id;
    }

    public AddRecordResponse(Long newId) {
        super();
        this.newId = newId;
    }

    public Long getNewId() {
        return newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }
}
