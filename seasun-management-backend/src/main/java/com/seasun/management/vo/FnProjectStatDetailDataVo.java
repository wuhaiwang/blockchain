package com.seasun.management.vo;

import java.util.ArrayList;
import java.util.List;

public class FnProjectStatDetailDataVo {
    private Long statId;
    private String statName;
    private Float total;
    private List<ValueVo> values;
    private List<FnProjectStatDetailDataVo> children;

    public FnProjectStatDetailDataVo() {
        values = new ArrayList<>();
        children = new ArrayList<>();
    }

    public static class ValueVo{

        private Boolean detailFlag;

        private Float value;

        public Boolean getDetailFlag() {
            return detailFlag;
        }

        public void setDetailFlag(Boolean detailFlag) {
            this.detailFlag = detailFlag;
        }

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }
    }

    public Long getStatId() {
        return statId;
    }

    public void setStatId(Long statId) {
        this.statId = statId;
    }

    public String getStatName() {
        return statName;
    }

    public void setStatName(String statName) {
        this.statName = statName;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public List<ValueVo> getValues() {
        return values;
    }

    public void setValues(List<ValueVo> values) {
        this.values = values;
    }

    public List<FnProjectStatDetailDataVo> getChildren() {
        return children;
    }

    public void setChildren(List<FnProjectStatDetailDataVo> children) {
        this.children = children;
    }
}
