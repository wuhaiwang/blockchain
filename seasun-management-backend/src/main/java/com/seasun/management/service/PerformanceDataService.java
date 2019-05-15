package com.seasun.management.service;

import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PerformanceWorkGroup;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PerformanceDataService {

    List<IdNameBaseObject> getPerformanceDataPermission(Long userId);

    List<YearMonthDto> getPerformanceDate(Long performanceWorkGroupId);

    void importUserPerformance(MultipartFile file, Long performanceWorkGroupId, Boolean insertFlag);

    List<String> checkImportUserPerformanceData(MultipartFile file, Long performanceWorkGroupId);

    boolean changeDataManager(Long userId, Long workGroupPerformanceId, String type);

    void deleteDataManager(Long id);

    Map<Long,String> getSubWorkGroupIdAndFullNames(Long workGroupId, Map<Long, List<PerformanceWorkGroup>> workGroupMap,Map<Long, PerformanceWorkGroup> idPWGMap);
}
