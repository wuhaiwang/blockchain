package com.seasun.management.service;

import com.seasun.management.dto.PerformanceWorkGroupDto;
import com.seasun.management.dto.UserPerformanceDto;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.model.User;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.vo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserPerformanceService {

    List<UserPerformance> getAllPerformanceByUserId(Long userId);

    List<YearMonthDto> getAllYearMonth();

    SubPerformanceAppVo getObserverPerformance(Long userId, Long workGroupId, Integer year, Integer month, String filter);

    WorkGroupMemberPerformanceAppVo getObserverWorkGroupMemberPerformance(Long observerUserId, Long observerWorkGroupId, Long performanceGroupId, Integer year, Integer month);

    SubPerformanceAppVo getSubPerformance(Integer year, Integer month, String filter);

    List<SubPerformanceAppVo.HistoryInfo> getWorkGroupHistory();

    WorkGroupMemberPerformanceAppVo getWorkGroupMemberPerformance(Long workGroupId, Integer year, Integer month);

    UserPerformanceDetailAppVo getUserPerformanceDetail(Long userId, Integer year, Integer month);

    List<SubPerformanceBaseVo.HistoryInfo> getUserPerformanceHistory(Long userId, boolean isMyself);

    void addPerformanceDetail(UserPerformance userPerformance);

    void updatePerformanceDetail(Long id, UserPerformance userPerformance);

    void batchHandlePerformanceDetail(List<UserPerformance> userPerformanceList, User logonUer, int year, int month);

    void submitSelfPerformance(Long id);

    void submitWorkGroup(Long workGroupId, Integer year, Integer month);

    void confirmPerformance(Integer year, Integer month);

    String downloadToExcel(List<UserPerformanceDto> userPerformances, String fileName, List<PerformanceWorkGroupDto> allActiveWorkGroup, List<Long> subUserIds, Map<Long, String> subWorkGoupIdAndFullNameMap);

    String downloadYearlyToExcel(List<UserPerformanceDto> userPerformances,  Map<Long, String> subWorkGoupIdAndFullNameMap, String fileName, List<UserPerformanceDto> leavedUserPerformanceDatas);

    void updateWorkGroup(User logonUser, Integer year, Integer month);

    boolean insertPerfWorkGroupStatus(List<Long> insertPerfWorkGroupIds, Map<Long, String> performanceWorkGroupStatusMap, Integer year, Integer month, User currentUser);

    String downloadUserPerformanceData(Long workGroupId, Integer year, Integer month, String grade);

    UserPhotoWallVo selectUserPerformance(Integer year, Integer month, String floorId);

    public boolean uploadUserPhoto(MultipartFile file , String loginId);

    public String downloadUserPhoto(Integer year, Integer month, String floorName);

    void notifyUserPerformance();

    void syncPhoto();

    List<SubGroupPerformanceVo> getSubGroupPerformanceByDate(Long userId, Integer year, Integer month);

    public void updateUserPerformanceStatus();
}
