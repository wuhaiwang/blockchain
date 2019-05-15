package com.seasun.management.service;

import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.vo.FnProjectStatDataVo;
import com.seasun.management.vo.FnProjectStatDetailDataVo;

import java.util.List;

public interface FnProjectStatDataService {
    List<FnProjectStatDataVo> getFnProjectStatDataVosByCondition(FnProjectStatData fnProjectStatData);

    void addProjectStatData(FnProjectStatData fnProjectStatData);

    void updateProjectStatData(FnProjectStatData fnProjectStatData);

    void batchUpdateProjectStatData(List<FnProjectStatData> fnProjectStatData);

    List<FnProjectStatDetailDataVo> getFnProjectStatMapDataVosByCondition(FnProjectStatData fnProjectStatData);
}
