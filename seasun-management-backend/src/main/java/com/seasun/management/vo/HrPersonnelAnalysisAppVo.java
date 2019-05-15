package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class HrPersonnelAnalysisAppVo {
    private JSONObject requestParam;

    private List<PersonnelAnalysis> age;

    private List<PersonnelAnalysis> companyAge;

    private List<PersonnelAnalysis> leavingCompanyAge;

    public JSONObject getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(JSONObject requestParam) {
        this.requestParam = requestParam;
    }

    public List<PersonnelAnalysis> getAge() {
        return age;
    }

    public void setAge(List<PersonnelAnalysis> age) {
        this.age = age;
    }

    public List<PersonnelAnalysis> getCompanyAge() {
        return companyAge;
    }

    public void setCompanyAge(List<PersonnelAnalysis> companyAge) {
        this.companyAge = companyAge;
    }

    public List<PersonnelAnalysis> getLeavingCompanyAge() {
        return leavingCompanyAge;
    }

    public void setLeavingCompanyAge(List<PersonnelAnalysis> leavingCompanyAge) {
        this.leavingCompanyAge = leavingCompanyAge;
    }

    public static class PersonnelAnalysis {

        private String name;

        private Integer count;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
