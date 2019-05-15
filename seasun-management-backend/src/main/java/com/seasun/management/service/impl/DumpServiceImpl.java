package com.seasun.management.service.impl;

import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.dto.AppDumpDayDto;
import com.seasun.management.mapper.DumpDayMapper;
import com.seasun.management.model.DumpDay;
import com.seasun.management.service.DumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DumpServiceImpl implements DumpService {

    @Autowired
    private DumpDayMapper dumpDayMapper;

    @Override
    public List<AppDumpDayDto> getAppDumpInfo(Long projectId) {
        DataSourceContextHolder.useThird();
        //暂时只提供剑网三数据
        return dumpDayMapper.selectByProjectAndLastday(DumpDay.Project.JX3,7);
    }
}
