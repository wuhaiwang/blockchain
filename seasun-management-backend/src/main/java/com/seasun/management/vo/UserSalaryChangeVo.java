package com.seasun.management.vo;


import com.seasun.management.dto.UserSalaryChangeDto;

import java.util.List;

public class UserSalaryChangeVo {
    private Profile profile;
    private List<UserSalaryChangeDto> salaryChangeList;

    public static class Profile {
        private Integer total;
        private Integer salaryIncreaseNumber;
        private Long salaryIncreaseAmount;
        private Integer waitingForCut;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getSalaryIncreaseNumber() {
            return salaryIncreaseNumber;
        }

        public void setSalaryIncreaseNumber(Integer salaryIncreaseNumber) {
            this.salaryIncreaseNumber = salaryIncreaseNumber;
        }

        public Long getSalaryIncreaseAmount() {
            return salaryIncreaseAmount;
        }

        public void setSalaryIncreaseAmount(Long salaryIncreaseAmount) {
            this.salaryIncreaseAmount = salaryIncreaseAmount;
        }

        public Integer getWaitingForCut() {
            return waitingForCut;
        }

        public void setWaitingForCut(Integer waitingForCut) {
            this.waitingForCut = waitingForCut;
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<UserSalaryChangeDto> getSalaryChangeList() {
        return salaryChangeList;
    }

    public void setSalaryChangeList(List<UserSalaryChangeDto> salaryChangeList) {
        this.salaryChangeList = salaryChangeList;
    }
}
