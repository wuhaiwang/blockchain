package com.seasun.management.service.performanceCore.historyTrack;

import com.seasun.management.mapper.UserSalaryChangeMapper;
import com.seasun.management.service.performanceCore.PerformanceCoreHelper;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "SalaryHistory")
public class SalaryHistoryTrackServiceImpl implements HistoryTrackService<OrdinateSalaryChangeAppVo> {

    @Autowired
    UserSalaryChangeMapper userSalaryChangeMapper;

    @Override
    public List<OrdinateSalaryChangeAppVo> getAllHistoryMembersByWorkGroupIdAndTime(Long workGroupId, int year, int month, int quarter) {
        List<OrdinateSalaryChangeAppVo> result = new ArrayList<>();
        List<OrdinateSalaryChangeAppVo> allMonthRecords = userSalaryChangeMapper.selectAllSalaryChangeByYearAndQuarter(year, quarter);
        List<Long> subGroupIds = new ArrayList<>();
        subGroupIds.add(workGroupId);
        PerformanceCoreHelper.processSubGroup(result, subGroupIds, allMonthRecords);
        return result;
    }
}