package com.seasun.management.service;

import com.seasun.management.dto.PmPlanDetailDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PmFinanceDetail;
import com.seasun.management.model.PmPlanDetail;
import com.seasun.management.vo.PmFinanceDetailVo;
import com.seasun.management.vo.PmPlanDetailAppVo;
import com.seasun.management.vo.PmPlanInfoVo;

import java.util.List;

public interface PmPlanService {

    PmPlanInfoVo getPmPlanInfo(Long projectId);

    PmPlanDetailDto insertPmPlanDetail(PmPlanDetailDto pmPlanDetailDto);

    int updatePmPlanDetail(Long id, PmPlanDetail pmPlanDetail);

    int deletePmPlanDetailByPmPlanDetailId(Long id);

    int publishPmPlan(Long projectId);

    List<IdNameBaseObject> getAppPmPlanList();

    PmPlanDetailAppVo getProjectPublishedPlanDetails(Long projectId);

    Long confirmProjectEstimateDay(Long projectId, Long pmPlanId,Long projectEstimateDay);

    List<PmFinanceDetailVo> getPmFinanceDetail(Integer year, Integer month);

    public Long createFinanceRemark(PmFinanceDetailVo pmFinanceDetailVo);
}
