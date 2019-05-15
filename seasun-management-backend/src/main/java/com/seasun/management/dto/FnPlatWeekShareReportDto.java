package com.seasun.management.dto;

import com.seasun.management.model.FnPlatWeekShareConfig;
import com.seasun.management.model.FnWeekShareWorkdayStatus;
import org.apache.poi.ss.usermodel.Workbook;

import java.math.BigDecimal;
import java.util.List;

public class FnPlatWeekShareReportDto{
    private Workbook wb;
    private int rowIndex;
    //查询条件
    private String date;
    private Integer previousYear;//上周所在年
    private Integer previousWeek;//上周
    private Integer currentYear;//当前周所在年
    private Integer currentWeek;//当前周
    private List<Long> platIds;//全部平台id
    private List<Long> artsPlatIds;//美术合并
    private Long zhPlatId;//珠海
    private Long gzPlatId;//广州
    private Long whPlatId;//武汉
    private Long cdPlatId;//成都
    private Long bjPlatId;//北京
    private List<Long> audioPlatIds ;//音频
    private List<Long> technologyPlatIds ;//技术
    private List<Long> qualityPlatIds ;//质量
    private List<Long> testPlatIds ;//测试
    private List<Long> webPlatIds ;//WEB
    private List<Long> operatePlatIds ;//运营

    //返回结果
    private String projectName;
    private BigDecimal proportion;//占比
    private BigDecimal preTotalWorkload;//上周总工作量(人周)
    private BigDecimal totalWorkload;//当前周总工作量(人周)
    private BigDecimal ringRatio;//环比
    private BigDecimal artsWorkload;//美术合并
    private BigDecimal zhWorkload;//珠海
    private BigDecimal gzWorkload;//广州
    private BigDecimal whWorkload;//武汉
    private BigDecimal cdWorkload;//成都
    private BigDecimal bjWorkload;//北京
    private BigDecimal audioWorkload;//音频
    private BigDecimal technologyWorkload;//技术
    private BigDecimal qualityWorkload;//质量
    private BigDecimal testWorkload;//测试
    private BigDecimal webWorkload;//WEB
    private BigDecimal operateWorkload;//运营

    //合并
    private String sumName;//名称合并
    private BigDecimal allTotalWorkload;//周总工作量(人周)总合并
    private BigDecimal allRingRatio;//环比合并
    private BigDecimal allArtsWorkload;//美术总合并
    private BigDecimal zhPeopleNo;//珠海
    private BigDecimal gzPeopleNo;//广州
    private BigDecimal whPeopleNo;//武汉
    private BigDecimal cdPeopleNo;//成都
    private BigDecimal bjPeopleNo;//北京
    private BigDecimal audioPeopleNo;//音频
    private BigDecimal technologyPeopleNo;//技术
    private BigDecimal qualityPeopleNo;//质量
    private BigDecimal testPeopleNo;//测试
    private BigDecimal webPeopleNo;//WEB
    private BigDecimal operatePeopleNo;//运营

    private FnWeekShareWorkdayStatus workdayStatus;

    public Integer getPreviousYear() {
        return previousYear;
    }

    public void setPreviousYear(Integer previousYear) {
        this.previousYear = previousYear;
    }

    public Integer getPreviousWeek() {
        return previousWeek;
    }

    public void setPreviousWeek(Integer previousWeek) {
        this.previousWeek = previousWeek;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(Integer currentWeek) {
        this.currentWeek = currentWeek;
    }

    public List<Long> getPlatIds() {
        return platIds;
    }

    public void setPlatIds(List<Long> platIds) {
        this.platIds = platIds;
    }

    public List<Long> getArtsPlatIds() {
        return artsPlatIds;
    }

    public void setArtsPlatIds(List<Long> artsPlatIds) {
        this.artsPlatIds = artsPlatIds;
    }

    public Long getZhPlatId() {
        return zhPlatId;
    }

    public void setZhPlatId(Long zhPlatId) {
        this.zhPlatId = zhPlatId;
    }

    public Long getGzPlatId() {
        return gzPlatId;
    }

    public void setGzPlatId(Long gzPlatId) {
        this.gzPlatId = gzPlatId;
    }

    public Long getWhPlatId() {
        return whPlatId;
    }

    public void setWhPlatId(Long whPlatId) {
        this.whPlatId = whPlatId;
    }

    public Long getCdPlatId() {
        return cdPlatId;
    }

    public void setCdPlatId(Long cdPlatId) {
        this.cdPlatId = cdPlatId;
    }

    public Long getBjPlatId() {
        return bjPlatId;
    }

    public void setBjPlatId(Long bjPlatId) {
        this.bjPlatId = bjPlatId;
    }

    public List<Long> getAudioPlatIds() {
        return audioPlatIds;
    }

    public void setAudioPlatIds(List<Long> audioPlatIds) {
        this.audioPlatIds = audioPlatIds;
    }

    public List<Long> getTechnologyPlatIds() {
        return technologyPlatIds;
    }

    public void setTechnologyPlatIds(List<Long> technologyPlatIds) {
        this.technologyPlatIds = technologyPlatIds;
    }

    public List<Long> getQualityPlatIds() {
        return qualityPlatIds;
    }

    public void setQualityPlatIds(List<Long> qualityPlatIds) {
        this.qualityPlatIds = qualityPlatIds;
    }

    public List<Long> getTestPlatIds() {
        return testPlatIds;
    }

    public void setTestPlatIds(List<Long> testPlatIds) {
        this.testPlatIds = testPlatIds;
    }

    public List<Long> getWebPlatIds() {
        return webPlatIds;
    }

    public void setWebPlatIds(List<Long> webPlatIds) {
        this.webPlatIds = webPlatIds;
    }

    public List<Long> getOperatePlatIds() {
        return operatePlatIds;
    }

    public void setOperatePlatIds(List<Long> operatePlatIds) {
        this.operatePlatIds = operatePlatIds;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getProportion() {
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }

    public BigDecimal getPreTotalWorkload() {
        return preTotalWorkload;
    }

    public void setPreTotalWorkload(BigDecimal preTotalWorkload) {
        this.preTotalWorkload = preTotalWorkload;
    }

    public BigDecimal getTotalWorkload() {
        return totalWorkload;
    }

    public void setTotalWorkload(BigDecimal totalWorkload) {
        this.totalWorkload = totalWorkload;
    }

    public BigDecimal getRingRatio() {
        return ringRatio;
    }

    public void setRingRatio(BigDecimal ringRatio) {
        this.ringRatio = ringRatio;
    }

    public BigDecimal getArtsWorkload() {
        return artsWorkload;
    }

    public void setArtsWorkload(BigDecimal artsWorkload) {
        this.artsWorkload = artsWorkload;
    }

    public BigDecimal getZhWorkload() {
        return zhWorkload;
    }

    public void setZhWorkload(BigDecimal zhWorkload) {
        this.zhWorkload = zhWorkload;
    }

    public BigDecimal getGzWorkload() {
        return gzWorkload;
    }

    public void setGzWorkload(BigDecimal gzWorkload) {
        this.gzWorkload = gzWorkload;
    }

    public BigDecimal getWhWorkload() {
        return whWorkload;
    }

    public void setWhWorkload(BigDecimal whWorkload) {
        this.whWorkload = whWorkload;
    }

    public BigDecimal getCdWorkload() {
        return cdWorkload;
    }

    public void setCdWorkload(BigDecimal cdWorkload) {
        this.cdWorkload = cdWorkload;
    }

    public BigDecimal getBjWorkload() {
        return bjWorkload;
    }

    public void setBjWorkload(BigDecimal bjWorkload) {
        this.bjWorkload = bjWorkload;
    }

    public BigDecimal getAudioWorkload() {
        return audioWorkload;
    }

    public void setAudioWorkload(BigDecimal audioWorkload) {
        this.audioWorkload = audioWorkload;
    }

    public BigDecimal getTechnologyWorkload() {
        return technologyWorkload;
    }

    public void setTechnologyWorkload(BigDecimal technologyWorkload) {
        this.technologyWorkload = technologyWorkload;
    }

    public BigDecimal getQualityWorkload() {
        return qualityWorkload;
    }

    public void setQualityWorkload(BigDecimal qualityWorkload) {
        this.qualityWorkload = qualityWorkload;
    }

    public BigDecimal getTestWorkload() {
        return testWorkload;
    }

    public void setTestWorkload(BigDecimal testWorkload) {
        this.testWorkload = testWorkload;
    }

    public BigDecimal getWebWorkload() {
        return webWorkload;
    }

    public void setWebWorkload(BigDecimal webWorkload) {
        this.webWorkload = webWorkload;
    }

    public BigDecimal getOperateWorkload() {
        return operateWorkload;
    }

    public void setOperateWorkload(BigDecimal operateWorkload) {
        this.operateWorkload = operateWorkload;
    }

    public BigDecimal getZhPeopleNo() {
        return zhPeopleNo;
    }

    public void setZhPeopleNo(BigDecimal zhPeopleNo) {
        this.zhPeopleNo = zhPeopleNo;
    }

    public BigDecimal getGzPeopleNo() {
        return gzPeopleNo;
    }

    public void setGzPeopleNo(BigDecimal gzPeopleNo) {
        this.gzPeopleNo = gzPeopleNo;
    }

    public BigDecimal getWhPeopleNo() {
        return whPeopleNo;
    }

    public void setWhPeopleNo(BigDecimal whPeopleNo) {
        this.whPeopleNo = whPeopleNo;
    }

    public BigDecimal getCdPeopleNo() {
        return cdPeopleNo;
    }

    public void setCdPeopleNo(BigDecimal cdPeopleNo) {
        this.cdPeopleNo = cdPeopleNo;
    }

    public BigDecimal getBjPeopleNo() {
        return bjPeopleNo;
    }

    public void setBjPeopleNo(BigDecimal bjPeopleNo) {
        this.bjPeopleNo = bjPeopleNo;
    }

    public BigDecimal getAudioPeopleNo() {
        return audioPeopleNo;
    }

    public void setAudioPeopleNo(BigDecimal audioPeopleNo) {
        this.audioPeopleNo = audioPeopleNo;
    }

    public BigDecimal getTechnologyPeopleNo() {
        return technologyPeopleNo;
    }

    public void setTechnologyPeopleNo(BigDecimal technologyPeopleNo) {
        this.technologyPeopleNo = technologyPeopleNo;
    }

    public BigDecimal getQualityPeopleNo() {
        return qualityPeopleNo;
    }

    public void setQualityPeopleNo(BigDecimal qualityPeopleNo) {
        this.qualityPeopleNo = qualityPeopleNo;
    }

    public BigDecimal getTestPeopleNo() {
        return testPeopleNo;
    }

    public void setTestPeopleNo(BigDecimal testPeopleNo) {
        this.testPeopleNo = testPeopleNo;
    }

    public BigDecimal getWebPeopleNo() {
        return webPeopleNo;
    }

    public void setWebPeopleNo(BigDecimal webPeopleNo) {
        this.webPeopleNo = webPeopleNo;
    }

    public BigDecimal getOperatePeopleNo() {
        return operatePeopleNo;
    }

    public void setOperatePeopleNo(BigDecimal operatePeopleNo) {
        this.operatePeopleNo = operatePeopleNo;
    }

    public BigDecimal getAllTotalWorkload() {
        return allTotalWorkload;
    }

    public void setAllTotalWorkload(BigDecimal allTotalWorkload) {
        this.allTotalWorkload = allTotalWorkload;
    }

    public BigDecimal getAllRingRatio() {
        return allRingRatio;
    }

    public void setAllRingRatio(BigDecimal allRingRatio) {
        this.allRingRatio = allRingRatio;
    }

    public BigDecimal getAllArtsWorkload() {
        return allArtsWorkload;
    }

    public void setAllArtsWorkload(BigDecimal allArtsWorkload) {
        this.allArtsWorkload = allArtsWorkload;
    }

    public String getSumName() {
        return sumName;
    }

    public void setSumName(String sumName) {
        this.sumName = sumName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Workbook getWb() {
        return wb;
    }

    public void setWb(Workbook wb) {
        this.wb = wb;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public FnWeekShareWorkdayStatus getWorkdayStatus() {
        return workdayStatus;
    }

    public void setWorkdayStatus(FnWeekShareWorkdayStatus workdayStatus) {
        this.workdayStatus = workdayStatus;
    }
}
