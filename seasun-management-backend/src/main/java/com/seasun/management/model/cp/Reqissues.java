package com.seasun.management.model.cp;

import java.util.Date;

public class Reqissues {
    private Integer id;

    private String briefDescription;

    private String fullDescription;

    private Integer createdBy;

    private Integer closedBy;

    private String assignedTo;

    private Date createdOn;

    private Integer type;

    private Integer severity;

    private Integer priority;

    private Integer resolutionOutcome;

    private String comment;

    private Integer status;

    private String attachments;

    private Integer project;

    private Date closeDate;

    private String duplicateIssueId;

    private String lastAssignedTo;

    private Float actovotyRatio;

    private Integer resolvedBy;

    private Integer lastModifiedBy;

    private Date lastModifiedOn;

    private Date resolvedOn;

    private String exProps;

    private Date eTA;

    private Integer subSystem;

    private String approvals;

    private Integer parentId;

    private Integer owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription == null ? null : briefDescription.trim();
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription == null ? null : fullDescription.trim();
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Integer closedBy) {
        this.closedBy = closedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo == null ? null : assignedTo.trim();
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getResolutionOutcome() {
        return resolutionOutcome;
    }

    public void setResolutionOutcome(Integer resolutionOutcome) {
        this.resolutionOutcome = resolutionOutcome;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments == null ? null : attachments.trim();
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getDuplicateIssueId() {
        return duplicateIssueId;
    }

    public void setDuplicateIssueId(String duplicateIssueId) {
        this.duplicateIssueId = duplicateIssueId == null ? null : duplicateIssueId.trim();
    }

    public String getLastAssignedTo() {
        return lastAssignedTo;
    }

    public void setLastAssignedTo(String lastAssignedTo) {
        this.lastAssignedTo = lastAssignedTo == null ? null : lastAssignedTo.trim();
    }

    public Float getActovotyRatio() {
        return actovotyRatio;
    }

    public void setActovotyRatio(Float actovotyRatio) {
        this.actovotyRatio = actovotyRatio;
    }

    public Integer getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(Integer resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public Integer getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Integer lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public Date getResolvedOn() {
        return resolvedOn;
    }

    public void setResolvedOn(Date resolvedOn) {
        this.resolvedOn = resolvedOn;
    }

    public String getExProps() {
        return exProps;
    }

    public void setExProps(String exProps) {
        this.exProps = exProps == null ? null : exProps.trim();
    }

    public Date geteTA() {
        return eTA;
    }

    public void seteTA(Date eTA) {
        this.eTA = eTA;
    }

    public Integer getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(Integer subSystem) {
        this.subSystem = subSystem;
    }

    public String getApprovals() {
        return approvals;
    }

    public void setApprovals(String approvals) {
        this.approvals = approvals == null ? null : approvals.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }
}