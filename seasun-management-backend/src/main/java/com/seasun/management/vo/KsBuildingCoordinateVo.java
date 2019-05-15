package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

public class KsBuildingCoordinateVo {

    private Integer total;

    private Set<Model> modelSet;

    private Boolean hasNext;

    private static class Model{
        private Long id;
        private Integer buildingFloorId;
        private String category;
        private String categoryCode;
        private String x;
        private String y;
        private String z;
        private String r;
        private String name;
        private String code;
        private String extra;
        private String floorName;
        private String floorCode;
        private String floorGraph;
        private String buildingName;
        private String buildingCode;
        private JSONObject detailInfo;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getBuildingFloorId() {
            return buildingFloorId;
        }

        public void setBuildingFloorId(Integer buildingFloorId) {
            this.buildingFloorId = buildingFloorId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryCode() {
            return categoryCode;
        }

        public void setCategoryCode(String categoryCode) {
            this.categoryCode = categoryCode;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getZ() {
            return z;
        }

        public void setZ(String z) {
            this.z = z;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getFloorName() {
            return floorName;
        }

        public void setFloorName(String floorName) {
            this.floorName = floorName;
        }

        public String getFloorCode() {
            return floorCode;
        }

        public void setFloorCode(String floorCode) {
            this.floorCode = floorCode;
        }

        public String getFloorGraph() {
            return floorGraph;
        }

        public void setFloorGraph(String floorGraph) {
            this.floorGraph = floorGraph;
        }

        public String getBuildingName() {
            return buildingName;
        }

        public void setBuildingName(String buildingName) {
            this.buildingName = buildingName;
        }

        public String getBuildingCode() {
            return buildingCode;
        }

        public void setBuildingCode(String buildingCode) {
            this.buildingCode = buildingCode;
        }

        public JSONObject getDetailInfo() {
            return detailInfo;
        }

        public void setDetailInfo(JSONObject detailInfo) {
            this.detailInfo = detailInfo;
        }
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Set<Model> getModelSet() {
        return modelSet;
    }

    public void setModelSet(Set<Model> modelSet) {
        this.modelSet = modelSet;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
}
