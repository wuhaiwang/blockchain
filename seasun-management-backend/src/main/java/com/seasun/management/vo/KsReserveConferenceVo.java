package com.seasun.management.vo;

import java.util.List; 

public class KsReserveConferenceVo {

    private Integer total;
    private List<ReserveConference> modelSet;
    private Boolean hasNext;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ReserveConference> getModelSet() {
        return modelSet;
    }

    public void setModelSet(List<ReserveConference> modelSet) {
        this.modelSet = modelSet;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public static class ReserveConference{
        private Long id;
        private Long roomId;
        private String roomLocation;
        private Long userId;
        private String roomName;
        private String meetingTitle;
        private String userName;
        private String company;
        private String userEmail;
        private String startTime;
        private String endTIme;
        private Boolean end;
        private Boolean start;
        private Boolean signed;
        private String state;
        private String bookingState;
        private String fromTime;
        private String toTime;
        private String date;
        private String participantCount;
        private Integer timeIndex;
        private Integer timeLength;
        private Boolean self;
        private Integer signedCount;
        private String level;
        private List<String> deviceRequirements;
        private String remark;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getRoomId() {
            return roomId;
        }

        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }

        public String getRoomLocation() {
            return roomLocation;
        }

        public void setRoomLocation(String roomLocation) {
            this.roomLocation = roomLocation;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getMeetingTitle() {
            return meetingTitle;
        }

        public void setMeetingTitle(String meetingTitle) {
            this.meetingTitle = meetingTitle;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTIme() {
            return endTIme;
        }

        public void setEndTIme(String endTIme) {
            this.endTIme = endTIme;
        }

        public Boolean getEnd() {
            return end;
        }

        public void setEnd(Boolean end) {
            this.end = end;
        }

        public Boolean getStart() {
            return start;
        }

        public void setStart(Boolean start) {
            this.start = start;
        }

        public Boolean getSigned() {
            return signed;
        }

        public void setSigned(Boolean signed) {
            this.signed = signed;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getBookingState() {
            return bookingState;
        }

        public void setBookingState(String bookingState) {
            this.bookingState = bookingState;
        }

        public String getFromTime() {
            return fromTime;
        }

        public void setFromTime(String fromTime) {
            this.fromTime = fromTime;
        }

        public String getToTime() {
            return toTime;
        }

        public void setToTime(String toTime) {
            this.toTime = toTime;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getParticipantCount() {
            return participantCount;
        }

        public void setParticipantCount(String participantCount) {
            this.participantCount = participantCount;
        }

        public Integer getTimeIndex() {
            return timeIndex;
        }

        public void setTimeIndex(Integer timeIndex) {
            this.timeIndex = timeIndex;
        }

        public Integer getTimeLength() {
            return timeLength;
        }

        public void setTimeLength(Integer timeLength) {
            this.timeLength = timeLength;
        }

        public Boolean getSelf() {
            return self;
        }

        public void setSelf(Boolean self) {
            this.self = self;
        }

        public Integer getSignedCount() {
            return signedCount;
        }

        public void setSignedCount(Integer signedCount) {
            this.signedCount = signedCount;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public List<String> getDeviceRequirements() {
            return deviceRequirements;
        }

        public void setDeviceRequirements(List<String> deviceRequirements) {
            this.deviceRequirements = deviceRequirements;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
