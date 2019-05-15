package com.seasun.management.controller.response;

public class AttachmentResponse extends  FileResponse {

    private Long id;

    private Long attachmentId;

    public AttachmentResponse(int code, String url, Long id, Long attachmentId) {
        super(code, url);
        this.id = id;
        this.attachmentId = attachmentId;
    }

    public AttachmentResponse(int code, String url) {
        super(code, url);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
