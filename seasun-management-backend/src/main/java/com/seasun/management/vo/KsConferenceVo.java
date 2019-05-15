package com.seasun.management.vo;

import java.util.List;

public class KsConferenceVo {

    private Integer total;

    private List<Room> modelSet;

    private Boolean hasNext;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


    public List<Room> getModelSet() {
        return modelSet;
    }

    public void setModelSet(List<Room> modelSet) {
        this.modelSet = modelSet;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public static class Room{
        private Long id;
        private String state;
        private String roomNo;
        private Long roomId;
        private String roomName;
        private String needApprove;
        private String qrCode;
        private String location;
        private String canBook;
        private String common;
        private String company;
        private String simpleRoomNo;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getRoomNo() {
            return roomNo;
        }

        public void setRoomNo(String roomNo) {
            this.roomNo = roomNo;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getNeedApprove() {
            return needApprove;
        }

        public void setNeedApprove(String needApprove) {
            this.needApprove = needApprove;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCanBook() {
            return canBook;
        }

        public void setCanBook(String canBook) {
            this.canBook = canBook;
        }

        public String getCommon() {
            return common;
        }

        public void setCommon(String common) {
            this.common = common;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getSimpleRoomNo() {
            return simpleRoomNo;
        }

        public void setSimpleRoomNo(String simpleRoomNo) {
            this.simpleRoomNo = simpleRoomNo;
        }

        public Long getRoomId() {
            return roomId;
        }

        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }
    }
}
