package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class Project {

    public interface ServiceLine {
        String plat = "平台";
        String project = "项目";
        String summary = "汇总";
        String share = "分摊项";
        String release = "发行";
    }

    public interface Status {
        String developing = "在研";
        String operating = "运营";
    }

    public interface Type {
        String zsfc = "暂时封存";
        String daiLi = "自研";
        String ziYan = "代理";
    }

    public interface AppShowMode {
        Integer none = 0;
        Integer all = 1;
        Integer project = 2;
        Integer hr = 3;
        Integer finance = 4;
        Integer financeAndProject = 5;
    }

    public interface Id {
        Long JX3 = 1029L;
        // 西山居世游
        Long SEASUN_WORLD_GAME = 5163L;
        // 平台-公司，虚拟项目
        Long PLAT_COMPANY = 100L;
        // 珠海技术中心(平台)
        Long TECGNOLOGY_CENTER = 5045L;
        // 珠海技术中心(分摊项)
        Long TECGNOLOGY_CENTER_SHARE_PROJECT = 5024L;
        // 珠海美术中心(平台)
        Long ART_CENTER = 1081L;
        // 珠海美术中心(分摊项)
        Long ART_CENTER_SHARE_PROJECT = 5025L;
        // 珠海质量中心(平台)
        Long QUALITY_CENTER = 1086L;
        // 珠海质量中心(分摊项)
        Long QUALITY_CENTER_SHARE_PROJECT = 5026L;
        // 珠海运营中心(平台)
        Long OPERATION_CENTER = 1080L;
        // 珠海运营中心(分摊项)
        Long OPERATION_CENTER_SHARE_PROJECT = 5027L;
        // 珠海web研发中心(平台)
        Long WEB_DEVLOPMENT_CENTER = 1244L;
        // 珠海web研发中心(分摊项)
        Long WEB_DEVLOPMENT_CENTER_SHARE_PROJECT = 5113L;
        // 北京世游技术中心
        Long BEIJING_WORLDGAME_TECGNOLOGY_CENTER = 1210L;

    }

    public interface Name {
        // 平台-公司，虚拟项目
        String PLAT_COMPANY = "平台-公司";
    }

    public interface City {
        String WUHAN = "武汉";
        String DALIAN = "大连";
        String GUANGZHOU = "广州";
        String BEIJING = "北京";
        String ZHUHAI = "珠海";
        String CHENGDU = "成都";
        String SEASUN = "大西山居";
    }

    private Long id;

    private String name;

    private String shortName;

    private String enShortName;

    private String logo;

    private String status;

    private String type;

    private String serviceLine;

    private String description;

    private String contactName;

    private Boolean activeFlag;

    private Boolean virtualFlag;

    private Boolean shareFlag;

    private Boolean financeFlag;

    private Boolean hrFlag;

    private Boolean orgFlag;

    private Long parentShareId;

    private Long parentHrId;

    private Long parentFnSumId;

    private String hrList;

    private String city;

    private Integer appShowMode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date establishTime;

    private Integer maxMember;

    private Long workGroupId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //  the following are user defined...
    private List<OrderCenter> orders;

    private List<ProjectUsedName> usedNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName == null ? null : shortName.trim();
    }

    public String getEnShortName() {
        return enShortName;
    }

    public void setEnShortName(String enShortName) {
        this.enShortName = enShortName == null ? null : enShortName.trim();
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getServiceLine() {
        return serviceLine;
    }

    public void setServiceLine(String serviceLine) {
        this.serviceLine = serviceLine == null ? null : serviceLine.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Boolean getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Boolean virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Boolean getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(Boolean shareFlag) {
        this.shareFlag = shareFlag;
    }

    public Boolean getHrFlag() {
        return hrFlag;
    }

    public void setHrFlag(Boolean hrFlag) {
        this.hrFlag = hrFlag;
    }

    public Boolean getOrgFlag() {
        return orgFlag;
    }

    public void setOrgFlag(Boolean orgFlag) {
        this.orgFlag = orgFlag;
    }

    public String getHrList() {
        return hrList;
    }

    public void setHrList(String hrList) {
        this.hrList = hrList == null ? null : hrList.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<OrderCenter> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderCenter> orders) {
        this.orders = orders;
    }

    public List<ProjectUsedName> getUsedNames() {
        return usedNames;
    }

    public void setUsedNames(List<ProjectUsedName> usedNames) {
        this.usedNames = usedNames;
    }

    public Boolean getFinanceFlag() {
        return financeFlag;
    }

    public void setFinanceFlag(Boolean financeFlag) {
        this.financeFlag = financeFlag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getParentShareId() {
        return parentShareId;
    }

    public void setParentShareId(Long parentShareId) {
        this.parentShareId = parentShareId;
    }

    public Integer getAppShowMode() {
        return appShowMode;
    }

    public void setAppShowMode(Integer appShowMode) {
        this.appShowMode = appShowMode;
    }

    public Date getEstablishTime() {
        return establishTime;
    }

    public void setEstablishTime(Date establishTime) {
        this.establishTime = establishTime;
    }

    public Integer getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Long getParentHrId() {
        return parentHrId;
    }

    public void setParentHrId(Long parentHrId) {
        this.parentHrId = parentHrId;
    }

    public Long getParentFnSumId() {
        return parentFnSumId;
    }

    public void setParentFnSumId(Long parentFnSumId) {
        this.parentFnSumId = parentFnSumId;
    }
}