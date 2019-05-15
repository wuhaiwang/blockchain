package com.seasun.management.vo;

import java.util.List;

public class KsDormitoryChangeRepVo {

    private Integer total;

    private List<KsDormitoryChangeVo> modelSet;

    private Boolean hasNext;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<KsDormitoryChangeVo> getModelSet() {
        return modelSet;
    }

    public void setModelSet(List<KsDormitoryChangeVo> modelSet) {
        this.modelSet = modelSet;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
}
