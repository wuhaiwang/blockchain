package com.seasun.management.vo;

import java.util.LinkedHashMap;
import java.util.List;

public class ConferenceVo {

    private Long id;
    private String roomName;
    private String location;
    private List<ConferenceRange> conferenceRanges;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ConferenceRange> getConferenceRanges() {
        return conferenceRanges;
    }

    public void setConferenceRanges(List<ConferenceRange> conferenceRanges) {
        this.conferenceRanges = conferenceRanges;
    }

    public static class ConferenceRange{
        private Boolean canReserveFlag;
        private String fromTime;
        private String toTime;
        private String state;
        private String userName;
        private String level;

        public Boolean getCanReserveFlag() {
            return canReserveFlag;
        }

        public void setCanReserveFlag(Boolean canReserveFlag) {
            this.canReserveFlag = canReserveFlag;
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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public ConferenceRange(Boolean canReserveFlag, String fromTime, String toTime, String state, String userName, String level) {
            this.canReserveFlag = canReserveFlag;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.state = state;
            this.userName = userName;
            this.level = level;
        }
        public ConferenceRange(Boolean canReserveFlag, String fromTime, String toTime) {
            this.canReserveFlag = canReserveFlag;
            this.fromTime = fromTime;
            this.toTime = toTime;
        }

        public ConferenceRange(){}
    }
}
