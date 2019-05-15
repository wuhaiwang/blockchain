package com.seasun.management.vo;

import java.util.List;

public class PerformanceWorkGroupInfoAppVo {
    private Long groupId;

    private String groupName;

    private String manager;

    private Integer total;

    private Integer invalided;

    private PerformancePro performancePro;

    private String status;

    private Integer sort;

    private List<MemberPerformanceAppVo> managerPerformanceList;

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

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getInvalided() {
        return invalided;
    }

    public void setInvalided(Integer invalided) {
        this.invalided = invalided;
    }

    public PerformancePro getPerformancePro() {
        return performancePro;
    }

    public void setPerformancePro(PerformancePro performancePro) {
        this.performancePro = performancePro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<MemberPerformanceAppVo> getManagerPerformanceList() {
        return managerPerformanceList;
    }

    public void setManagerPerformanceList(List<MemberPerformanceAppVo> managerPerformanceList) {
        this.managerPerformanceList = managerPerformanceList;
    }

    public static class PerformancePro {

        private Float s;

        private Float a;

        private Float b;

        private Float c;

        private Float sa;

        private Integer sCount;

        private Integer aCount;

        private Integer bCount;

        private Integer cCount;

        public Float getS() {
            return s;
        }

        public void setS(Float s) {
            this.s = s;
        }

        public Float getA() {
            return a;
        }

        public void setA(Float a) {
            this.a = a;
        }

        public Float getB() {
            return b;
        }

        public void setB(Float b) {
            this.b = b;
        }

        public Float getC() {
            return c;
        }

        public void setC(Float c) {
            this.c = c;
        }

        public Float getSa() {
            return sa;
        }

        public void setSa(Float sa) {
            this.sa = sa;
        }

        public Integer getSCount() {
            return sCount;
        }

        public void setSCount(Integer sCount) {
            this.sCount = sCount;
        }

        public Integer getACount() {
            return aCount;
        }

        public void setACount(Integer aCount) {
            this.aCount = aCount;
        }

        public Integer getBCount() {
            return bCount;
        }

        public void setBCount(Integer bCount) {
            this.bCount = bCount;
        }

        public Integer getCCount() {
            return cCount;
        }

        public void setCCount(Integer cCount) {
            this.cCount = cCount;
        }
    }
}
