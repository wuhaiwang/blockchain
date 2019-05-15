package com.seasun.management.service.performanceCore.historyTrack;

import com.seasun.management.dto.UserPerformanceDto;
import com.seasun.management.mapper.UserPerformanceMapper;
import com.seasun.management.service.performanceCore.PerformanceCoreHelper;
import com.seasun.management.util.MyStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@Service
public class PerformanceHistoryTrackServiceImpl implements PerformanceHistoryTrackService {

    @Override
    public List<UserPerformanceDto> getAllHistoryMembersByWorkGroupIdAndTime(List<Long> managerGroupIds,int year, int month,  List<UserPerformanceDto> allMonthRecords) {
        List<UserPerformanceDto> result = new ArrayList<>();
        processSubGroup(result, managerGroupIds, managerGroupIds, allMonthRecords, true);
        return result;
    }

    // fixme 多次遍历整个月绩效记录,浪费性能,并且会丢失绩效组中没有下属成员的组及其子节点
    private void processSubGroup(List<UserPerformanceDto> result, List<Long> subGroupIds, List<Long> managerGroupIds, List<UserPerformanceDto> allMonthRecords, Boolean root) {
        List<Long> newSubGroups = new ArrayList<>();
        for (UserPerformanceDto userPerformanceDto : allMonthRecords) {
            if (!root && managerGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                continue;
            }
            if (!subGroupIds.contains(userPerformanceDto.getWorkGroupId()) && !subGroupIds.contains(userPerformanceDto.getParentGroup())) {
                continue;
            }

            // 直接下属
            if (subGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                result.add(userPerformanceDto);
            }

            // 下属团队
            if (subGroupIds.contains(userPerformanceDto.getParentGroup()) && !subGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                if (!newSubGroups.contains(userPerformanceDto.getWorkGroupId())) {
                    newSubGroups.add(userPerformanceDto.getWorkGroupId());
                }
            }
        }

        if (newSubGroups.size() == 0) {
            return;
        }
        processSubGroup(result, newSubGroups, managerGroupIds, allMonthRecords, false);
    }
}