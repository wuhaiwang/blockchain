package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserDetail {
    private Long id;

    private Long userId;

    private String post;

    private Boolean gender;

    private String workStatus;

    private Integer workAgeOutKs;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date birthday;

    private String certificateType;

    private String certificationNo;

    private String extraPhone;

    private String qq;

    private String emgContactPerson;

    private String emgContactPersonRel;

    private String emgContactPersonTel;

    private String marryFlag;

    private String hobbies;

    private String fax;

    private String msn;

    private String specialSkill;

    private String graduateFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date graduateDate;

    private String major;

    private String job;

    private String joinType;

    private String personalEmail;

    private String postCode;

    private String postAddress;

    private String leaveType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date leaveDate;

    private String topEducation;

    private String topDegree;

    private String nationName;

    private String politicalStatus;

    private String householdType;

    private String englishLevel;

    private String otherLanguageLevel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date firstJoinDate;

    private Long subcompanyId;

    private String belongToCenter;

    private String familyContactPerson;

    private String familyContactPersonRel;

    private String familyContactPersonPlane;

    private String familyContactPersonTel;

    private String nationality;

    private String homeAddress;

    private String currentAddress;

    private String nativePlace;

    private String insurancePlace;

    private String householdPlace;

    private String encouragement;

    public interface Gender{
         Integer male =0;
         Integer female =1;
    }

    public interface WorkStatus {
         String cadet ="实习";
         String regular ="正式";
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date joinPostDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date intershipEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date becomeValidDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post == null ? null : post.trim();
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus == null ? null : workStatus.trim();
    }

    public Integer getWorkAgeOutKs() {
        return workAgeOutKs;
    }

    public void setWorkAgeOutKs(Integer workAgeOutKs) {
        this.workAgeOutKs = workAgeOutKs;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType == null ? null : certificateType.trim();
    }

    public String getCertificationNo() {
        return certificationNo;
    }

    public void setCertificationNo(String certificationNo) {
        this.certificationNo = certificationNo == null ? null : certificationNo.trim();
    }

    public String getExtraPhone() {
        return extraPhone;
    }

    public void setExtraPhone(String extraPhone) {
        this.extraPhone = extraPhone == null ? null : extraPhone.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getEmgContactPerson() {
        return emgContactPerson;
    }

    public void setEmgContactPerson(String emgContactPerson) {
        this.emgContactPerson = emgContactPerson == null ? null : emgContactPerson.trim();
    }

    public String getEmgContactPersonRel() {
        return emgContactPersonRel;
    }

    public void setEmgContactPersonRel(String emgContactPersonRel) {
        this.emgContactPersonRel = emgContactPersonRel == null ? null : emgContactPersonRel.trim();
    }

    public String getEmgContactPersonTel() {
        return emgContactPersonTel;
    }

    public void setEmgContactPersonTel(String emgContactPersonTel) {
        this.emgContactPersonTel = emgContactPersonTel == null ? null : emgContactPersonTel.trim();
    }

    public String getMarryFlag() {
        return marryFlag;
    }

    public void setMarryFlag(String marryFlag) {
        this.marryFlag = marryFlag == null ? null : marryFlag.trim();
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies == null ? null : hobbies.trim();
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn == null ? null : msn.trim();
    }

    public String getSpecialSkill() {
        return specialSkill;
    }

    public void setSpecialSkill(String specialSkill) {
        this.specialSkill = specialSkill == null ? null : specialSkill.trim();
    }

    public String getGraduateFrom() {
        return graduateFrom;
    }

    public void setGraduateFrom(String graduateFrom) {
        this.graduateFrom = graduateFrom == null ? null : graduateFrom.trim();
    }

    public Date getGraduateDate() {
        return graduateDate;
    }

    public void setGraduateDate(Date graduateDate) {
        this.graduateDate = graduateDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major == null ? null : major.trim();
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job == null ? null : job.trim();
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType == null ? null : joinType.trim();
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail == null ? null : personalEmail.trim();
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode == null ? null : postCode.trim();
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress == null ? null : postAddress.trim();
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType == null ? null : leaveType.trim();
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getTopEducation() {
        return topEducation;
    }

    public void setTopEducation(String topEducation) {
        this.topEducation = topEducation == null ? null : topEducation.trim();
    }

    public String getTopDegree() {
        return topDegree;
    }

    public void setTopDegree(String topDegree) {
        this.topDegree = topDegree == null ? null : topDegree.trim();
    }

    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName == null ? null : nationName.trim();
    }

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus == null ? null : politicalStatus.trim();
    }

    public String getHouseholdType() {
        return householdType;
    }

    public void setHouseholdType(String householdType) {
        this.householdType = householdType == null ? null : householdType.trim();
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel == null ? null : englishLevel.trim();
    }

    public String getOtherLanguageLevel() {
        return otherLanguageLevel;
    }

    public void setOtherLanguageLevel(String otherLanguageLevel) {
        this.otherLanguageLevel = otherLanguageLevel == null ? null : otherLanguageLevel.trim();
    }

    public Date getFirstJoinDate() {
        return firstJoinDate;
    }

    public void setFirstJoinDate(Date firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
    }

    public Long getSubcompanyId() {
        return subcompanyId;
    }

    public void setSubcompanyId(Long subcompanyId) {
        this.subcompanyId = subcompanyId;
    }

    public String getBelongToCenter() {
        return belongToCenter;
    }

    public void setBelongToCenter(String belongToCenter) {
        this.belongToCenter = belongToCenter == null ? null : belongToCenter.trim();
    }

    public String getFamilyContactPerson() {
        return familyContactPerson;
    }

    public void setFamilyContactPerson(String familyContactPerson) {
        this.familyContactPerson = familyContactPerson == null ? null : familyContactPerson.trim();
    }

    public String getFamilyContactPersonRel() {
        return familyContactPersonRel;
    }

    public void setFamilyContactPersonRel(String familyContactPersonRel) {
        this.familyContactPersonRel = familyContactPersonRel == null ? null : familyContactPersonRel.trim();
    }

    public String getFamilyContactPersonPlane() {
        return familyContactPersonPlane;
    }

    public void setFamilyContactPersonPlane(String familyContactPersonPlane) {
        this.familyContactPersonPlane = familyContactPersonPlane == null ? null : familyContactPersonPlane.trim();
    }

    public String getFamilyContactPersonTel() {
        return familyContactPersonTel;
    }

    public void setFamilyContactPersonTel(String familyContactPersonTel) {
        this.familyContactPersonTel = familyContactPersonTel == null ? null : familyContactPersonTel.trim();
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality == null ? null : nationality.trim();
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress == null ? null : homeAddress.trim();
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress == null ? null : currentAddress.trim();
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace == null ? null : nativePlace.trim();
    }

    public String getInsurancePlace() {
        return insurancePlace;
    }

    public void setInsurancePlace(String insurancePlace) {
        this.insurancePlace = insurancePlace == null ? null : insurancePlace.trim();
    }

    public String getHouseholdPlace() {
        return householdPlace;
    }

    public void setHouseholdPlace(String householdPlace) {
        this.householdPlace = householdPlace == null ? null : householdPlace.trim();
    }

    public String getEncouragement() {
        return encouragement;
    }

    public void setEncouragement(String encouragement) {
        this.encouragement = encouragement == null ? null : encouragement.trim();
    }

    public Date getJoinPostDate() {
        return joinPostDate;
    }

    public void setJoinPostDate(Date joinPostDate) {
        this.joinPostDate = joinPostDate;
    }

    public Date getIntershipEndDate() {
        return intershipEndDate;
    }

    public void setIntershipEndDate(Date intershipEndDate) {
        this.intershipEndDate = intershipEndDate;
    }

    public Date getBecomeValidDate() {
        return becomeValidDate;
    }

    public void setBecomeValidDate(Date becomeValidDate) {
        this.becomeValidDate = becomeValidDate;
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
}