package com.seasun.management.service;

import com.seasun.management.vo.AppAnnualPlanVo;
import com.seasun.management.vo.PMilestoneRequestDto;
import com.seasun.management.vo.WebAnnualPlanVo;

public interface ProjectPlanService {

    AppAnnualPlanVo getAppAnnulPlan();

    WebAnnualPlanVo getWebAnnualPlan();

    void deletePMilestone(long id);

    long addPMilestone(PMilestoneRequestDto pMilestoneVo);

    long updatePMilestone(PMilestoneRequestDto pMilestoneVo);

    void releasePMilestone(int releaseYear);
}
