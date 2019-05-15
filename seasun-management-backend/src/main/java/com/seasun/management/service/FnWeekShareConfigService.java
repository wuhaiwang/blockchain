package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.dto.FnPlatWeekShareConfigDto;
import com.seasun.management.dto.FnWeekCommitStatusDto;
import com.seasun.management.dto.UserEmployeeNoDto;
import com.seasun.management.model.FnPlatWeekShareConfig;
import com.seasun.management.model.FnSumWeekShareConfig;
import com.seasun.management.model.FnWeekShareWorkdayStatus;
import com.seasun.management.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FnWeekShareConfigService {

    FnUserPlatWeekShareConfigVo getUserWeekShareConfigData(String userType, Long platId, int year, int week, int endMonth);

    boolean insertFnPlatWeekShareConfig(FnPlatWeekShareConfigDto fnPlatShareConfigVo);

    JSONObject updateWeekShareConfig(FnPlatWeekShareConfig fnPlatShareConfigVo);

    List<String> copyFromLastWeek(Long platId, Integer year, Integer month, Integer week, Integer nextMonth, Integer endDay, String userType, Integer lastWeek,boolean copyFlag);

    List<SimpleSharePlatWeekVo> getPlatWeekShareDataDetail(Long platId, Long projectId, Integer year, Integer week, Integer endMonth);

    void createPlatSumShareConfigByWeekShareConfig(Long platId, Integer year, Integer month);

    String importPlatWeekShareData(MultipartFile file, Long platId, Integer year, Integer week, Integer month, Integer endMonth, Integer endDay);

    String exportPlatWeekShareData(Long platId, Integer year, Integer mont, Integer week);

    List<UserEmployeeNoDto> getPlatMembersByManagerId(Boolean platManagerFlag, Long userId, Long platId);

    Boolean lockUserWeekShareData(Long platId, Integer year, Integer week, Long userId, boolean lockFlag);

    Boolean lockGroupWeekShareData(Long platId, Integer year, Integer week, Long workGroupId, boolean lockFlag);

    List<WeekShareConfigVo> getMemberWeekShareConfig(Long platId, Integer year, Integer week, Long userId, Integer endMonth);

    ShareMemberSumVo getShareMembersWeekConfigInfo(Long platId, Integer year, Integer week, Integer endMonth);

    FnSumWeekShareConfig addFnSumWeekShareConfig(FnSumWeekShareConfig fnSumWeekShareConfig);

    int updateFnSumWeekShareConfigRemark(FnSumWeekShareConfig fnSumWeekShareConfig);

    void confirmPlatWeekShare(Long platId, int year, int week);

    void addEmptyDataForMissedProjects(Long platId, List<WeekShareConfigVo> configData) ;

    List<FnWeekCommitStatusDto>  getCommitLogByWeek(int year, int week);

    String getPlatFavorTemplate(Long platId);

    List<WorkdayStatusVo> getWeekShareWorkdayList(Integer year);

    void addWeekShareWorkday(FnWeekShareWorkdayStatus item);

    boolean updateWeekShareWorkday(FnWeekShareWorkdayStatus item);

    FnWeekShareSumVo getPlatWeekShareConfigData(String userType, Long platId, Integer year, Integer week, Integer lastWeek, Integer endMonth);

    PlatFavorProjectInfoVo getPlatFavorProjectInfo(Long platId, Integer year, Integer week);

    Integer setPlatFavorProject(Long platId, List<Long> projectIds);
}
