package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.dto.FnPlatShareConfigUserDTO;
import com.seasun.management.model.*;
import com.seasun.management.vo.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FnPlatShareConfigService {

    boolean getPlatLockFlag(int year, int month, long platId);

    FnPlatShareConfigLockVo getUserShareConfigData(String userType, Long platId, int year, int month);

    List<FnPlatSumProVo> getPlatSumConfigList(Integer year, Integer month);

    void batchUpdateLockFlag(Long[] plats, Integer year, Integer month, String type);

    List<FnPlatShareConfigVo> getMemberShareConfigData(String userType, Long platId, int year, int month, Long memberId);

    List<FnPlatShareConfigVo> getDetailData(Long platId, Long projectId, int year, int month);

    boolean insert(FnPlatShareConfigVo fnPlatShareConfigVo);

    JSONObject update(FnPlatShareConfigVo fnPlatShareConfigVo);

    JSONObject importShareTemplate(MultipartFile file, int year, int month);

    String importShareDetailExcel(MultipartFile file, long platId, int year, int month);

    FnShareTemplate getShareTemplate(int year, int month);

    void copyFromLastMonth(Long platId, int year, int month);

    void startNewMonthShareConfig(int year, int month);

    FnShareInfo getLatestFnShareInfo();

    void saveBatchShareConfig(FnPlatShareBatchVo fnPlatShareBatchVo);

    String exportPlatMonthShareData(Long platId, Integer year, Integer month) throws Exception;

    String exportPlatShareData(Long platId, List<FnPlatShareConfigUserDTO> fnPlatShareConfigs, String fileName, String workdayStr, Map<Long, String> remarkByUserIdMap, BigDecimal workday,String workdayPeriod);

    List<Long> resortForFavorProject(Long platId, List<Long> projectIds);

        //按比例导出
    String exportPlatMonthShareConfig(Integer year, Integer month, List<Long> platIds,String filePath) throws IOException;
    /**
     *周报表导出,周有跨月跨年的情况
     * @param lastYear 上周是哪年
     * @param lastWeek 上周
     * @param currentYear 当前周是哪年
     * @param currentWeek 当前周
     * @return 报表路径
     */
    public String exportWeekly( Integer lastYear, Integer lastWeek, Integer currentYear, Integer currentWeek);
}
