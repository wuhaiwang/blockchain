package com.seasun.management.vo;

public class KsPositionDetailVo {

    public static class Coordinate {
        private Long id;
        private Integer buildingFloorId;
        private Integer buildingCoordinateCategoryId;
        private Integer x;
        private Integer y;
        private Integer z;
        private Integer r;
        private String name;
        private String code;
        private String uniqueValue;
        private String extra;

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

        public Integer getBuildingCoordinateCategoryId() {
            return buildingCoordinateCategoryId;
        }

        public void setBuildingCoordinateCategoryId(Integer buildingCoordinateCategoryId) {
            this.buildingCoordinateCategoryId = buildingCoordinateCategoryId;
        }

        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }

        public Integer getZ() {
            return z;
        }

        public void setZ(Integer z) {
            this.z = z;
        }

        public Integer getR() {
            return r;
        }

        public void setR(Integer r) {
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

        public String getUniqueValue() {
            return uniqueValue;
        }

        public void setUniqueValue(String uniqueValue) {
            this.uniqueValue = uniqueValue;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }
    }

    public static class Floor {
        private Long id;
        private Integer buildingId;
        private String name;
        private String code;
        private String graph;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getBuildingId() {
            return buildingId;
        }

        public void setBuildingId(Integer buildingId) {
            this.buildingId = buildingId;
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

        public String getGraph() {
            return graph;
        }

        public void setGraph(String graph) {
            this.graph = graph;
        }
    }

    private Coordinate coordinate;
    private KsBuildingImageVo.Building building;
    private Floor floor;
    private Boolean self;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public KsBuildingImageVo.Building getBuilding() {
        return building;
    }

    public void setBuilding(KsBuildingImageVo.Building building) {
        this.building = building;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Boolean getSelf() {
        return self;
    }

    public void setSelf(Boolean self) {
        this.self = self;
    }
}
