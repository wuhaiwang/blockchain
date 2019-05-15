package com.seasun.management.service;

import com.seasun.management.model.FnProjectSchedData;
import com.seasun.management.vo.FnProjectSchedDataVo;

import java.util.List;

public interface FnProjectSchedDataService {
    List<FnProjectSchedDataVo> getFnProjectSchedDataByCondition(FnProjectSchedData fnProjectSchedData);

    void batchUpdateProjectSchedData(List<FnProjectSchedData> fnProjectSchedData);

    void updateProjectSchedData(FnProjectSchedData fnProjectSchedData);
}
