package com.seasun.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class UserDetailBaseInfoDto implements Serializable {

    private Long id;

    private Long orderCenterId;

    private Long costCenterId;

    private Long workGroupId;

    private Long perfWorkGroupId;

    private Long employeeNo;

    private String middleName;

    private String firstName;

    private String lastName;

    private String loginId;

    private Long floorId;

    private String label;

    private String photo;

    private boolean photoStatus;

    private String email;

    private String workPlace;

    private Boolean activeFlag;

    private String phone;

    private String fullName;

    private String post;

    private String gender;

    private String workStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date graduateDate;

    private String major;

    private String job;

    private String joinType;

    private String personalEmail;

    private String postCode;

    private String postAddress;

    private String leaveType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveDate;

    private String topEducation;

    private String topDegree;

    private String nationName;

    private String politicalStatus;

    private String householdType;

    private String englishLevel;

    private String otherLanguageLevel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date firstJoinDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inDate;

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

    private Integer workAge;

    private Integer workAgeInKs;

    public interface Gender{
        Integer male =0;
        Integer female =1;
    }

    public interface WorkStatus {
        String cadet ="实习";
        String regular ="正式";
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date joinPostDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date intershipEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date becomeValidDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderCenterId() {
        return orderCenterId;
    }

    public void setOrderCenterId(Long orderCenterId) {
        this.orderCenterId = orderCenterId;
    }

    public Long getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Long costCenterId) {
        this.costCenterId = costCenterId;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Long getPerfWorkGroupId() {
        return perfWorkGroupId;
    }

    public void setPerfWorkGroupId(Long perfWorkGroupId) {
        this.perfWorkGroupId = perfWorkGroupId;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(boolean photoStatus) {
        this.photoStatus = photoStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
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
        this.certificateType = certificateType;
    }

    public String getCertificationNo() {
        return certificationNo;
    }

    public void setCertificationNo(String certificationNo) {
        this.certificationNo = certificationNo;
    }

    public String getExtraPhone() {
        return extraPhone;
    }

    public void setExtraPhone(String extraPhone) {
        this.extraPhone = extraPhone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmgContactPerson() {
        return emgContactPerson;
    }

    public void setEmgContactPerson(String emgContactPerson) {
        this.emgContactPerson = emgContactPerson;
    }

    public String getEmgContactPersonRel() {
        return emgContactPersonRel;
    }

    public void setEmgContactPersonRel(String emgContactPersonRel) {
        this.emgContactPersonRel = emgContactPersonRel;
    }

    public String getEmgContactPersonTel() {
        return emgContactPersonTel;
    }

    public void setEmgContactPersonTel(String emgContactPersonTel) {
        this.emgContactPersonTel = emgContactPersonTel;
    }

    public String getMarryFlag() {
        return marryFlag;
    }

    public void setMarryFlag(String marryFlag) {
        this.marryFlag = marryFlag;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getSpecialSkill() {
        return specialSkill;
    }

    public void setSpecialSkill(String specialSkill) {
        this.specialSkill = specialSkill;
    }

    public String getGraduateFrom() {
        return graduateFrom;
    }

    public void setGraduateFrom(String graduateFrom) {
        this.graduateFrom = graduateFrom;
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
        this.major = major;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
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
        this.topEducation = topEducation;
    }

    public String getTopDegree() {
        return topDegree;
    }

    public void setTopDegree(String topDegree) {
        this.topDegree = topDegree;
    }

    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getHouseholdType() {
        return householdType;
    }

    public void setHouseholdType(String householdType) {
        this.householdType = householdType;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }

    public String getOtherLanguageLevel() {
        return otherLanguageLevel;
    }

    public void setOtherLanguageLevel(String otherLanguageLevel) {
        this.otherLanguageLevel = otherLanguageLevel;
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
        this.belongToCenter = belongToCenter;
    }

    public String getFamilyContactPerson() {
        return familyContactPerson;
    }

    public void setFamilyContactPerson(String familyContactPerson) {
        this.familyContactPerson = familyContactPerson;
    }

    public String getFamilyContactPersonRel() {
        return familyContactPersonRel;
    }

    public void setFamilyContactPersonRel(String familyContactPersonRel) {
        this.familyContactPersonRel = familyContactPersonRel;
    }

    public String getFamilyContactPersonPlane() {
        return familyContactPersonPlane;
    }

    public void setFamilyContactPersonPlane(String familyContactPersonPlane) {
        this.familyContactPersonPlane = familyContactPersonPlane;
    }

    public String getFamilyContactPersonTel() {
        return familyContactPersonTel;
    }

    public void setFamilyContactPersonTel(String familyContactPersonTel) {
        this.familyContactPersonTel = familyContactPersonTel;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getInsurancePlace() {
        return insurancePlace;
    }

    public void setInsurancePlace(String insurancePlace) {
        this.insurancePlace = insurancePlace;
    }

    public String getHouseholdPlace() {
        return householdPlace;
    }

    public void setHouseholdPlace(String householdPlace) {
        this.householdPlace = householdPlace;
    }

    public String getEncouragement() {
        return encouragement;
    }

    public void setEncouragement(String encouragement) {
        this.encouragement = encouragement;
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

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Integer getWorkAge() {
        return workAge;
    }

    public void setWorkAge(Integer workAge) {
        this.workAge = workAge;
    }

    public Integer getWorkAgeInKs() {
        return workAgeInKs;
    }

    public void setWorkAgeInKs(Integer workAgeInKs) {
        this.workAgeInKs = workAgeInKs;
    }
}
