package com.seasun.management.service;

import com.seasun.management.vo.FmGroupConfirmInfoVo;
import com.seasun.management.vo.ProjectFixMemberInfoVo;
import com.seasun.management.vo.SubFixMemberPerformanceVo;

import java.util.List;

public interface FixMemberService {

    List<FmGroupConfirmInfoVo> getFmGroupConfirmInfoListByWorkGroupManager(Integer year, Integer month);

    SubFixMemberPerformanceVo getSubFixMemberPerformance(Integer year, Integer month);

    ProjectFixMemberInfoVo getFmGroupConfirmInfoListByProjectConfirmer(Integer year, Integer month);

    FmGroupConfirmInfoVo getFmGroupConfirmInfo(FmGroupConfirmInfoVo fmGroupConfirmInfo);

    void submitFmGroup(Integer year, Integer month);

    void confirmFmGroup(Long id);

    void confirmAllFmGroup(Integer year, Integer month, Long projectId);
}
