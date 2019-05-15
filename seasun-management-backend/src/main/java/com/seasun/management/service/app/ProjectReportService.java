package com.seasun.management.service.app;

import com.seasun.management.vo.*;

import java.util.List;

public interface ProjectReportService {

    List<ProjectAppVo> getAppProjects(String status);

    FnProjectInfoAppVo getAppFnProjectInfo(Long projectId, Integer year, Integer month);

    FnProjectShareAppVo getAppProjectShare(Long projectId, Integer year, Integer month);

    List<FnProjectShareDetailVo> getAppProjectShareDetail(Long projectId, Integer year, Integer month);

    List<PlatAppVo> getAppPlats();

    FnPlatCostAppVo getAppPlatCost(Long platId, Integer year, Integer month);

    FnPlatShareAppVo getAppPlatShare(Long platId, Integer year, Integer month);

    OrgProjectInfoAppVo getAppOrgProjectInfo(Long projectId);

    OrgProjectWorkGroupInfoAppVo getAppOrgProjectWorkGroupInfo(Long projectId, Long groupId);

    List<FnSummaryProjectAppVo> getAppSummaryProjects();

    FnProjectStatYoYVO getAppFnProjectStatYoY(Long id, Integer year);
}
