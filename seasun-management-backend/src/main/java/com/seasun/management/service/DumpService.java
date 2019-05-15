package com.seasun.management.service;

import com.seasun.management.dto.AppDumpDayDto;
import java.util.List;

public interface DumpService {

    List<AppDumpDayDto> getAppDumpInfo(Long projectId);
}
