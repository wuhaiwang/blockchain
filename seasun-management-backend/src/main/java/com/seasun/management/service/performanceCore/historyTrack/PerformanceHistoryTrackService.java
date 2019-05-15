package com.seasun.management.service.performanceCore.historyTrack;

import com.seasun.management.dto.UserPerformanceDto;

import java.util.List;

public interface PerformanceHistoryTrackService {
    List<UserPerformanceDto> getAllHistoryMembersByWorkGroupIdAndTime(List<Long> managerGroupIds, int year, int month, List<UserPerformanceDto> allMonthRecords);
}
