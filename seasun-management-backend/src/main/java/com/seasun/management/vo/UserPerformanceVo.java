package com.seasun.management.vo;

import com.seasun.management.dto.PerformanceDto;
import com.seasun.management.dto.UserPerformanceDetailDto;
import com.seasun.management.model.UserPerformance;

import java.util.List;


public class UserPerformanceVo extends UserPerformance{

    private String loginId;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    private UserProfileVo profile;

    private List<PerformanceDto> performanceList;

    public UserProfileVo getProfile() {
        return profile;
    }

    public void setProfile(UserProfileVo profile) {
        this.profile = profile;
    }

    public List<PerformanceDto> getPerformanceList() {
        return performanceList;
    }

    public void setPerformanceList(List<PerformanceDto> performanceList) {
        this.performanceList = performanceList;
    }

    public static class UserProfileVo {
        private Integer total;
        private Integer waitingForCommit;
        private Integer waitingForMark;
        private PerformancePro performancePro;


        public static class PerformancePro {
            private Float S;
            private Float A;
            private Float B;
            private Float C;
            private Float D;

            public Float getS() {
                return S;
            }

            public void setS(Float s) {
                this.S = s;
            }

            public Float getA() {
                return A;
            }

            public void setA(Float a) {
                this.A = a;
            }

            public Float getB() {
                return B;
            }

            public void setB(Float b) {
                this.B = b;
            }

            public Float getC() {
                return C;
            }

            public void setC(Float c) {
                this.C = c;
            }

            public Float getD() {
                return D;
            }

            public void setD(Float d) {
                this.D = d;
            }

        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getWaitingForCommit() {
            return waitingForCommit;
        }

        public void setWaitingForCommit(Integer waitingForCommit) {
            this.waitingForCommit = waitingForCommit;
        }

        public Integer getWaitingForMark() {
            return waitingForMark;
        }

        public void setWaitingForMark(Integer waitingForMark) {
            this.waitingForMark = waitingForMark;
        }

        public PerformancePro getPerformancePro() {
            return performancePro;
        }

        public void setPerformancePro(PerformancePro performancePro) {
            this.performancePro = performancePro;
        }
    }

}
