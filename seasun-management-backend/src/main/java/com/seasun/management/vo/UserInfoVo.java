package com.seasun.management.vo;

import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class UserInfoVo {

    public static class BaseInfoVo {

        private List<CityInfoVo> citys;

        private List<IdNameBaseObject> workGroups;

        private List<IdNameBaseObject> projects;

        public List<CityInfoVo> getCitys() {
            return citys;
        }

        public void setCitys(List<CityInfoVo> citys) {
            this.citys = citys;
        }

        public List<IdNameBaseObject> getWorkGroups() {
            return workGroups;
        }

        public void setWorkGroups(List<IdNameBaseObject> workGroups) {
            this.workGroups = workGroups;
        }

        public List<IdNameBaseObject> getProjects() {
            return projects;
        }

        public void setProjects(List<IdNameBaseObject> projects) {
            this.projects = projects;
        }
    }



    public static class CityInfoVo {

        private String name;

        private List<Long> subcompanyIds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Long> getSubcompanyIds() {
            return subcompanyIds;
        }

        public void setSubcompanyIds(List<Long> subcompanyIds) {
            this.subcompanyIds = subcompanyIds;
        }
    }

}
