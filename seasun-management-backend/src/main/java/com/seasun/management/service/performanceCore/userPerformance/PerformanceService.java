package com.seasun.management.service.performanceCore.userPerformance;

import com.seasun.management.vo.*;

public interface PerformanceService   {
    SubPerformanceAppVo getSubPerformance(Long userId, Long workGroupId, Integer year, Integer month, String filter);

    SubFixMemberPerformanceVo getSubFixMemberPerformance(Long userId, Integer year, Integer month);

    WorkGroupMemberPerformanceAppVo getWorkGroupMemberPerformance(Long observerUserId, Long workGroupId, Integer year, Integer month);

    ProjectFixMemberInfoVo getFmGroupConfirmInfoListByProjectConfirmer(Long userId, Integer year, Integer month, ProjectFixMemberInfoVo.History history);

    FmGroupConfirmInfoVo getFmGroupConfirmInfo(FmGroupConfirmInfoVo fmGroupConfirmInfo);
}
