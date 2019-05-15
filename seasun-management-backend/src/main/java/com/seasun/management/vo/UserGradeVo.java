package com.seasun.management.vo;

import com.seasun.management.dto.UserGradeDto;
import com.seasun.management.dto.WorkGroupUserDto;

import java.util.List;

public class UserGradeVo {
    private Profile profile;
    private List<UserGradeDto> managerMemberList;
    private SubWorkGroupGrade subGroup;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<UserGradeDto> getManagerMemberList() {
        return managerMemberList;
    }

    public void setManagerMemberList(List<UserGradeDto> managerMemberList) {
        this.managerMemberList = managerMemberList;
    }

    public SubWorkGroupGrade getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(SubWorkGroupGrade subGroup) {
        this.subGroup = subGroup;
    }

    public static class Profile {
        private float T1;
        private int T1Count;
        private float T2;
        private int T2Count;
        private float T3;
        private int T3Count;
        private float T4;
        private int T4Count;
        private float T5;
        private int T5Count;
        private float T6;
        private int T6Count;
        private float T7;
        private int T7Count;
        private int total;

        public float getT1() {
            return T1;
        }

        public void setT1(float t1) {
            T1 = t1;
        }

        public int getT1Count() {
            return T1Count;
        }

        public void setT1Count(int t1Count) {
            T1Count = t1Count;
        }

        public float getT2() {
            return T2;
        }

        public void setT2(float t2) {
            T2 = t2;
        }

        public int getT2Count() {
            return T2Count;
        }

        public void setT2Count(int t2Count) {
            T2Count = t2Count;
        }

        public float getT3() {
            return T3;
        }

        public void setT3(float t3) {
            T3 = t3;
        }

        public int getT3Count() {
            return T3Count;
        }

        public void setT3Count(int t3Count) {
            T3Count = t3Count;
        }

        public float getT4() {
            return T4;
        }

        public void setT4(float t4) {
            T4 = t4;
        }

        public int getT4Count() {
            return T4Count;
        }

        public void setT4Count(int t4Count) {
            T4Count = t4Count;
        }

        public float getT5() {
            return T5;
        }

        public void setT5(float t5) {
            T5 = t5;
        }

        public int getT5Count() {
            return T5Count;
        }

        public void setT5Count(int t5Count) {
            T5Count = t5Count;
        }

        public float getT6() {
            return T6;
        }

        public void setT6(float t6) {
            T6 = t6;
        }

        public int getT6Count() {
            return T6Count;
        }

        public void setT6Count(int t6Count) {
            T6Count = t6Count;
        }

        public float getT7() {
            return T7;
        }

        public void setT7(float t7) {
            T7 = t7;
        }

        public int getT7Count() {
            return T7Count;
        }

        public void setT7Count(int t7Count) {
            T7Count = t7Count;
        }

        public void calculate() {
            if (getT1Count() + getT2Count() + getT3Count() + getT4Count() > 0) {
                setT1(getT1Count() * 100F / (getTotal()));
                setT2(getT2Count() * 100F / (getTotal()));
                setT3(getT3Count() * 100F / (getTotal()));
                setT4(getT4Count() * 100F / (getTotal()));
                setT5(getT5Count() * 100F / (getTotal()));
                setT6(getT6Count() * 100F / (getTotal()));
                setT7(getT7Count() * 100F / (getTotal()));
            }
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class WorkGroupGrade extends Profile {
        private Long groupId;
        private String groupName;
        private int memberCount;

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }
    }

    public static class SubWorkGroupGrade {
        private int total;
        private int memberCount;
        private List<WorkGroupGrade> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public List<WorkGroupGrade> getData() {
            return data;
        }

        public void setData(List<WorkGroupGrade> data) {
            this.data = data;
        }
    }
}
