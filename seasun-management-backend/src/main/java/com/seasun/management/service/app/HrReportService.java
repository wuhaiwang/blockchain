package com.seasun.management.service.app;

import com.seasun.management.dto.UserCountOfProjectDto;
import com.seasun.management.model.Project;
import com.seasun.management.vo.ContactsAppVo;
import com.seasun.management.vo.HrOverviewAppVo;
import com.seasun.management.vo.HrPersonnelAnalysisAppVo;
import com.seasun.management.vo.HrUserTransactionAppVo;

import java.util.List;

public interface HrReportService {

    List<HrOverviewAppVo> getAppHrOverview();

    List<HrOverviewAppVo> getAppHrProject();

    List<HrOverviewAppVo> getAppHrPlat();

    HrUserTransactionAppVo getHrUserTransactionByLocation(String location, Integer year, Integer month);

    HrUserTransactionAppVo getHrUserTransactionByProject(Long projectId, Integer year, Integer month);

    HrPersonnelAnalysisAppVo getHrPersonnelAnalysisByLocation(String location, Integer year, Integer month);

    HrPersonnelAnalysisAppVo getHrPersonnelAnalysisByProject(Long projectId, Integer year, Integer month);

       List<UserCountOfProjectDto> getHrUserTransactionByProjectWithSubPlat(Long projectId, Integer year, Integer month);

    List<UserCountOfProjectDto> getHrUserTransactionByProjectsWithSubPlat(List<Long> projectIds, Integer year, Integer month);

    ContactsAppVo getContacts(Long workGroupId);
    
    ContactsAppVo getContactsByProjectId(Long projectId, Long groupId);
}
