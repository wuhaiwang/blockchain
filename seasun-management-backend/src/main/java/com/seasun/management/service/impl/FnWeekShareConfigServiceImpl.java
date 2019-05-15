package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.UserShareType;
import com.seasun.management.dto.*;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.FnShareHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.FnPlatShareConfigService;
import com.seasun.management.service.FnWeekShareConfigService;
import com.seasun.management.service.RUserWorkGroupService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FnWeekShareConfigServiceImpl implements FnWeekShareConfigService {

    private static final Logger logger = LoggerFactory.getLogger(FnPlatShareConfigServiceImpl.class);

    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backupExcelUrl;

    @Autowired
    private FnPlatShareConfigMapper fnPlatShareConfigMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FnPlatWeekShareConfigMapper fnPlatWeekShareConfigMapper;

    @Autowired
    private WorkGroupService workGroupService;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private FnWeekShareStatusMapper fnWeekShareStatusMapper;

    @Autowired
    private FnSumWeekShareConfigMapper fnSumWeekShareConfigMapper;

    @Autowired
    private FnWeekShareCommitLogMapper fnWeekShareCommitLogMapper;

    @Autowired
    private FnPlatShareConfigService fnPlatShareConfigService;

    @Autowired
    private FnPlatFavorProjectMapper fnPlatFavorProjectMapper;

    @Autowired
    private RUserWorkGroupService rUserWorkGroupService;

    @Autowired
    private FnWeekShareWorkdayStatusMapper fnWeekShareWorkdayStatusMapper;

    private BigDecimal getWeekShareWorkday(Integer year, Integer week) {
        BigDecimal workday = fnWeekShareWorkdayStatusMapper.selectWorkdayByYearAndWeek(year, week);
        if (workday == null) {
            workday = new BigDecimal(0);
        }
        return workday;
    }

    @Override
    public FnUserPlatWeekShareConfigVo getUserWeekShareConfigData(String userType, Long platId, int year, int week, int endMonth) {
        User user = MyTokenUtils.getCurrentUser();
        FnUserPlatWeekShareConfigVo result = new FnUserPlatWeekShareConfigVo();
        boolean lockFlag = false;
//        boolean memberFlag = true;
//        boolean platManagerFlag = getUserPlatShareManagerFlag(user.getId(), platId);
        String status = "";

        // 判断每周工作日是否填写，开启
        FnWeekShareWorkdayStatus fnWeekShareWorkdayStatus = fnWeekShareWorkdayStatusMapper.selectByYearAndWeek(year, week);
        if (fnWeekShareWorkdayStatus == null || !fnWeekShareWorkdayStatus.getStatus()) {
            lockFlag = true;
            status = "未开启";
        }
        // 回填每周工作日
        else {
            result.setWorkday(fnWeekShareWorkdayStatus.getWorkday());
        }

        // 判断平台是否确认本周
        if (!lockFlag) {
            List<FnWeekShareCommitLog> commitLogs = fnWeekShareCommitLogMapper.selectByYearAndWeekAndPlatId(year, week, platId);
            if (!commitLogs.isEmpty()) {
                lockFlag = true;
            }
        }
//        if (UserShareType.member.toString().equals(userType)) {
        List<WeekShareConfigVo> configDatas = fnPlatWeekShareConfigMapper.selectUserDataByUserIdAndWeekAndYear(user.getId(), week, year);
        if (!lockFlag) {
            // 个人记录中带锁
            if (!configDatas.isEmpty()) {
                if (configDatas.get(0).getLockFlag()) {
                    lockFlag = true;
                }
                // 没有记录，但是人力组被锁定
            } else {
                FnWeekShareStatus fnWeekShareStatus = fnWeekShareStatusMapper.selectLockedGroupByYearAndWeekAndGroupId(year, week, user.getWorkGroupId());
                if (fnWeekShareStatus != null && fnWeekShareStatus.getLockFlag() != null) {
                    lockFlag = fnWeekShareStatus.getLockFlag();
                }
            }
        }
        /// 这里做判断是因为,会出现平台分摊负责人锁了组，又去填下属个人分摊
//            if (!lockFlag) {
//                FnWeekShareStatus fnWeekShareStatus = fnWeekShareStatusMapper.selectLockedGroupByYearAndWeekAndGroupId(year, week, user.getWorkGroupId());
//                if (fnWeekShareStatus != null && fnWeekShareStatus.getLockFlag() != null && fnWeekShareStatus.getLockFlag()) {
//                    lockFlag = true;
//                }
//            }
        FnSumWeekShareConfig remarkInfo = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserId(year, week, user.getId());
        if (remarkInfo != null) {
            result.setRemark(remarkInfo.getRemark());
            result.setRemarkId(remarkInfo.getId());
        }
//        }

//        // 汇总视图
//        else if (UserShareType.manager.toString().equals(userType)) {
//            memberFlag = false;
//            if (platManagerFlag) {
//                configDatas = fnPlatWeekShareConfigMapper.selectPlatDataByPlatIdAndWeekAndYear(platId, week, year);
//            } else {
//                List<Long> memberIds = getPlatMemberIdsByManagerId(platManagerFlag, user.getId(), platId);
//                if (!memberIds.isEmpty()) {
//                    configDatas = fnPlatWeekShareConfigMapper.selectPlatDataByPlatIdAndWeekAndYearAndUserIds(platId, week, year, memberIds);
//                }
//            }
//
//            if (!lockFlag) {
//                // 大锁
//                // 若当前用户是分摊负责人，则确认后，整个组锁定
//                if (platManagerFlag) {
//                    List<FnWeekShareCommitLog> commitLogs = fnWeekShareCommitLogMapper.selectByYearAndWeekAndPlatId(year, week, platId);
//                    lockFlag = commitLogs.size() > 0;
//                }
//                // 若当前用户是普通人力组负责人，则两个组都被锁定时，返回lockFlag = true
//                else {
//                    List<Long> directManagedWorkGroupIds = getManageWorkGroupIdsByUserId(user.getId(), platId);
//                    lockFlag = fnWeekShareStatusMapper.selectLockedGroupByYearAndWeekAndGroupIds(year, week, directManagedWorkGroupIds).size()
//                            == directManagedWorkGroupIds.size();
//                }
//            }
//        }

        if (configDatas == null) {
            configDatas = new ArrayList<>();
        }

        // 降序排序
        Collections.sort(configDatas, new Comparator<WeekShareConfigVo>() {
            @Override
            public int compare(WeekShareConfigVo o1, WeekShareConfigVo o2) {
                return o2.getShareAmount().compareTo(o1.getShareAmount());
            }
        });

        addEmptyDataForMissedProjects(platId, configDatas);

        result.setPlatProcessList(configDatas);
        result.setLockFlag(lockFlag);
        if (lockFlag && status.isEmpty()) {
            status = "已锁定";
        }
        result.setStatus(status);
        if (lockFlag) {
            for (WeekShareConfigVo configData : result.getPlatProcessList()) {
                configData.setLockFlag(lockFlag);
            }
        }
        return result;
    }

    @Override
    public boolean insertFnPlatWeekShareConfig(FnPlatWeekShareConfigDto fnPlatShareConfigVo) {
        if (fnPlatShareConfigVo.getCreateBy() == null) {
            fnPlatShareConfigVo.setCreateBy(MyTokenUtils.getCurrentUserId());
            fnPlatShareConfigVo.setWorkGroupId(MyTokenUtils.getCurrentUser().getWorkGroupId());
        } else {
            User targetUser = userMapper.selectByPrimaryKey(fnPlatShareConfigVo.getCreateBy());
            fnPlatShareConfigVo.setWorkGroupId(targetUser == null ? 0 : targetUser.getWorkGroupId());
        }
        if (fnPlatShareConfigVo.getShareAmount() == null) {
            fnPlatShareConfigVo.setShareAmount(new BigDecimal(0));
        }
        // 如果工作日和备注都是空,拦截掉
        if (fnPlatShareConfigVo.getShareAmount().floatValue() <= 0 && fnPlatShareConfigVo.getRemark() == null) {
            return true;
        }

        if (!chechUpdateWeekSharePermission(fnPlatShareConfigVo.getPlatId(), fnPlatShareConfigVo.getWorkGroupId(), fnPlatShareConfigVo.getCreateBy())) {
            throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
        }

        BigDecimal workday = getWeekShareWorkday(fnPlatShareConfigVo.getYear(), fnPlatShareConfigVo.getWeek());
        // 平台分摊负责人锁了成员或者组，又新增成员分摊记录，需要用到这个lockFlag todo:这把锁很关键，千万不要改关于这个lockFlag的逻辑
        boolean lockFlag = false;

        List<FnPlatWeekShareConfig> fnPlatWeekShareConfig = fnPlatWeekShareConfigMapper.selectByCond(fnPlatShareConfigVo);
        BigDecimal shareAmount = new BigDecimal(fnPlatShareConfigVo.getShareAmount().toString());
        if (!fnPlatWeekShareConfig.isEmpty()) {
            // 注： 必须使用BigDecimal计算,且必须 .toString() ，否则存在浮点数的精度转换问题！
            for (FnPlatWeekShareConfig config : fnPlatWeekShareConfig) {
                shareAmount = shareAmount.add(new BigDecimal(config.getShareAmount().toString()));
                lockFlag = config.getLockFlag();
                // 若找到一条已经存在的项目记录，则直接更新。 todo : 不会出现这种情况，出现了就是异常流程情况
                if (fnPlatShareConfigVo.getProjectId().equals(config.getProjectId())) {
                    config.setRemark(fnPlatShareConfigVo.getRemark());
                    config.setShareAmount(fnPlatShareConfigVo.getShareAmount());
                    config.setUpdateTime(new Date());
                    fnPlatWeekShareConfigMapper.updateByPrimaryKeySelective(config);
                    return true;
                }
            }
        }
        if (shareAmount.compareTo(workday) > 0) {
            throw new UserInvalidOperateException("本周工作日为 " + workday.doubleValue() + " 天,已经超过上限,无法新增。");
        }

        // 判断是否锁了人
        if (!lockFlag) {
            // 判断是否锁了组
            Boolean groupLockFlag = fnWeekShareStatusMapper.selectGroupLockFlagByYearAndWeekAndUserId(fnPlatShareConfigVo.getYear(), fnPlatShareConfigVo.getWeek(), fnPlatShareConfigVo.getCreateBy());
            if (groupLockFlag != null) {
                lockFlag = groupLockFlag;
            }
        }
        fnPlatShareConfigVo.setLockFlag(lockFlag);

        List<FnPlatWeekShareConfig> inserts = new ArrayList<>(3);
        BigDecimal sharePro = new BigDecimal(1);
        fnPlatShareConfigVo.setSharePro(sharePro);
        fnPlatShareConfigVo.setCreateTime(new Date());

        // 周跨月的情况，把一条数据拆成两条，并重新分配工作日比例
        if (!fnPlatShareConfigVo.getMonth().equals(fnPlatShareConfigVo.getEndMonth())) {
            BigDecimal nextMonthPro = new BigDecimal(fnPlatShareConfigVo.getEndDay() / FnPlatWeekShareConfig.workDay);
            sharePro = sharePro.subtract(nextMonthPro);
            FnPlatWeekShareConfig nextMonthConfig = createNextMonthFnPlatWeekShareConfig(fnPlatShareConfigVo, fnPlatShareConfigVo.getShareAmount(), sharePro, nextMonthPro);
            inserts.add(nextMonthConfig);
        }

        inserts.add(fnPlatShareConfigVo);
        fnPlatWeekShareConfigMapper.batchInsert(inserts);
        return true;
    }

    @Override
    public JSONObject updateWeekShareConfig(FnPlatWeekShareConfig fnPlatShareConfigVo) {
        // fixme : 待补充锁定逻辑
        boolean editShareAmountFlag = true;
        boolean editRemarkFlag = false;

        BigDecimal workday = getWeekShareWorkday(fnPlatShareConfigVo.getYear(), fnPlatShareConfigVo.getWeek());
        if (fnPlatShareConfigVo.getShareAmount() == null) {
            fnPlatShareConfigVo.setShareAmount(new BigDecimal(0));
            editShareAmountFlag = false;
        }
        if (fnPlatShareConfigVo.getRemark() != null) {
            editRemarkFlag = true;
        }

        if (fnPlatShareConfigVo.getCreateBy() == null) {
            fnPlatShareConfigVo.setCreateBy(MyTokenUtils.getCurrentUserId());
            fnPlatShareConfigVo.setWorkGroupId(MyTokenUtils.getCurrentUser().getWorkGroupId());
        } else {
            User targetUser = userMapper.selectByPrimaryKey(fnPlatShareConfigVo.getCreateBy());
            fnPlatShareConfigVo.setWorkGroupId(targetUser == null ? 0 : targetUser.getWorkGroupId());
        }
        if (!chechUpdateWeekSharePermission(fnPlatShareConfigVo.getPlatId(), fnPlatShareConfigVo.getWorkGroupId(), fnPlatShareConfigVo.getCreateBy())) {
            throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
        }

        List<FnPlatWeekShareConfig> weekShareConfigVos = fnPlatWeekShareConfigMapper.selectByUserIdAndWeekAndYear(fnPlatShareConfigVo.getCreateBy(), fnPlatShareConfigVo.getWeek(), fnPlatShareConfigVo.getYear());
        List<FnPlatWeekShareConfig> needUpdates = new ArrayList<>(3);
        BigDecimal shareAmount = fnPlatShareConfigVo.getShareAmount();
        for (FnPlatWeekShareConfig weekShareConfigVo : weekShareConfigVos) {
            if (weekShareConfigVo.getProjectId().equals(fnPlatShareConfigVo.getProjectId())) {
                needUpdates.add(weekShareConfigVo);
            } else {
                shareAmount = shareAmount.add(new BigDecimal(weekShareConfigVo.getShareAmount().toString()));
            }
        }
        if (editShareAmountFlag && shareAmount.compareTo(workday) > 0) {
            throw new UserInvalidOperateException("本周工作日 " + workday.doubleValue() + " 天,用户在其他项目的工作日加上本次修改的工作日超过上限,请重新修改。");
        }

        if (needUpdates.isEmpty() || needUpdates.size() > 2) {
            logger.error("数据库中 ，用户：" + fnPlatShareConfigVo.getCreateBy() + "在" + fnPlatShareConfigVo.getYear() + "年" + fnPlatShareConfigVo.getWeek() + "周的数据异常");
            throw new ParamException(ErrorMessage.PERSISTENT_LAYER_MESSAGE);
        }

        Date date = new Date();
        for (FnPlatWeekShareConfig item : needUpdates) {
            if (editShareAmountFlag) {
                item.setShareAmount(fnPlatShareConfigVo.getShareAmount().multiply(item.getSharePro()));
            }
            if (editRemarkFlag) {
                item.setRemark(fnPlatShareConfigVo.getRemark());
            }
            item.setUpdateTime(date);
        }

        fnPlatWeekShareConfigMapper.batchUpdate(needUpdates);

        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("message", "success");
        return result;
    }

    private List<String> checkShareData(List<FnPlatWeekShareConfig> configs, List<Long> passUserIds, boolean managerFlag, Long operatorId, Long platId, Integer year, Integer week) {
        List<Long> writedUserIds;

        Set<Long> existDataUserIds = configs.stream().map(c->c.getCreateBy()).collect(Collectors.toSet());
        List<String> result = new ArrayList<>();
        List<Project> projectVos = projectMapper.selectUnActiveProject();
        Map<Long, String> projectNameByIdMap = projectVos.stream().collect(Collectors.toMap(x -> x.getId(), x -> x.getName()));
        Map<Long, Object> userIdMap = new HashMap<>();
        if (managerFlag) {
            writedUserIds = fnPlatWeekShareConfigMapper.selectWritedUserIdsByPlatIdAndYearAndWeek(platId, year, week);
            List<Long> memberIds = getPlatMemberIdsByManagerId(null, operatorId, platId);
            for (Long memberId : memberIds) {
                userIdMap.put(memberId, null);
            }
            if (!writedUserIds.isEmpty()) {
                result.add("当前已有 " + writedUserIds.size() + " 人已填写分摊数据，这些人数据将被保留。");
            }
        } else {
            writedUserIds = new ArrayList<>();
            Long createBy = configs.get(0).getCreateBy();
            userIdMap.put(createBy, null);
        }

        Set<Long> unActiveProjectIdSet = new HashSet<>();
        Set<Long> unMatchedUserIdSet = new HashSet<>();
        Set<Long> passedUserIdSet = new HashSet<>();
        Iterator<FnPlatWeekShareConfig> iterator = configs.iterator();
        while (iterator.hasNext()) {
            FnPlatWeekShareConfig item = iterator.next();

            // 过滤掉已经非激活的项目
            if (projectNameByIdMap.containsKey(item.getProjectId())) {
                unActiveProjectIdSet.add(item.getProjectId());
                iterator.remove();
                continue;
            }

            // 过滤掉已经填写过的数据
            if (writedUserIds.contains(item.getCreateBy())) {
                iterator.remove();
                continue;
            }

            // 过滤掉不属于本人或本人所管理的成员
            if (!userIdMap.containsKey(item.getCreateBy())) {
                iterator.remove();
                continue;
            }
            passedUserIdSet.add(item.getCreateBy());
        }
        if (unActiveProjectIdSet.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Long unActiveProjectId : unActiveProjectIdSet) {
                sb.append(projectNameByIdMap.get(unActiveProjectId));
                sb.append("、");
            }
            result.add(sb.substring(0, sb.length() - 1) + " 项目被关闭，上述项目数据将不被复制。");
        }

        // 检查没有在上个月找到数据的用户
        userIdMap.forEach((k, v) -> {
            boolean isLastMonthContainData = existDataUserIds.contains(k);
            if (!isLastMonthContainData) {
                unMatchedUserIdSet.add(k);
            }
        });

        if (!unMatchedUserIdSet.isEmpty()) {
            List<Long> userIdList = new ArrayList<>();
            unMatchedUserIdSet.forEach(p -> userIdList.add(p));
            List<User> invalidUsers = userMapper.selectBYUserIds(userIdList);
            StringBuffer invalidUserNameStrBuffer = new StringBuffer();
            invalidUsers.forEach(u -> invalidUserNameStrBuffer.append(u.getLastName() + u.getFirstName()).append(","));
            String invalidUserString = invalidUserNameStrBuffer.toString();
            result.add("发现共 " + invalidUsers.size() + " 人没有找到上周的数据，这些成员无法完成复制，包括：" + invalidUserString.substring(0, invalidUserString.length() - 1));
        }
        passUserIds.addAll(passedUserIdSet);
        return result;
    }

    // 自动生成的情况下，会有精度损失。以下逻辑会把总工作日抹平到totalWorkday
    private void fixWorkday(List<FnPlatWeekShareConfig> records, BigDecimal totalWorkday) {
        if (!records.isEmpty()) {
            Map<Long, List<FnPlatWeekShareConfig>> configsByProjectIdMap = records.stream().collect(Collectors.groupingBy(x -> x.getProjectId()));
            BigDecimal userTotaWorkday = new BigDecimal(0);

            Long maxShareProProjectId = records.get(0).getProjectId();
            BigDecimal maxShareProjectAmount = new BigDecimal(0);
            for (Map.Entry<Long, List<FnPlatWeekShareConfig>> entry : configsByProjectIdMap.entrySet()) {
                BigDecimal projectShareAmount = new BigDecimal(0);
                for (FnPlatWeekShareConfig item : entry.getValue()) {
                    projectShareAmount.add(item.getShareAmount());
                    userTotaWorkday = userTotaWorkday.add(item.getShareAmount()).setScale(1, BigDecimal.ROUND_HALF_UP);
                }
                if (projectShareAmount.compareTo(maxShareProjectAmount) > 0) {
                    maxShareProjectAmount = projectShareAmount;
                }
            }

            BigDecimal appendValue = totalWorkday.subtract(userTotaWorkday).setScale(1, BigDecimal.ROUND_HALF_UP);
            List<FnPlatWeekShareConfig> configs = configsByProjectIdMap.get(maxShareProProjectId);
            if (configs.size() == 1) {
                configs.get(0).setShareAmount(configs.get(0).getShareAmount().add(appendValue));
            } else {
                BigDecimal firstAppendValue = appendValue.multiply(configs.get(0).getSharePro());
                configs.get(0).setShareAmount(configs.get(0).getShareAmount().add(firstAppendValue));
                configs.get(1).setShareAmount(configs.get(1).getShareAmount().add(appendValue.subtract(firstAppendValue)));

            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> copyFromLastWeek(Long platId, Integer year, Integer month, Integer week, Integer nextMonth, Integer endDay, String userType, Integer lastWeek, boolean copyFlag) {
        Long useId = MyTokenUtils.getCurrentUserId();

        boolean twoMonthWeekFlag = !month.equals(nextMonth);
        boolean memberFlag = UserShareType.member.toString().equals(userType);

        FnPlatWeekShareConfig selectCond = new FnPlatWeekShareConfig();
        Integer copyYear = year;
        if (week == 1) {
            copyYear--;
        }
        selectCond.setYear(copyYear);
        selectCond.setWeek(lastWeek);
        selectCond.setPlatId(platId);
        if (memberFlag) {
            selectCond.setCreateBy(useId);
        }

        List<FnPlatWeekShareConfig> fnPlatWeekShareConfigs = fnPlatWeekShareConfigMapper.selectByCond(selectCond);
        if (fnPlatWeekShareConfigs.isEmpty()) {
            throw new UserInvalidOperateException("上周未在该平台填写过分摊数据，无法复制。");
        }

        List<String> result = new ArrayList<>();

        List<FnWeekShareWorkdayStatus> workdayStatuses = fnWeekShareWorkdayStatusMapper.selectByDate(year, week, copyYear, lastWeek);
        BigDecimal workDay = new BigDecimal(0);
        BigDecimal lastWorkDay = new BigDecimal(0);
        for (FnWeekShareWorkdayStatus item : workdayStatuses) {
            if (year.equals(item.getYear()) && week.equals(item.getWeek())) {
                workDay = item.getWorkday();
            }
            if (copyYear.equals(item.getYear()) && lastWeek.equals(item.getWeek())) {
                lastWorkDay = item.getWorkday();
            }
        }

        if (workDay.compareTo(lastWorkDay) != 0) {
            result.add("本周工作日 " + workDay + " 天,上周工作日 " + lastWorkDay + " 天，无法直接拷贝，系统默认会按上周各项目的周工作日占比计算出本周数据.");
        }
        BigDecimal workdayPro = workDay.divide(lastWorkDay, 4, BigDecimal.ROUND_HALF_UP);

        List<Long> passUserIds = new ArrayList<>();
        result.addAll(checkShareData(fnPlatWeekShareConfigs, passUserIds, !memberFlag, useId, platId, year, week));
        if (!copyFlag) {
            if (result.size() > 0) {
                result.add(0, "系统检测到如下问题：");
                for (int i = 1; i < result.size(); i++) {
                    result.set(i, i + ". " + result.get(i));
                }
            }
            return result;
        } else {
            if (!fnPlatWeekShareConfigs.isEmpty()) {

                Map<Long, List<FnPlatWeekShareConfig>> configsByUserIdMap = fnPlatWeekShareConfigs.stream().collect(Collectors.groupingBy(x -> x.getCreateBy()));
                List<FnPlatWeekShareConfig> inserts = new ArrayList<>();
                Date nowDate = new Date();
                for (Map.Entry<Long, List<FnPlatWeekShareConfig>> userEntry : configsByUserIdMap.entrySet()) {
                    List<FnPlatWeekShareConfig> userConfigs = new ArrayList<>();
                    BigDecimal userTotalWorkday = new BigDecimal(0);
                    Map<Long, List<FnPlatWeekShareConfig>> configsByProjectIdMap = userEntry.getValue().stream().collect(Collectors.groupingBy(x -> x.getProjectId()));
                    // 出现跨月情况，则拷贝的肯定是不跨月的数据

                    if (twoMonthWeekFlag) {
                        for (Map.Entry<Long, List<FnPlatWeekShareConfig>> projectEntry : configsByProjectIdMap.entrySet()) {
                            FnPlatWeekShareConfig config = projectEntry.getValue().get(0);
                            config.setYear(year);
                            config.setWeek(week);
                            config.setUpdateTime(nowDate);
                            config.setPlatId(platId);
                            config.setMonth(month);
                            config.setLockFlag(false);
                            userTotalWorkday = userTotalWorkday.add(config.getShareAmount());
                            BigDecimal totalShareAmount = config.getShareAmount().multiply(workdayPro);

                            BigDecimal nextMonthSharePro = new BigDecimal(endDay / FnPlatWeekShareConfig.workDay);
                            BigDecimal sharePro = new BigDecimal(1).subtract(nextMonthSharePro);

                            FnPlatWeekShareConfig nextMonthConfig = createNextMonthFnPlatWeekShareConfig(config, totalShareAmount, sharePro, nextMonthSharePro);

                            userConfigs.add(config);
                            userConfigs.add(nextMonthConfig);
                        }
                    }
                    // 不是跨月的情况，则有可能上周是两条数据
                    else {
                        for (Map.Entry<Long, List<FnPlatWeekShareConfig>> projectEntry : configsByProjectIdMap.entrySet()) {

                            FnPlatWeekShareConfig config = projectEntry.getValue().get(0);
                            config.setYear(year);
                            config.setWeek(week);
                            config.setUpdateTime(nowDate);
                            config.setPlatId(platId);
                            config.setMonth(month);
                            config.setLockFlag(false);
                            BigDecimal shareAmout = new BigDecimal(0);
                            for (FnPlatWeekShareConfig item : projectEntry.getValue()) {
                                shareAmout = shareAmout.add(item.getShareAmount());
                            }
                            userTotalWorkday = userTotalWorkday.add(shareAmout);

                            shareAmout = shareAmout.multiply(workdayPro);
                            config.setShareAmount(shareAmout);
                            config.setSharePro(BigDecimal.ONE);
                            userConfigs.add(config);
                        }
                    }
                    fixWorkday(userConfigs, userTotalWorkday.multiply(workdayPro).setScale(1, BigDecimal.ROUND_HALF_UP));
                    inserts.addAll(userConfigs);
                }

                // 删除这周旧数据
                fnPlatWeekShareConfigMapper.delectByUserIdsAndYearAndWeekAndPlatId(passUserIds, year, week, platId);

                // 插入
                fnPlatWeekShareConfigMapper.batchInsert(inserts);
            }
            return null;

        }

    }


    @Override
    public List<SimpleSharePlatWeekVo> getPlatWeekShareDataDetail(Long platId, Long projectId, Integer year, Integer week, Integer endMonth) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        List<SimpleSharePlatWeekVo> result;
        boolean userPlatManagerFlag = getUserPlatShareManagerFlag(currentUserId, platId);

        if (userPlatManagerFlag) {
            result = fnPlatWeekShareConfigMapper.selectSimpleSharePlatWeekVoByCond(platId, projectId, year, week);
        } else {
            List<Long> memberIds = getPlatMemberIdsByManagerId(userPlatManagerFlag, currentUserId, platId);
            if (!memberIds.isEmpty()) {
                result = fnPlatWeekShareConfigMapper.selectSimpleSharePlatWeekVoByCondAndUserIds(platId, projectId, year, week, memberIds);
            } else {
                result = new ArrayList<>();
            }
        }

        // 平台负责人，锁重设
        if (userPlatManagerFlag) {
            List<FnWeekShareCommitLog> commitLogs = fnWeekShareCommitLogMapper.selectByYearAndWeekAndPlatId(year, week, platId);
            if (commitLogs.size() < 1) {
                for (SimpleSharePlatWeekVo item : result) {
                    item.setLockFlag(false);
                }
            }

        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPlatSumShareConfigByWeekShareConfig(Long platId, Integer year, Integer month) {
        List<FnPlatWeekShareConfig> fnPlatWeekShareConfigs = fnPlatWeekShareConfigMapper.selectMonthShareConfigByPlatIdAndMonthAndWeek(platId, year, month);
        // 1月份需要考虑跨年的情况，即将 createNextMonthFnPlatWeekShareConfig 方法中的情况处理掉。
        if (month == 1) {
            fnPlatWeekShareConfigs.addAll(fnPlatWeekShareConfigMapper.selectMonthShareConfigByPlatIdAndMonthAndWeek(platId, year - 1, 13));
        }

        // 1. 准备基础数据
        Set<Long> allProjectIds = new HashSet<>(); // 所有员工本月参与过的项目
        Map<Long, BigDecimal> shareAmountByUserIdMap = new HashMap<>(); // 单个用户-所有项目的总工作日
        Map<Long, List<FnPlatWeekShareConfig>> fnPlatWeekShareConfigsByUserIdMap = new HashMap<>();
        for (FnPlatWeekShareConfig fnPlatWeekShareConfig : fnPlatWeekShareConfigs) {
            allProjectIds.add(fnPlatWeekShareConfig.getProjectId());
            if (shareAmountByUserIdMap.containsKey(fnPlatWeekShareConfig.getCreateBy())) {
                shareAmountByUserIdMap.put(fnPlatWeekShareConfig.getCreateBy(), shareAmountByUserIdMap.get(fnPlatWeekShareConfig.getCreateBy()).add(fnPlatWeekShareConfig.getShareAmount()));
                fnPlatWeekShareConfigsByUserIdMap.get(fnPlatWeekShareConfig.getCreateBy()).add(fnPlatWeekShareConfig);
            } else {
                shareAmountByUserIdMap.put(fnPlatWeekShareConfig.getCreateBy(), fnPlatWeekShareConfig.getShareAmount());
                List<FnPlatWeekShareConfig> configs = new ArrayList<>();
                configs.add(fnPlatWeekShareConfig);
                fnPlatWeekShareConfigsByUserIdMap.put(fnPlatWeekShareConfig.getCreateBy(), configs);
            }
        }

        // 2. 遍历用户，为每个成员生成个人的月分摊数据
        Date createTime = new Date();
        BigDecimal zero = new BigDecimal(0);
        BigDecimal shareRate = new BigDecimal(MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.FixMemberRate, MySystemParamUtils.DefaultValue.fixMemberRate));
        List<FnPlatShareConfig> allUserMonthRecords = new ArrayList<>();

        // 开始遍历用户
        for (Map.Entry<Long, List<FnPlatWeekShareConfig>> entry : fnPlatWeekShareConfigsByUserIdMap.entrySet()) {
            Long userId = entry.getKey();
            BigDecimal userTotalShareDay = shareAmountByUserIdMap.get(userId);

            // 直接排除工作日为0的用户
            if (userTotalShareDay.compareTo(zero) == 0) {
                continue;
            }
            // 用户-项目-周分摊数据
            Map<Long, List<FnPlatWeekShareConfig>> userConfigsByProjectIdMap = entry.getValue().stream().collect(Collectors.groupingBy(x -> x.getProjectId()));

            boolean inPlatCompanyFlag = userConfigsByProjectIdMap.containsKey(Project.Id.PLAT_COMPANY);
            boolean onlyInPlatCompanyFlag = false;
            BigDecimal platShareAmount = new BigDecimal(0);
            if (inPlatCompanyFlag) {
                for (FnPlatWeekShareConfig config : userConfigsByProjectIdMap.get(Project.Id.PLAT_COMPANY)) {
                    platShareAmount = platShareAmount.add(config.getShareAmount());
                }
                if (userTotalShareDay.compareTo(platShareAmount) == 0) {
                    onlyInPlatCompanyFlag = true;
                }
            }
            // 开始遍历项目
            List<FnPlatShareConfig> userProjectMonthRecords = new ArrayList<>();
            if (onlyInPlatCompanyFlag) {
                float denominator = allProjectIds.size() - 1;
                BigDecimal sharePro = new BigDecimal(1 / denominator).setScale(2, BigDecimal.ROUND_HALF_UP);
                for (Long item : allProjectIds) {
                    // 平台公司，不需要生成月数据。
                    if (Project.Id.PLAT_COMPANY.equals(item)) {
                        continue;
                    }
                    FnPlatShareConfig shareConfig = new FnPlatShareConfig();
                    shareConfig.setMonth(month);
                    shareConfig.setYear(year);
                    shareConfig.setPlatId(platId);
                    shareConfig.setProjectId(item);
                    shareConfig.setCreateBy(userId);
                    shareConfig.setCreateTime(createTime);
                    shareConfig.setWeight(BigDecimal.ONE);
                    shareConfig.setShareAmount(platShareAmount.multiply(sharePro));
                    shareConfig.setSharePro(sharePro);
                    // 检查是否为固化
                    if (shareConfig.getSharePro().compareTo(shareRate) > 0) {
                        shareConfig.setFixedNumber(BigDecimal.ONE);
                    }
                    userProjectMonthRecords.add(shareConfig);
                }
            } else {
                for (Map.Entry<Long, List<FnPlatWeekShareConfig>> configEntry : userConfigsByProjectIdMap.entrySet()) {
                    Long projectId = configEntry.getKey();

                    // 平台公司，不需要生成月数据。
                    if (Project.Id.PLAT_COMPANY.equals(projectId)) {
                        continue;
                    }

                    // 计算用户在某个项目下的总工作日
                    BigDecimal projectShareDayAmount = new BigDecimal(0);
                    for (FnPlatWeekShareConfig config : configEntry.getValue()) {
                        projectShareDayAmount = projectShareDayAmount.add(config.getShareAmount());
                    }
                    // 若包含平台公司，则需要做二次分摊，重新计算工作量和分摊比例
                    if (inPlatCompanyFlag) {
                        projectShareDayAmount = projectShareDayAmount.add(platShareAmount.multiply(projectShareDayAmount.divide((userTotalShareDay.subtract(platShareAmount)), 1, BigDecimal.ROUND_HALF_UP)));
                    }
                    BigDecimal sharePro = projectShareDayAmount.divide(userTotalShareDay, 1, BigDecimal.ROUND_HALF_UP);

                    FnPlatShareConfig shareConfig = new FnPlatShareConfig();
                    shareConfig.setMonth(month);
                    shareConfig.setYear(year);
                    shareConfig.setPlatId(platId);
                    shareConfig.setProjectId(projectId);
                    shareConfig.setCreateBy(userId);
                    shareConfig.setCreateTime(createTime);
                    shareConfig.setWeight(BigDecimal.ONE);
                    shareConfig.setShareAmount(projectShareDayAmount);
                    shareConfig.setSharePro(sharePro);

                    // 检查是否为固化
                    if (shareConfig.getSharePro().compareTo(shareRate) > 0) {
                        shareConfig.setFixedNumber(BigDecimal.ONE);
                    }
                    userProjectMonthRecords.add(shareConfig);
                }
            }
            fixPercent(userProjectMonthRecords);
            allUserMonthRecords.addAll(userProjectMonthRecords);
        }

        // 删除个人
        fnPlatShareConfigMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);
        // 插入个人
        fnPlatShareConfigMapper.batchInsert(allUserMonthRecords);
    }

    // 自动生成的情况下，会有精度损失。以下逻辑将会把比例抹平为100%
    private void fixPercent(List<FnPlatShareConfig> records) {
        if (!records.isEmpty()) {
            FnPlatShareConfig maxShareProItem = records.get(0);
            BigDecimal totalSharePro = new BigDecimal(0);
            for (FnPlatShareConfig projectRecordItem : records) {
                if (projectRecordItem.getSharePro().compareTo(maxShareProItem.getSharePro()) > 0) {
                    maxShareProItem = projectRecordItem;
                }
                totalSharePro = totalSharePro.add(projectRecordItem.getSharePro());
            }
            BigDecimal appendValue = new BigDecimal(1).subtract(totalSharePro).setScale(4, BigDecimal.ROUND_HALF_UP);
            maxShareProItem.setSharePro(maxShareProItem.getSharePro().add(appendValue));
        }
    }


    // 获取用户在平台管辖的下属员工
    @Override
    public List<UserEmployeeNoDto> getPlatMembersByManagerId(Boolean platManagerFlag, Long userId, Long platId) {
        List<UserEmployeeNoDto> result;

        if (platManagerFlag == null) {
            platManagerFlag = getUserPlatShareManagerFlag(userId, platId);
        }

        if (platManagerFlag) {
            result = userMapper.selectActiveUserEmployeeNoDtoByProjectId(platId);
        } else {
            List<Long> allSubGroupIds = getAllSubGroupIds(userId, platId);
            if (allSubGroupIds.isEmpty()) {
                return new ArrayList<>();
            }
            result = userMapper.selectActiveUserEmployeeNoDtoByWorkGroupIds(allSubGroupIds);
        }

        return result;
    }

    // 获取用户在平台管辖的下属员工(去除掉直接管理的组下，已经被锁定的员工。如：一个人管理两个组，其中一个被锁定)
    private List<UserEmployeeNoDto> getUnlockedPlatMembersByManagerId(boolean platManagerFlag, Long userId, Long platId, int year, int week) {
        List<UserEmployeeNoDto> result;
//        boolean platManagerFlag = getUserPlatShareManagerFlag(userId, platId);

        if (platManagerFlag) {
            result = userMapper.selectActiveUserEmployeeNoDtoByProjectId(platId);
        } else {
            List<Long> allSubGroupIds = getAllUnLockedSubGroupIds(year, week, userId, platId);
            if (allSubGroupIds.isEmpty()) {
                return new ArrayList<>();
            }
            result = userMapper.selectActiveUserEmployeeNoDtoByWorkGroupIds(allSubGroupIds);
        }

        return result;
    }


    @Override
    public Boolean lockUserWeekShareData(Long platId, Integer year, Integer week, Long userId, boolean lockFlag) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        boolean managerFlag = getUserPlatShareManagerFlag(currentUserId, platId);

        List<FnPlatWeekShareConfig> dbRecords = fnPlatWeekShareConfigMapper.selectByUserIdAndWeekAndYear(currentUserId, year, week);
        if (dbRecords.size() == 0) {
            throw new UserInvalidOperateException("该员工尚未填写周数据，无法锁定。");
        }

        // 只有分摊负责人才可以执行锁定操作
        if (managerFlag) {
            List<Long> userIds = new ArrayList<>(2);
            userIds.add(userId);
            fnPlatWeekShareConfigMapper.updateLockFlagByPlatIdAndYearAndWeekAndUserIds(lockFlag, platId, year, week, userIds);
        }

        return true;
    }

    private boolean getPlatWeekLockFlag(Integer year, Integer week, Long platId) {
        boolean lockFlag = false;
        Boolean startFlag = fnWeekShareWorkdayStatusMapper.selectStatusByYearAndWeek(year, week);
        if (startFlag == null || !startFlag) {
            lockFlag = true;
        }
        if (!lockFlag) {
            List<FnWeekShareCommitLog> commitLogs = fnWeekShareCommitLogMapper.selectByYearAndWeekAndPlatId(year, week, platId);
            if (commitLogs.size() > 0) {
                lockFlag = true;
            }
        }

        return lockFlag;
    }

    @Override
    public List<WeekShareConfigVo> getMemberWeekShareConfig(Long platId, Integer year, Integer week, Long userId, Integer endMonth) {
        boolean weekLockFlag = getPlatWeekLockFlag(year, week, platId);

        List<WeekShareConfigVo> weekShareConfigVos = fnPlatWeekShareConfigMapper.selectUserDataByUserIdAndWeekAndYear(userId, week, year);
        // 降序
        Collections.sort(weekShareConfigVos, new Comparator<WeekShareConfigVo>() {
            @Override
            public int compare(WeekShareConfigVo o1, WeekShareConfigVo o2) {

                return o2.getShareAmount().compareTo(o1.getShareAmount());
            }
        });

        addEmptyDataForMissedProjects(platId, weekShareConfigVos);

        if (!weekLockFlag && !getUserPlatShareManagerFlag(MyTokenUtils.getCurrentUserId(), platId)) {
            // 判断是否记录被锁
            if (weekShareConfigVos.get(0).getLockFlag()) {
                weekLockFlag = true;
            }
            // 这种情况出现在人力组组长查看一个没填写过分摊记录但是人力组被平台分摊负责人锁了的情况
            if (!weekLockFlag) {
                Boolean groupLockFlag = fnWeekShareStatusMapper.selectGroupLockFlagByYearAndWeekAndUserId(year, week, userId);
                if (groupLockFlag != null) {
                    weekLockFlag = groupLockFlag;
                }
            }
        }

        for (WeekShareConfigVo weekShareConfigVo : weekShareConfigVos) {
            weekShareConfigVo.setLockFlag(weekLockFlag);
        }

        return weekShareConfigVos;
    }

    @Override
    @Deprecated
    public ShareMemberSumVo getShareMembersWeekConfigInfo(Long platId, Integer year, Integer week, Integer endMonth) {
        ShareMemberSumVo result = new ShareMemberSumVo();
        List<ShareMemberSumVo.ShareWorkGroup> directGroups = new ArrayList<>();
        List<ShareMemberSumVo.ShareWorkGroup> subGroups = new ArrayList<>();
        List<Long> allGroupIds = new ArrayList<>();
        List<SimUserShareDto> simUserShareDtos;
        List<FnSumWeekShareConfig> fnSumWeekShareConfigs;
        List<UserEmployeeNoDto> userEmployeeNoDtos;
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        Map<Long, List<Long>> groupIdsBySubGroupIdMap = new HashMap<>();
        boolean platManagerFlag = getUserPlatShareManagerFlag(currentUserId, platId);

        // 判断平台是否被锁
        boolean platLockFlag = getPlatWeekLockFlag(year, week, platId);

        if (platManagerFlag) {
            // todo 直接复用这个servive会把子平台的人力组查出来，但是查人是根据平台id查，后续逻辑会把没人的人力组排除
            List<WorkGroup> rootTrees = workGroupService.getWorkGroupTreeByProjectId(platId);
            // 直接展示的组 todo: 平台分摊负责人在人力树上可能只是普通叶子节点,所在拿在人力树上顶级组节点做直接管辖的组
            for (WorkGroup item : rootTrees) {
                ShareMemberSumVo.ShareWorkGroup directGroup = new ShareMemberSumVo.ShareWorkGroup();
                directGroup.setId(item.getId());
                directGroup.setName(item.getName());
                directGroups.add(directGroup);
                allGroupIds.add(item.getId());
                // 子组,只深入1级
                if (item.getChildren() != null) {
                    for (WorkGroup workGroup : item.getChildren()) {
                        allGroupIds.add(workGroup.getId());
                        List<Long> allSubGroupIds = getAllSubGroupIds(workGroup);
                        allGroupIds.addAll(allSubGroupIds);
                        groupIdsBySubGroupIdMap.put(workGroup.getId(), allSubGroupIds);

                        ShareMemberSumVo.ShareWorkGroup subGroup = new ShareMemberSumVo.ShareWorkGroup();
                        subGroup.setId(workGroup.getId());
                        subGroup.setName(workGroup.getName());
                        subGroups.add(subGroup);
                    }
                }
            }
            userEmployeeNoDtos = userMapper.selectUserEmployeeNoDtoByProjectId(platId);
            simUserShareDtos = fnPlatWeekShareConfigMapper.selectSimUserShareVoByPlatIdAndYearAndWeek(platId, year, week);
            fnSumWeekShareConfigs = fnSumWeekShareConfigMapper.selectByYearAndWeek(year, week, platId);
        } else {
            List<HrWorkGroupDto> allGroups = workGroupMapper.selectHrWorkGroupDtoByProjectId(platId);
            Map<Long, List<HrWorkGroupDto>> groupsByParentIdMap = allGroups.stream().collect(Collectors.groupingBy(x -> x.getParent()));

            List<HrWorkGroupDto> manageGroups = new ArrayList<>();
            for (HrWorkGroupDto item : allGroups) {
                if (item.getLeaderIds() != null) {
                    for (String s : item.getLeaderIds().split(",")) {
                        if (currentUserId.equals(Long.parseLong(s))) {
                            manageGroups.add(item);
                        }
                    }
                }
            }
            List<Long> leaderIds = new ArrayList<>(2);
            leaderIds.add(currentUserId);
            // 遍历直接管辖的组，生成人力树
            for (HrWorkGroupDto manageGroup : manageGroups) {
                workGroupService.trackHrGroupTreeForRootGroup(leaderIds, manageGroup, groupsByParentIdMap, new HashMap<>(), new HashMap<>(), false);
                // 直接管辖的组
                ShareMemberSumVo.ShareWorkGroup directGroup = new ShareMemberSumVo.ShareWorkGroup();
                directGroup.setId(manageGroup.getId());
                directGroup.setName(manageGroup.getName());
                directGroups.add(directGroup);
                allGroupIds.add((manageGroup.getId()));
                // 子组,只深入1级
                if (manageGroup.getChildWorkGroups() != null) {
                    for (HrWorkGroupDto hrWorkGroupDto : manageGroup.getChildWorkGroups()) {
                        allGroupIds.add(hrWorkGroupDto.getId());
                        List<Long> allSubGroupIds = getAllSubGroupIds(hrWorkGroupDto);
                        allGroupIds.addAll(allSubGroupIds);
                        groupIdsBySubGroupIdMap.put(hrWorkGroupDto.getId(), allSubGroupIds);

                        ShareMemberSumVo.ShareWorkGroup subGroup = new ShareMemberSumVo.ShareWorkGroup();
                        subGroup.setId(hrWorkGroupDto.getId());
                        subGroup.setName(hrWorkGroupDto.getName());
                        subGroups.add(subGroup);
                    }
                }
            }
            if (!allGroupIds.isEmpty()) {
                userEmployeeNoDtos = userMapper.selectActiveUserEmployeeNoDtoByWorkGroupIds(allGroupIds);
                List<Long> userIds = userEmployeeNoDtos.stream().map(x -> x.getUserId()).collect(Collectors.toList());
                simUserShareDtos = fnPlatWeekShareConfigMapper.selectSimUserShareVoByPlatIdAndYearAndWeekAndUserIds(platId, year, week, userIds);
                fnSumWeekShareConfigs = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserIds(year, week, userIds);
            } else {
                userEmployeeNoDtos = new ArrayList<>();
                simUserShareDtos = new ArrayList<>();
                fnSumWeekShareConfigs = new ArrayList<>();
            }
        }

        result.setHasLockPerms(platManagerFlag);
        result.setGroup(directGroups);
        result.setSubGroup(subGroups);
        if (allGroupIds.isEmpty()) {
            return result;
        }

        // 开始向人力组回填人员分摊数据
        Map<Long, SimUserShareDto> userShareByIdMap = simUserShareDtos.stream().collect(Collectors.toMap(x -> x.getId(), y -> y));
        Map<Long, FnSumWeekShareConfig> userRemarkByUserIdMap = fnSumWeekShareConfigs.stream().collect(Collectors.toMap(x -> x.getUserId(), y -> y));
        Map<Long, List<UserEmployeeNoDto>> userByWorkGroupIdMap = userEmployeeNoDtos.stream().collect(Collectors.groupingBy(x -> x.getWorkGroupId()));

        Iterator<ShareMemberSumVo.ShareWorkGroup> groupIterator = result.getGroup().iterator();
        Iterator<ShareMemberSumVo.ShareWorkGroup> subGroupIterator = result.getSubGroup().iterator();

        // 回填直接管理的组
        while (groupIterator.hasNext()) {
            ShareMemberSumVo.ShareWorkGroup directGroup = groupIterator.next();
            if (userByWorkGroupIdMap.containsKey(directGroup.getId())) {
                directGroup.setMembers(getGroupSimUserShareDto(userByWorkGroupIdMap.get(directGroup.getId()), userShareByIdMap, userRemarkByUserIdMap, platLockFlag));
            } else {
                groupIterator.remove();
            }
        }

        // 回填子组
        while (subGroupIterator.hasNext()) {
            ShareMemberSumVo.ShareWorkGroup subGroup = subGroupIterator.next();
            List<Long> groupIds = groupIdsBySubGroupIdMap.get(subGroup.getId());
            groupIds.add(subGroup.getId());
            List<UserEmployeeNoDto> userShareDtos = new ArrayList<>();
            for (Long groupId : groupIds) {
                if (userByWorkGroupIdMap.containsKey(groupId)) {
                    userShareDtos.addAll(userByWorkGroupIdMap.get(groupId));
                }
            }
            if (userShareDtos.isEmpty()) {
                subGroupIterator.remove();
            } else {
                subGroup.setMembers(getGroupSimUserShareDto(userShareDtos, userShareByIdMap, userRemarkByUserIdMap, platLockFlag));
            }
        }

        // 设置group和subGroup的lockFlag
        List<Long> lockedGroupIds = fnWeekShareStatusMapper.selectWorkGroupIdsByYearAndWeekAndLockFlag(year, week, true);
        result.getGroup().forEach(g -> {
            if (lockedGroupIds.contains(g.getId())) {
                g.setLockFlag(true);
            }
        });
        result.getSubGroup().forEach(g -> {
            if (lockedGroupIds.contains(g.getId())) {
                g.setLockFlag(true);
            }
        });

        return result;
    }

    private List<SimUserShareDto> getGroupSimUserShareDto(List<UserEmployeeNoDto> users, Map<Long, SimUserShareDto> userShareByIdMap, Map<Long, FnSumWeekShareConfig> userRemarkByUserIdMap, boolean lockFlag) {
        List<SimUserShareDto> result = new ArrayList<>();
        if (users == null) {
            return result;
        }
        List<SimUserShareDto> noShareDataUsers = new ArrayList<>();
        for (UserEmployeeNoDto user : users) {
            SimUserShareDto userShareDto = userShareByIdMap.get(user.getUserId());
            FnSumWeekShareConfig userRemark = userRemarkByUserIdMap.get(user.getUserId());
            String remark = null;
            if (userRemark != null) {
                remark = userRemark.getRemark();
            }
            if (userShareDto != null) {
                userShareDto.setRemark(remark);
                result.add(userShareDto);
            } else {
                userShareDto = new SimUserShareDto();
                userShareDto.setId(user.getUserId());
                userShareDto.setRemark(remark);
                userShareDto.setName(user.getUserName());
                userShareDto.setLockFlag(lockFlag);
                userShareDto.setLoginId(user.getLoginId());
                noShareDataUsers.add(userShareDto);
            }
        }
        Collections.sort(result, new Comparator<SimUserShareDto>() {
            @Override
            public int compare(SimUserShareDto o1, SimUserShareDto o2) {
                return o2.getShareAmount().compareTo(o1.getShareAmount());
            }
        });
        result.addAll(noShareDataUsers);
        return result;
    }

    private List<Long> getAllSubGroupIds(HrWorkGroupDto item) {
        List<Long> result = new ArrayList<>();
        if (item.getChildWorkGroups() != null) {
            for (HrWorkGroupDto group : item.getChildWorkGroups()) {
                result.add(group.getId());
                result.addAll(getAllSubGroupIds(group));
            }
        }
        return result;
    }

    private List<Long> getAllSubGroupIds(WorkGroup workGroup) {
        List<Long> result = new ArrayList<>();
        if (workGroup.getChildren() != null) {

            for (WorkGroup group : workGroup.getChildren()) {
                result.add(group.getId());
                result.addAll(getAllSubGroupIds(group));
            }
        }
        return result;
    }

    @Override
    public FnSumWeekShareConfig addFnSumWeekShareConfig(FnSumWeekShareConfig fnSumWeekShareConfig) {
        FnSumWeekShareConfig item = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserId(fnSumWeekShareConfig.getYear(), fnSumWeekShareConfig.getWeek(), fnSumWeekShareConfig.getUserId());
        if (item == null) {
            fnSumWeekShareConfig.setCreateTime(new Date());
            fnSumWeekShareConfigMapper.insert(fnSumWeekShareConfig);
        } else {
            fnSumWeekShareConfig.setId(item.getId());
            updateFnSumWeekShareConfigRemark(fnSumWeekShareConfig);
        }
        return fnSumWeekShareConfig;
    }

    @Override
    public int updateFnSumWeekShareConfigRemark(FnSumWeekShareConfig fnSumWeekShareConfig) {
        FnSumWeekShareConfig cond = new FnSumWeekShareConfig();
        cond.setId(fnSumWeekShareConfig.getId());
        cond.setRemark(fnSumWeekShareConfig.getRemark());
        cond.setUpdateTime(new Date());
        return fnSumWeekShareConfigMapper.updateByPrimaryKeySelective(cond);
    }

    // 分摊导出时，补充一些没填写过的成员
    private List<FnPlatShareConfigUserDTO> getNoWriteShareConfigUserDto(Long userId, Long platId, Boolean platManagerFlag, Set<Long> userIdSet) {
        List<FnPlatShareConfigUserDTO> result;

        if (platManagerFlag) {
            result = userMapper.selectActiveShareConfigUserDTOByPlatId(platId);
        } else {
            List<Long> allSubGroupIds = getAllSubGroupIds(userId, platId);
            if (!allSubGroupIds.isEmpty()) {
                result = userMapper.selectActiveShareConfiUserDTOByWorkGroupIds(allSubGroupIds);
            } else {
                result = new ArrayList<>();
            }
        }

        result = result.stream().filter(x -> !userIdSet.contains(x.getId())).collect(Collectors.toList());

        return result;
    }

    @Override
    public String exportPlatWeekShareData(Long platId, Integer year, Integer month, Integer week) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        boolean platManagerFlag = getUserPlatShareManagerFlag(currentUserId, platId);

        List<FnPlatShareConfigUserDTO> fnPlatShareConfigs;

        if (platManagerFlag) {
            fnPlatShareConfigs = fnPlatWeekShareConfigMapper.selectConfigUserDTOByPlatIdAndYearAndMonth(platId, year, week);
        } else {
            List<Long> subMemberIds = getPlatMemberIdsByManagerId(platManagerFlag, currentUserId, platId);
            if (subMemberIds.isEmpty()) {
                throw new UserInvalidOperateException("您管辖的人力组下，没有成员，无法导出。");
            }
            fnPlatShareConfigs = fnPlatWeekShareConfigMapper.selectConfigUserDTOByPlatIdAndYearAndMonthAndUserIds(platId, year, week, subMemberIds);
        }

        if (fnPlatShareConfigs.isEmpty()) {
            throw new UserInvalidOperateException("该平台本周没有分摊成员填写数据，请先填写成员周分摊。");
        }

        Set<Long> userIdSet = fnPlatShareConfigs.stream().map(x -> x.getCreateBy()).collect(Collectors.toSet());

        fnPlatShareConfigs.addAll(getNoWriteShareConfigUserDto(currentUserId, platId, platManagerFlag, userIdSet));

        Map<Long, String> remarkByUserIdMap = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserIds(year, week, new ArrayList<>(userIdSet)).stream().collect(Collectors.toMap(x -> x.getUserId(), y -> y.getRemark(), (x, y) -> y));

        FnWeekShareWorkdayStatus workdayStatus = fnWeekShareWorkdayStatusMapper.selectByYearAndWeek(year, week);
     /*     if (workdayStatus == null || workdayStatus.getWorkday() == null || new BigDecimal(0).compareTo(workdayStatus.getWorkday()) == 0) {
            throw new UserInvalidOperateException("本周未开启周分摊或者本周工作日为0，无法导出。");
        }
        StringBuilder sb = new StringBuilder();
        if (workdayStatus.getMonth() != null) {
            sb.append(workdayStatus.getMonth()).append("月").append(workdayStatus.getDay()).append("日-").append(workdayStatus.getEndMonth()).append("月").append(workdayStatus.getEndDay()).append("日，");
        }
        sb.append(workdayStatus.getYear()).append("年第").append(workdayStatus.getWeek()).append("周");*/
        return fnPlatShareConfigService.exportPlatShareData(platId, fnPlatShareConfigs, year + "年" + week + "周人力统计.xls", "本周核算时间:", remarkByUserIdMap, workdayStatus.getWorkday(), MyDateUtils.getDateStrByFnWeekShareWorkdayStatus(workdayStatus));
    }


    @Override
    public void confirmPlatWeekShare(Long platId, int year, int week) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        // 添加锁定记录
        FnWeekShareCommitLog record = new FnWeekShareCommitLog();
        record.setYear(year);
        record.setWeek(week);
        record.setOperatorId(MyTokenUtils.getCurrentUserId());
        record.setCommitTime(new Date());
        record.setPlatId(platId);
        fnWeekShareCommitLogMapper.insert(record);

        // 锁定所有组和所有人,已经插入的组绩效记录，只需更新即可。
        List<HrWorkGroupDto> platSubActiveGroups = workGroupMapper.selectHrWorkGroupDtoByProjectId(platId);

        // 1. 所定所有的子组
        List<Long> existRecords = fnWeekShareStatusMapper.selectWorkGroupIdsByYearAndWeek(year, week);
        List<FnWeekShareStatus> insertList = new ArrayList<>();
        List<Long> updateIdList = new ArrayList<>();
        for (HrWorkGroupDto group : platSubActiveGroups) {
            if (!existRecords.contains(group.getId())) {
                FnWeekShareStatus fnPlatShareLockStatus = new FnWeekShareStatus();
                fnPlatShareLockStatus.setLockFlag(true);
                fnPlatShareLockStatus.setCreateTime(new Date());
                fnPlatShareLockStatus.setOperatorId(currentUserId);
                fnPlatShareLockStatus.setYear(year);
                fnPlatShareLockStatus.setWeek(week);
                fnPlatShareLockStatus.setWorkGroupId(group.getId());
                insertList.add(fnPlatShareLockStatus);
            } else {
                updateIdList.add(group.getId());
            }
        }
        if (insertList.size() > 0) {
            fnWeekShareStatusMapper.batchInsert(insertList);
        }
        if (updateIdList.size() > 0) {
            fnWeekShareStatusMapper.updateByWorkGroupIdsAndYearAndWeek(year, week, true, currentUserId, updateIdList);
        }

        // 2. 锁定子组下的所有用户
        List<Long> groupIds = platSubActiveGroups.stream().map(p -> p.getId()).collect(Collectors.toList());
        fnPlatWeekShareConfigMapper.updateLockFlagByPlatIdAndYearAndWeekAndWorkGroupIds(true, platId, year, week, groupIds);

    }

    @Override
    @Transactional
    public Boolean lockGroupWeekShareData(Long platId, Integer year, Integer week, Long workGroupId, boolean lockFlag) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        boolean platManagerFlag = getUserPlatShareManagerFlag(currentUserId, platId);
        List<HrWorkGroupDto> platSubActiveGroups = workGroupMapper.selectHrWorkGroupDtoByProjectId(platId);
        if (platSubActiveGroups.isEmpty()) {
            throw new ParamException("该平台配置异常，下属人力工作组成员为空。");
        }

        Set<Long> subGroupIds = new HashSet<>();
        List<Long> needLockWorkGroupIds = new ArrayList<>();
        needLockWorkGroupIds.add(workGroupId); // 1. 先锁定指定的组
        boolean isLeader = isLeaderInGroupList(platSubActiveGroups, workGroupId, currentUserId);
        // 平台分摊负责人才能锁定和解锁
        // 人力组长可以只能锁定
        if (platManagerFlag || (isLeader && lockFlag)) {
            subGroupIds = workGroupService.getSubGroupIds(workGroupId, platSubActiveGroups); // 2. 再锁定指定组的子组
            needLockWorkGroupIds.addAll(subGroupIds);
        } else {
            throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
        }

        // 1. 插入所有的子组锁
        if (!needLockWorkGroupIds.isEmpty()) {
            List<Long> existRecords = fnWeekShareStatusMapper.selectWorkGroupIdsByYearAndWeek(year, week);
            List<FnWeekShareStatus> insertList = new ArrayList<>();
            List<Long> updateIdList = new ArrayList<>();
            for (Long needLockWorkGroupId : needLockWorkGroupIds) {
                if (!existRecords.contains(needLockWorkGroupId)) {
                    FnWeekShareStatus fnPlatShareLockStatus = new FnWeekShareStatus();
                    fnPlatShareLockStatus.setLockFlag(lockFlag);
                    fnPlatShareLockStatus.setCreateTime(new Date());
                    fnPlatShareLockStatus.setOperatorId(currentUserId);
                    fnPlatShareLockStatus.setYear(year);
                    fnPlatShareLockStatus.setWeek(week);
                    fnPlatShareLockStatus.setWorkGroupId(needLockWorkGroupId);
                    insertList.add(fnPlatShareLockStatus);
                } else {
                    updateIdList.add(needLockWorkGroupId);
                }
            }
            if (insertList.size() > 0) {
                fnWeekShareStatusMapper.batchInsert(insertList);
            }
            if (updateIdList.size() > 0) {
                fnWeekShareStatusMapper.updateByWorkGroupIdsAndYearAndWeek(year, week, lockFlag, currentUserId, updateIdList);
            }
        }

        // 2. 锁定子组下的所有用户
        if (needLockWorkGroupIds.size() > 0) {
            fnPlatWeekShareConfigMapper.updateLockFlagByPlatIdAndYearAndWeekAndWorkGroupIds(lockFlag, platId, year, week, new ArrayList<>(needLockWorkGroupIds));
        }
        return true;
    }


    @Override
    @Transactional
    public String importPlatWeekShareData(MultipartFile file, Long platId, Integer year, Integer week, Integer month, Integer endMonth, Integer endDay) {
        Project project = projectMapper.selectByPrimaryKey(platId);
        if (project == null || !project.getActiveFlag()) {
            throw new UserInvalidOperateException("该平台不存在或已关闭，请联系系统管理员。");
        }

        Workbook wb = null;
        try {
            ExcelHelper.saveExcelBackup(file, filePathPrefix + backupExcelUrl);
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }
        Long userId = MyTokenUtils.getCurrentUserId();
        boolean twoMonthWeekFlag = false;
        boolean platManagerFlag = getUserPlatShareManagerFlag(userId, platId);
        BigDecimal workday = getWeekShareWorkday(year, week);
        BigDecimal sharePro = new BigDecimal(1);
        BigDecimal nextMonthSharePro = new BigDecimal(0);

        if (endMonth != null && !month.equals(endMonth)) {
            twoMonthWeekFlag = true;
            sharePro = sharePro.subtract(new BigDecimal(endDay / FnPlatWeekShareConfig.workDay));
        }
        // 获取当前负责人所管理的用户列表
        List<UserEmployeeNoDto> userList = getUnlockedPlatMembersByManagerId(platManagerFlag, userId, platId, year, week);
        Map<Long, Long> userIdByEmployeeNoMap = userList.stream().collect(Collectors.toMap(x -> x.getEmployeeNo(), y -> y.getUserId()));
        // 获取当前平台下已锁定的成员
        List<Long> lockUserIds;
        if (platManagerFlag) {
            lockUserIds = new ArrayList<>();
        } else {
            lockUserIds = fnPlatWeekShareConfigMapper.selectLockUserIdsByPlatIdAndYearAndWeek(platId, year, week);
        }

        List<ProjectVo> shareProjects = projectMapper.selectAllShareProject();
        // 把珠海技术中心平台下的平台排除 平台-公司 项目
        if (Project.Id.TECGNOLOGY_CENTER.equals(project.getParentShareId())) {
            shareProjects = shareProjects.stream().filter(x -> !Project.Id.PLAT_COMPANY.equals(x.getId())).collect(Collectors.toList());
        }
        List<String> errorLogHeader = new ArrayList<>();
        List<String> errorLogColumn = new ArrayList<>();
        List<String> errorLogRow = new ArrayList<>();
        List<String> errorLogTail = new ArrayList<>();
        errorLogHeader.add("\r\n");
        errorLogHeader.add("已完成有效数据的导入，但发现一些有问题的行或列，以下这些数据已被丢弃:\r\n");
        errorLogHeader.add("\r\n");
        errorLogColumn.add("\r\n");
        errorLogColumn.add("# 有问题的列：");
        errorLogColumn.add("\r\n");
        errorLogRow.add("\r\n");
        errorLogRow.add("# 有问题的行：");
        errorLogRow.add("\r\n");
        errorLogTail.add("\r\n");
        errorLogTail.add("# 其他错误：");
        errorLogTail.add("\r\n");
        Sheet sheet = wb.getSheetAt(0);
        // 建立列和项目ID的映射关系
        Map<Integer, Long> mapColumnProject = new HashMap<>();
        int colIndexEnd = 7;
        Row rowProject = sheet.getRow(0);
        while (true) {
            Cell cell = rowProject.getCell(colIndexEnd);
            String cellText = cell.getStringCellValue();
            // 以这一列作为结束
            if ("工作日".equals(cellText)) {
                break;
            }
            // 为所有项目列匹配id
            if (Project.Name.PLAT_COMPANY.equals(cellText) && ReportHelper.PlatInShareProjectMap.get(platId) != null) {
                mapColumnProject.put(colIndexEnd, ReportHelper.PlatInShareProjectMap.get(platId));
            } else {
                ProjectVo projectVo = shareProjects.stream().filter(item -> {
                    List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(item);
                    return projectUsedNames.contains(ExcelHelper.trimSpaceAndSpecialSymbol(cellText));
                }).findFirst().orElse(null);
                if (null == projectVo) {
                    errorLogColumn.add("- 第 " + (colIndexEnd + 1) + " 列, 项目名称 : " + cellText + " 不正确，系统中找不到此项目。 \r\n");
                } else {
                    mapColumnProject.put(colIndexEnd, projectVo.getId());
                }
            }
            colIndexEnd++;
        }

        // 开始读取个人的分摊数据
        List<UserFnShareDataDto> userFnShareDataDtoList = new ArrayList<>();
        int rowIndexEnd = 1;
        try {
            while (true) {
                Row userShareDataRow = sheet.getRow(rowIndexEnd++);
                logger.info("current row is:{}", userShareDataRow.getRowNum());
                Cell cellEndOfUserShare = userShareDataRow.getCell(5);

                //  工号
                Cell employeeNoCell = userShareDataRow.getCell(0);
                // 以这一行做结束行
                if (employeeNoCell == null) {
                    break;
                }

                Long employeeNo = new BigDecimal(MyCellUtils.getCellValue(employeeNoCell)).longValue();
                if (employeeNo == 0L) {
                    continue;
                }
                // 姓名
                String userName = MyCellUtils.getCellValue(userShareDataRow.getCell(1));

                Long cellUserId = userIdByEmployeeNoMap.get(employeeNo);
                if (null == cellUserId) {
                    errorLogRow.add("- 第 " + (userShareDataRow.getRowNum() + 1) + " 行, 员工姓名：" + userName + " , 不在该平台下您所管辖的人力组内，已忽略该员工分摊信息。 \r\n");
                    continue;
                }
                if (lockUserIds.contains(cellUserId)) {
                    errorLogRow.add("- 第 " + (userShareDataRow.getRowNum() + 1) + " 行, 员工姓名：" + userName + " , 已被平台分摊负责人锁定，已忽略该员工分摊信息。 \r\n");
                    continue;
                }

                UserFnShareDataDto userShareDataDto = new UserFnShareDataDto();
                Map<Long, BigDecimal> shareDayByProjectIdMap = new HashMap<>();
                BigDecimal shareTotal = new BigDecimal(0);
                for (int columnIndex = 7; columnIndex <= colIndexEnd; columnIndex++) {
                    if (!mapColumnProject.containsKey(columnIndex)) {
                        continue;
                    }
                    String shareDayStr = MyCellUtils.getCellValue(userShareDataRow.getCell(columnIndex));
                    if (shareDayStr.isEmpty() || "-".equals(shareDayStr)) {
                        continue;
                    }
                    BigDecimal shareDay = new BigDecimal(shareDayStr);
                    shareTotal = shareTotal.add(shareDay);
                    Long projectId = mapColumnProject.get(columnIndex);
                    if (shareDayByProjectIdMap.containsKey(projectId)) {
                        shareDayByProjectIdMap.put(projectId, shareDayByProjectIdMap.get(projectId).add(shareDay));
                    } else {
                        shareDayByProjectIdMap.put(projectId, shareDay);
                    }
                }
                shareTotal = shareTotal.setScale(1, BigDecimal.ROUND_HALF_UP);
                if (shareTotal.compareTo(workday) > 0) {
                    errorLogRow.add("- 第 " + (userShareDataRow.getRowNum() + 1) + " 行， 员工姓名：" + userName + " ,总工作日大于 " + workday.doubleValue() + " 天，请重新填写该员工本周工作信息。\r\n");
                    continue;
                }
                userShareDataDto.setEmployeeNo(employeeNo);
                userShareDataDto.setUserId(cellUserId);
                userShareDataDto.setShareData(shareDayByProjectIdMap);
                userShareDataDto.setRemark(MyCellUtils.getCellValue(userShareDataRow.getCell(colIndexEnd + 1)));
                userFnShareDataDtoList.add(userShareDataDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        // 导入时不读取下面的统计信息，后续导出时，由系统计算。
//        // 保存月工作周期的工作日总和
//        Date nowDate = new Date();
//        Row row = sheet.getRow(rowIndexEnd + 1);
//        if (row != null) {
//            String workDayPeriodCell = MyCellUtils.getCellValue(row.getCell(5));
//            String WorkDayCell = MyCellUtils.getCellValue(row.getCell(7));
//            if (workDayPeriodCell.isEmpty()) {
//                workDayPeriodCell = null;
//                errorLogTail.add("本月核算时间未填写。 \r\n");
//            }
//            if (WorkDayCell.isEmpty()) {
//                errorLogTail.add("本月工作周期的工作日之和未填写。\r\n");
//            }
//
//            fnPlatShareMonthInfoMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);
//            FnPlatShareMonthInfo fnPlatShareMonthInfo = new FnPlatShareMonthInfo();
//            fnPlatShareMonthInfo.setYear(year);
//            fnPlatShareMonthInfo.setMonth(month);
//            fnPlatShareMonthInfo.setPlatId(platId);
//            fnPlatShareMonthInfo.setWorkDay(WorkDayCell == null ? null : Float.parseFloat(WorkDayCell));
//            fnPlatShareMonthInfo.setWorkdayPeriod(workDayPeriodCell);
//            fnPlatShareMonthInfo.setCreateTime(nowDate);
//            fnPlatShareMonthInfoMapper.insert(fnPlatShareMonthInfo);
//        }

        List<FnPlatWeekShareConfig> inserts = new ArrayList<>();

        if (!userFnShareDataDtoList.isEmpty()) {
            List<Long> userIds = new ArrayList<>();
            for (UserFnShareDataDto userFnShareDataDto : userFnShareDataDtoList) {
                userIds.add(userFnShareDataDto.getUserId());
                for (Map.Entry<Long, BigDecimal> entry : userFnShareDataDto.getShareData().entrySet()) {
                    BigDecimal value = entry.getValue();
                    FnPlatWeekShareConfig config = new FnPlatWeekShareConfig();
                    config.setProjectId(entry.getKey());
                    config.setPlatId(platId);
                    config.setCreateBy(userFnShareDataDto.getUserId());
                    config.setYear(year);
                    config.setWeek(week);
                    config.setMonth(month);
                    config.setRemark(userFnShareDataDto.getRemark());
                    config.setCreateTime(new Date());

                    // 按周填写，导入时，无需处理二次分摊。生成月数据的时候，再处理。

                    if (!twoMonthWeekFlag) {
                        config.setSharePro(sharePro);
                        config.setShareAmount(value);
                    } else {
                        inserts.add(createNextMonthFnPlatWeekShareConfig(config, value, sharePro, nextMonthSharePro));
                    }
                    inserts.add(config);
                }
            }

            // 先删除数据
            fnPlatWeekShareConfigMapper.delectByUserIdsAndYearAndWeekAndPlatId(userIds, year, week, platId);

            // 插入数据
            if (inserts.size() > 0) {
            }
            fnPlatWeekShareConfigMapper.batchInsert(inserts);
        }
        String result = "";
        List<String> errorLogs = new ArrayList<>();
        int errorSizeThreshold = 3;
        if (errorLogColumn.size() > errorSizeThreshold || errorLogRow.size() > errorSizeThreshold || errorLogTail.size() > errorSizeThreshold) {
            errorLogs.addAll(errorLogHeader);
        }
        if (errorLogColumn.size() > errorSizeThreshold) {
            errorLogs.addAll(errorLogColumn);
        }
        if (errorLogRow.size() > errorSizeThreshold) {
            errorLogs.addAll(errorLogRow);
        }
        if (errorLogTail.size() > errorSizeThreshold) {
            errorLogs.addAll(errorLogTail);
        }
        if (!errorLogs.isEmpty()) {
            String errorFilePath = filePathPrefix + exportExcelPath + File.separator + project.getName() + "" + year + "年第" + week + "周异常数据.txt";
            boolean saveFlag = MyFileUtils.outputListToFile(errorLogs, errorFilePath);
            if (saveFlag) {
                result = errorFilePath.replace(filePathPrefix, "");
            }
        }

        return result;
    }

    /**
     * 前台需求，若某个项目未填写分摊项，则返回该项目的空值记录。
     *
     * @param platId
     * @param configDatas
     * @return
     */
    @Override
    public void addEmptyDataForMissedProjects(Long platId, List<WeekShareConfigVo> configDatas) {
        Project plat = projectMapper.selectByPrimaryKey(platId);
        List<Long> projectIds = configDatas.stream().map(x -> x.getProjectId()).collect(Collectors.toList());
        List<ProjectVo> shareProjects = projectMapper.selectAllShareProject();

        List<Long> favorProjectIds = fnPlatFavorProjectMapper.selectFavorProjectIdsByPlatId(platId); // 查询出平台常用项目
        List<WeekShareConfigVo> favorConfigData = new ArrayList<>();
        List<WeekShareConfigVo> otherConfigData = new ArrayList<>();

        // PlatInShareProjectMap 中的特殊平台，以及技术中心下的平台，都不需要显示 “平台-公司”列。
        if (ReportHelper.PlatInShareProjectMap.get(platId) != null || ReportHelper.PlatInShareProjectMap.get(plat.getParentShareId()) != null) {
            shareProjects = shareProjects.stream().filter(x -> !Project.Id.PLAT_COMPANY.equals(x.getId())).collect(Collectors.toList());
        }
        for (ProjectVo shareProject : shareProjects) {
            if (projectIds.contains(shareProject.getId())) {
                continue;
            }
            WeekShareConfigVo emptyOne = new WeekShareConfigVo();
            emptyOne.setProjectId(shareProject.getId());
            emptyOne.setPlatId(platId);
            emptyOne.setProjectName(shareProject.getName());
            emptyOne.setProjectUsedNames(shareProject.getUsedNamesStr());
            emptyOne.setCity(shareProject.getCity());

            // 将项目名按常用序列重排
            if (!CollectionUtils.isEmpty(favorProjectIds)) {
                if (favorProjectIds.contains(shareProject.getId())) {
                    favorConfigData.add(emptyOne);
                } else {
                    otherConfigData.add(emptyOne);
                }

            } else {
                configDatas.add(emptyOne);
            }

        }
        configDatas.addAll(favorConfigData);
        configDatas.addAll(otherConfigData);

    }

    @Override
    public List<FnWeekCommitStatusDto> getCommitLogByWeek(int year, int week) {
        List<FnWeekCommitStatusDto> result = fnWeekShareCommitLogMapper.getCommitStatusByYearAndWeek(year, week);
        if (!CollectionUtils.isEmpty(result)) {
            result.sort((x, y) -> {
                return x.getStatus().compareTo(y.getStatus());
            });
        }
        return result;
    }


    @Override
    public String getPlatFavorTemplate(Long platId) {

        Long userId = MyTokenUtils.getCurrentUserId();
        boolean platManagerFlag = getUserPlatShareManagerFlag(userId, platId);

        // 查询平台常用项目
        List<Project> favorProjects = fnPlatFavorProjectMapper.selectFavorProjects(platId);
        Project plat = projectMapper.selectByPrimaryKey(platId);
        String resultFilePath = exportExcelPath + File.separator + plat.getName() + "_人力分布导入模板.xls";

        Workbook wb = new HSSFWorkbook();
        FileOutputStream fos = null;

        // 黄色背景
        CellStyle yellowStyle = wb.createCellStyle();
        yellowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        yellowStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        yellowStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 黄色背景
        CellStyle grayStyle = wb.createCellStyle();
        grayStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        grayStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        grayStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 居中
        CellStyle midStyle = wb.createCellStyle();
        midStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);


        Sheet sheet = wb.createSheet();
        int rowIndex = 0;
        Row firstRow = sheet.createRow(rowIndex++);
        int cellIndex = 0;


        // 首行员工信息
        for (int i = 0; i < FnShareHelper.exportPlatShareConfigColums.size() - 3; i++) {
            Cell cell = firstRow.createCell(cellIndex++);
            cell.setCellValue(FnShareHelper.exportPlatShareConfigColumNameMap.get(FnShareHelper.exportPlatShareConfigColums.get(i)));
            cell.setCellStyle(midStyle);
        }

        //首行项目列
        for (Project project : favorProjects) {
            Cell projectNameCell = firstRow.createCell(cellIndex);
            projectNameCell.setCellValue(project.getName());
            projectNameCell.setCellStyle(yellowStyle);
            cellIndex++;
        }

        // 若是分摊项，或技术中心下的平台，则无需添加一列：“平台-公司”
        if (ReportHelper.PlatInShareProjectMap.get(Long.valueOf(platId)) != null ||
                (plat.getParentShareId() != null && ReportHelper.PlatInShareProjectMap.get(plat.getParentShareId()) != null)) {
            logger.info("该平台是分摊项，无需生成");
        } else {
            Cell platCompanyCell = firstRow.createCell(cellIndex);
            platCompanyCell.setCellValue("平台-公司");
            platCompanyCell.setCellStyle(yellowStyle);
            cellIndex++;
        }


        // 工作日列
        Cell projectNameCell = firstRow.createCell(cellIndex);
        projectNameCell.setCellValue("工作日");
        projectNameCell.setCellStyle(yellowStyle);
        cellIndex++;

        // 查询该平台下所管理的用户
        List<UserShareTemplateDto> allUsers = null;
        List<Long> workGroupIds = null;
        if (platManagerFlag) {
            List<HrWorkGroupDto> workGroups = workGroupMapper.selectHrWorkGroupDtoByProjectId(platId);
            workGroupIds = workGroups.stream().map(p -> p.getId()).collect(Collectors.toList());
        } else {
            workGroupIds = getAllSubGroupIds(userId, platId);
        }
        allUsers = userMapper.selectActiveUserTemplateDtoByWorkGroupIds(workGroupIds);

        try {
            // 填充用户数据
            for (UserShareTemplateDto user : allUsers) {
                Row row = sheet.createRow(rowIndex++);
                // 用户信息数据
                Cell cell1 = row.createCell(row.getPhysicalNumberOfCells());
                cell1.setCellValue(user.getEmployeeNo());

                Cell cell2 = row.createCell(row.getPhysicalNumberOfCells());
                cell2.setCellValue(user.getUserName());

                Cell cell3 = row.createCell(row.getPhysicalNumberOfCells());
                cell3.setCellValue("0".equals(user.getGender()) ? "男" : "女");

                Cell cell4 = row.createCell(row.getPhysicalNumberOfCells());
                cell4.setCellValue(user.getWorkStatus());

                Cell cell5 = row.createCell(row.getPhysicalNumberOfCells());
                cell5.setCellValue(user.getPost());

                Cell cell6 = row.createCell(row.getPhysicalNumberOfCells());
                cell6.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(user.getFirstJoinDate()));

                Cell cell7 = row.createCell(row.getPhysicalNumberOfCells());
                cell7.setCellValue(user.getWorkGroupName());

                Cell cellTotalWorkDay = row.createCell(cellIndex - 1);
                cellTotalWorkDay.setCellType(XSSFCell.CELL_TYPE_FORMULA);
                String formulaString = "SUM(H" + (rowIndex) + ":" + MyCellUtils.indexToColumn(cellIndex - 1) + (rowIndex) + ")";
                cellTotalWorkDay.setCellFormula(formulaString);
                cellTotalWorkDay.setCellStyle(grayStyle);
            }

            // 冻结首行
            sheet.createFreezePane(0, 1, 0, 1);
            fos = new FileOutputStream((new File(filePathPrefix + resultFilePath)));
            wb.write(fos);
            return resultFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        } finally {
            try {
                fos.close();
                wb.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public List<WorkdayStatusVo> getWeekShareWorkdayList(Integer year) {
        return fnWeekShareWorkdayStatusMapper.selectVoByYear(year);
    }

    @Override
    public void addWeekShareWorkday(FnWeekShareWorkdayStatus item) {
        item.setCreateTime(new Date());
        item.setOperatorId(MyTokenUtils.getCurrentUserId());
        fnWeekShareWorkdayStatusMapper.insertSelective(item);
    }

    @Override
    public boolean updateWeekShareWorkday(FnWeekShareWorkdayStatus item) {
        boolean result = false;
        FnWeekShareWorkdayStatus fnWeekShareWorkdayStatus = fnWeekShareWorkdayStatusMapper.selectByPrimaryKey(item.getId());
        if (fnWeekShareWorkdayStatus == null) {
            return result;
        }
        fnWeekShareWorkdayStatus.setUpdateTime(new Date());
        fnWeekShareWorkdayStatus.setOperatorId(MyTokenUtils.getCurrentUserId());
        if (item.getStatus() != null) {
            fnWeekShareWorkdayStatus.setStatus(item.getStatus());
        }
        if (item.getWorkday() != null) {
            fnWeekShareWorkdayStatus.setWorkday(item.getWorkday());
        }
        int i = fnWeekShareWorkdayStatusMapper.updateByPrimaryKeySelective(fnWeekShareWorkdayStatus);
        if (i > 0) {
            result = true;
        }
        return result;
    }

    @Override
    public FnWeekShareSumVo getPlatWeekShareConfigData(String userType, Long platId, Integer year, Integer week, Integer lastWeek, Integer endMonth) {
        FnWeekShareSumVo result = new FnWeekShareSumVo();

        boolean platLockFlag = false;
        List<Long> allGroupIds = new ArrayList<>();
        List<SimpleSharePlatWeekDto> currentWeekUserShareData = new ArrayList<>();
        List<SimpleSharePlatWeekDto> lastWeekUserShareData = new ArrayList<>();
        List<FnSumWeekShareConfig> userWeekShareRemarks = new ArrayList<>();
        List<UserEmployeeNoDto> userEmployeeNoDtos = new ArrayList<>();

        List<FnWeekShareSumVo.Member.GroupVo> directGroups = new ArrayList<>();
        List<FnWeekShareSumVo.Member.GroupVo> subGroups = new ArrayList<>();
        List<FnWeekShareSumVo.Member> members = new ArrayList<>();
        Map<Long, List<Long>> groupIdsBySubGroupIdMap = new HashMap<>();
        Map<Long, Boolean> lockFlagByWorkGroupIdMap = new HashMap<>();

        int lastYear = year;
        if (week == 1) {
            lastYear--;
        }

        Long currentUserId = MyTokenUtils.getCurrentUserId();
        boolean platManagerFlag = getUserPlatShareManagerFlag(currentUserId, platId);
        boolean platObserverFlag = !platManagerFlag && getUserPlatShareObserverFlag(currentUserId, platId);
        FnWeekShareWorkdayStatus weekStatus = fnWeekShareWorkdayStatusMapper.selectByYearAndWeek(year, week);
        String status = "";
        BigDecimal workDay = new BigDecimal(0);
        if (weekStatus == null || !weekStatus.getStatus()) {
            platLockFlag = true;
            status = FnWeekShareWorkdayStatus.StatusVo.unstart;
        }
        if (weekStatus != null && weekStatus.getWorkday() != null) {
            workDay = workDay.add(weekStatus.getWorkday());
        }

        if (!platLockFlag) {
            List<FnWeekShareCommitLog> commitLogs = fnWeekShareCommitLogMapper.selectByYearAndWeekAndPlatId(year, week, platId);
            if (commitLogs.size() > 0) {
                platLockFlag = true;
            }
        }
        List<IdNameBaseObject> projects = fnPlatFavorProjectMapper.selectFavorIdNameByPlatIdAndYearAndWeek(platId, year, week);

        if (platManagerFlag || platObserverFlag) {
            // 平台分摊负责人或观察者视图
            List<WorkGroup> rootTrees = workGroupService.getActiveWorkGroupTreeByProjectId(platId);
            // 直接展示的组
            for (WorkGroup item : rootTrees) {
                FnWeekShareSumVo.Member.GroupVo directGroup = new FnWeekShareSumVo.Member.GroupVo();
                directGroup.setId(item.getId());
                directGroup.setName(item.getName());
                directGroups.add(directGroup);
                allGroupIds.add(item.getId());
                // 子组,只深入1级
                if (item.getChildren() != null) {
                    for (WorkGroup workGroup : item.getChildren()) {
                        allGroupIds.add(workGroup.getId());
                        List<Long> allSubGroupIds = getAllSubGroupIds(workGroup);
                        allGroupIds.addAll(allSubGroupIds);
                        groupIdsBySubGroupIdMap.put(workGroup.getId(), allSubGroupIds);

                        FnWeekShareSumVo.Member.GroupVo subGroup = new FnWeekShareSumVo.Member.GroupVo();
                        subGroup.setId(workGroup.getId());
                        subGroup.setSubGroupFlag(true);
                        subGroup.setName(workGroup.getName());
                        subGroups.add(subGroup);
                    }
                }
            }
            userEmployeeNoDtos = userMapper.selectUserEmployeeNoDtoByProjectId(platId);
            currentWeekUserShareData = fnPlatWeekShareConfigMapper.selectSimpleSharePlatWeekDtoByPlatIdAndYearAndWeek(platId, year, week);
            lastWeekUserShareData = fnPlatWeekShareConfigMapper.selectSimpleSharePlatWeekDtoByPlatIdAndYearAndWeek(platId, lastYear, lastWeek);
            userWeekShareRemarks = fnSumWeekShareConfigMapper.selectByYearAndWeek(year, week, platId);
        } else {
            // 平台人力组组长视图
            List<HrWorkGroupDto> allGroups = workGroupMapper.selectHrWorkGroupDtoByProjectId(platId);
            Map<Long, List<HrWorkGroupDto>> groupsByParentIdMap = allGroups.stream().collect(Collectors.groupingBy(x -> x.getParent()));

            List<HrWorkGroupDto> manageGroups = new ArrayList<>();
            for (HrWorkGroupDto item : allGroups) {
                if (item.getLeaderIds() != null) {
                    for (String s : item.getLeaderIds().split(",")) {
                        if (currentUserId.equals(Long.parseLong(s))) {
                            manageGroups.add(item);
                        }
                    }
                }
            }
            List<Long> leaderIds = new ArrayList<>(2);
            leaderIds.add(currentUserId);
            // 遍历直接管辖的组，生成人力树
            for (HrWorkGroupDto manageGroup : manageGroups) {
                workGroupService.trackHrGroupTreeForRootGroup(leaderIds, manageGroup, groupsByParentIdMap, new HashMap<>(), new HashMap<>(), false);
                // 直接管辖的组
                FnWeekShareSumVo.Member.GroupVo directGroup = new FnWeekShareSumVo.Member.GroupVo();
                directGroup.setId(manageGroup.getId());
                directGroup.setName(manageGroup.getName());
                directGroups.add(directGroup);
                allGroupIds.add((manageGroup.getId()));
                // 子组,只深入1级
                if (manageGroup.getChildWorkGroups() != null) {
                    for (HrWorkGroupDto hrWorkGroupDto : manageGroup.getChildWorkGroups()) {
                        allGroupIds.add(hrWorkGroupDto.getId());
                        List<Long> allSubGroupIds = getAllSubGroupIds(hrWorkGroupDto);
                        allGroupIds.addAll(allSubGroupIds);
                        groupIdsBySubGroupIdMap.put(hrWorkGroupDto.getId(), allSubGroupIds);

                        FnWeekShareSumVo.Member.GroupVo subGroup = new FnWeekShareSumVo.Member.GroupVo();
                        subGroup.setId(hrWorkGroupDto.getId());
                        subGroup.setName(hrWorkGroupDto.getName());
                        subGroup.setSubGroupFlag(true);
                        subGroups.add(subGroup);
                    }
                }
            }
            if (!allGroupIds.isEmpty()) {
                userEmployeeNoDtos = userMapper.selectActiveUserEmployeeNoDtoByWorkGroupIds(allGroupIds);
                List<Long> userIds = userEmployeeNoDtos.stream().map(x -> x.getUserId()).collect(Collectors.toList());
                if (!userIds.isEmpty()) {
                    currentWeekUserShareData = fnPlatWeekShareConfigMapper.selectSimpleSharePlatWeekDtoByPlatIdAndYearAndWeekAndUserIds(platId, year, week, userIds);
                    lastWeekUserShareData = fnPlatWeekShareConfigMapper.selectSimpleSharePlatWeekDtoByPlatIdAndYearAndWeekAndUserIds(platId, lastYear, lastWeek, userIds);
                    userWeekShareRemarks = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserIds(year, week, userIds);
                }
            }
        }

        if (!allGroupIds.isEmpty()) {
            List<FnWeekShareStatus> groupStatues = fnWeekShareStatusMapper.selectLockedGroupByYearAndWeekAndGroupIds(year, week, allGroupIds);
            for (FnWeekShareStatus item : groupStatues) {
                lockFlagByWorkGroupIdMap.put(item.getWorkGroupId(), item.getLockFlag());
            }
        }

        // 开始向人力组回填人员分摊数据
        Map<Long, List<SimpleSharePlatWeekDto>> currentWeekShareByUserIdsMap = currentWeekUserShareData.stream().collect(Collectors.groupingBy(x -> x.getCreateBy()));
        Map<Long, List<SimpleSharePlatWeekDto>> lastWeekShareByUserIdsMap = lastWeekUserShareData.stream().collect(Collectors.groupingBy(x -> x.getCreateBy()));
        Map<Long, FnSumWeekShareConfig> userRemarkByUserIdMap = userWeekShareRemarks.stream().collect(Collectors.toMap(x -> x.getUserId(), y -> y));
        Map<Long, List<UserEmployeeNoDto>> userByWorkGroupIdMap = userEmployeeNoDtos.stream().collect(Collectors.groupingBy(x -> x.getWorkGroupId()));

        Iterator<FnWeekShareSumVo.Member.GroupVo> groupIterator = directGroups.iterator();
        Iterator<FnWeekShareSumVo.Member.GroupVo> subGroupIterator = subGroups.iterator();

        boolean groupAllLockedFlag = true;
        // 回填直接管理的组
        while (groupIterator.hasNext()) {
            FnWeekShareSumVo.Member.GroupVo item = groupIterator.next();
            // todo: 这把锁只能针对只有锁定人力组的业务情况，如果业务增加锁定人的情况，要重构这把锁
            Boolean groupLockFlag = lockFlagByWorkGroupIdMap.get(item.getId());
            if (groupLockFlag == null) {
                groupLockFlag = false;
            }
            if (!groupLockFlag) {
                groupAllLockedFlag = false;
            }
            item.setLockFlag(groupLockFlag);

            if (userByWorkGroupIdMap.containsKey(item.getId())) {
                members.addAll(getGroupUserWeekShareDto(projects, userByWorkGroupIdMap.get(item.getId()), currentWeekShareByUserIdsMap, lastWeekShareByUserIdsMap, userRemarkByUserIdMap, groupLockFlag, item));
            } else {
                groupIterator.remove();
            }
        }

        // 回填子组
        while (subGroupIterator.hasNext()) {
            FnWeekShareSumVo.Member.GroupVo subGroup = subGroupIterator.next();

            // todo: 这把锁只能针对只有锁定人力组的业务情况，如果业务增加锁定人的情况，要重构这把锁
            Boolean groupLockFlag = lockFlagByWorkGroupIdMap.get(subGroup.getId());
            if (groupLockFlag == null) {
                groupLockFlag = false;
            }
            if (!groupLockFlag) {
                groupAllLockedFlag = false;
            }
            subGroup.setLockFlag(groupLockFlag);

            // 子组下面还有层级，需要把子组下面的所有层级的员工都拿到
            List<Long> groupIds = groupIdsBySubGroupIdMap.get(subGroup.getId());
            groupIds.add(subGroup.getId());
            List<UserEmployeeNoDto> userShareDtos = new ArrayList<>();
            for (Long groupId : groupIds) {
                if (userByWorkGroupIdMap.containsKey(groupId)) {
                    userShareDtos.addAll(userByWorkGroupIdMap.get(groupId));
                }
            }
            if (userShareDtos.isEmpty()) {
                subGroupIterator.remove();
            } else {
                members.addAll(getGroupUserWeekShareDto(projects, userShareDtos, currentWeekShareByUserIdsMap, lastWeekShareByUserIdsMap, userRemarkByUserIdMap, groupLockFlag, subGroup));
            }
        }

        // 人力组长视图 && 平台没有被锁
        if (!platManagerFlag && !platObserverFlag && !platLockFlag) {
            // 判断管的人力组是否全部被锁定，如果是，人力组长的视图也算被锁
            platLockFlag = groupAllLockedFlag;
        }

        if (platLockFlag && status.isEmpty()) {
            status = FnWeekShareWorkdayStatus.StatusVo.locked;
        }

        result.setMembers(members);
        result.setProjects(projects);
        result.setLockFlag(platLockFlag);
        result.setWorkday(workDay);
        result.setStatus(status);
        return result;
    }

    @Override
    public PlatFavorProjectInfoVo getPlatFavorProjectInfo(Long platId, Integer year, Integer week) {
        PlatFavorProjectInfoVo result = new PlatFavorProjectInfoVo();
        result.setShareProjects(projectMapper.selectUnfavorShareProjectIdAndNamesByPlatId(platId, year, week));
        result.setFavorProjects(fnPlatFavorProjectMapper.selectFavorIdNameByPlatIdAndYearAndWeek(platId, year, week));
        return result;
    }

    @Override
    @Transactional
    public Integer setPlatFavorProject(Long platId, List<Long> projectIds) {
        if (projectIds.isEmpty()) {
            return 0;
        }
        fnPlatFavorProjectMapper.deleteByPlatId(platId);
        List<FnPlatFavorProject> inserts = new ArrayList<>();
        int index = 1;
        for (Long projectId : projectIds) {
            FnPlatFavorProject item = new FnPlatFavorProject();
            item.setPlatId(platId);
            item.setSort(index);
            item.setFavorProjectId(projectId);
            inserts.add(item);
            index++;
        }
        fnPlatFavorProjectMapper.batchInsert(inserts);
        return null;
    }

    private List<FnWeekShareSumVo.Member> getGroupUserWeekShareDto(List<IdNameBaseObject> projects, List<UserEmployeeNoDto> users, Map<Long, List<SimpleSharePlatWeekDto>> currentWeekShareByUserIdsMap, Map<Long, List<SimpleSharePlatWeekDto>> lastWeekShareByUserIdsMap, Map<Long, FnSumWeekShareConfig> userRemarkByUserIdMap, boolean lockFlag, FnWeekShareSumVo.Member.GroupVo workGroup) {
        List<FnWeekShareSumVo.Member> result = new ArrayList<>();
        if (users == null) {
            return result;
        }
        BigDecimal zero = new BigDecimal(0);
        for (UserEmployeeNoDto user : users) {
            FnWeekShareSumVo.Member member = new FnWeekShareSumVo.Member();
            member.setId(user.getUserId());
            member.setName(user.getUserName());
            member.setWorkGroup(workGroup);
            member.setLockFlag(lockFlag);

            FnSumWeekShareConfig userRemark = userRemarkByUserIdMap.get(user.getUserId());
            if (userRemark != null) {
                member.setRemarkId(userRemark.getId());
                member.setRemark(userRemark.getRemark());
            }

            List<SimpleSharePlatWeekDto> shareData = currentWeekShareByUserIdsMap.get(user.getUserId());
            List<SimpleSharePlatWeekDto> lastWeekShareData = lastWeekShareByUserIdsMap.get(user.getUserId());

            if (shareData == null) {
                shareData = new ArrayList<>();
            }

            if (lastWeekShareData == null) {
                lastWeekShareData = new ArrayList<>();
            }

            Map<Long, SimpleSharePlatWeekDto> shareDataByProjectIdMap = shareData.stream().collect(Collectors.toMap(x -> x.getProjectId(), x -> x));
            Map<Long, SimpleSharePlatWeekDto> lastWeekShareDataByProjectIdMap = lastWeekShareData.stream().collect(Collectors.toMap(x -> x.getProjectId(), x -> x));
            List<FnWeekShareSumVo.Member.ShareDto> data = new ArrayList<>();
            BigDecimal totalShareAmount = new BigDecimal(0);
            for (IdNameBaseObject project : projects) {
                FnWeekShareSumVo.Member.ShareDto shareDto = new FnWeekShareSumVo.Member.ShareDto();

                SimpleSharePlatWeekDto userProjectShareData = shareDataByProjectIdMap.get(project.getId());
                SimpleSharePlatWeekDto lastWeekUserProjectShareData = lastWeekShareDataByProjectIdMap.get(project.getId());
                if (userProjectShareData != null) {
                    simpleSharePlatWeekDtoToShareDto(userProjectShareData, shareDto);
                    totalShareAmount = totalShareAmount.add(userProjectShareData.getShareAmount());
                } else {
                    shareDto.setShareAmount(zero);
                    shareDto.setProjectId(project.getId());
                }
                if (lastWeekUserProjectShareData != null) {
                    shareDto.setLastWeekValue(lastWeekUserProjectShareData.getShareAmount());
                } else {
                    shareDto.setLastWeekValue(zero);
                }

                data.add(shareDto);
            }

            member.setTotalShareAmount(totalShareAmount);
            member.setData(data);
            result.add(member);
        }

        Collections.sort(result, new Comparator<FnWeekShareSumVo.Member>() {
            @Override
            public int compare(FnWeekShareSumVo.Member o1, FnWeekShareSumVo.Member o2) {
                return o2.getTotalShareAmount().compareTo(o1.getTotalShareAmount());
            }
        });
        return result;
    }

    private void simpleSharePlatWeekDtoToShareDto(SimpleSharePlatWeekDto source, FnWeekShareSumVo.Member.ShareDto target) {
        target.setId(source.getId());
        target.setProjectId(source.getProjectId());
        target.setRemark(source.getRemark());
        target.setShareAmount(source.getShareAmount());
    }


    private List<Long> getPlatMemberIdsByManagerId(Boolean platManagerFlag, Long userId, Long platId) {
        return getPlatMembersByManagerId(platManagerFlag, userId, platId).stream().map(x -> x.getUserId()).collect(Collectors.toList());
    }

    private List<Long> getManageWorkGroupIdsByUserId(Long userId, Long platId) {
        List<WorkGroupHrDto> workGroups = workGroupMapper.selectActiveWorkGroupHrDtoByProjectId(platId);
        return getManageWorkGroupIdsByUserId(userId, workGroups);
    }

    private List<Long> getManageWorkGroupIdsByUserId(Long userId, List<WorkGroupHrDto> workGroups) {
        List<Long> result = new ArrayList<>();
        for (WorkGroupHrDto workGroup : workGroups) {
            if (userId.equals(workGroup.getHrId())) {
                result.add(workGroup.getId());
            }
        }
        return result;
    }

    private List<Long> getAllSubGroupIds(Long userId, Long platId) {
        Set<Long> subGroupIdSet = new HashSet<>();
        List<WorkGroupHrDto> workGroups = workGroupMapper.selectActiveWorkGroupHrDtoByProjectId(platId);
        List<Long> manageGroupIds = getManageWorkGroupIdsByUserId(userId, workGroups);
        if (manageGroupIds.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<WorkGroupHrDto>> workGroupByParentIdMap = workGroups.stream().collect(Collectors.groupingBy(x -> x.getParent()));
        for (Long manageGroupId : manageGroupIds) {
            subGroupIdSet.add(manageGroupId);
            subGroupIdSet.addAll(workGroupService.getSubGroupIds(manageGroupId, workGroupByParentIdMap));
        }
        return new ArrayList<>(subGroupIdSet);
    }

    private List<Long> getAllUnLockedSubGroupIds(int year, int week, Long userId, Long platId) {
        Set<Long> subGroupIdSet = new HashSet<>();
        List<WorkGroupHrDto> workGroups = workGroupMapper.selectActiveWorkGroupHrDtoByProjectId(platId);
        List<Long> manageGroupIds = getManageWorkGroupIdsByUserId(userId, workGroups);
        if (manageGroupIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<FnWeekShareStatus> fnWeekShareStatusLockedList = fnWeekShareStatusMapper.selectLockedGroupByYearAndWeekAndGroupIds(year, week, manageGroupIds);
        Map<Long, List<WorkGroupHrDto>> workGroupByParentIdMap = workGroups.stream().collect(Collectors.groupingBy(x -> x.getParent()));
        for (Long manageGroupId : manageGroupIds) {
            if (isWorkGroupLocked(fnWeekShareStatusLockedList, manageGroupId)) {
                continue;
            }
            subGroupIdSet.add(manageGroupId);
            subGroupIdSet.addAll(workGroupService.getSubGroupIds(manageGroupId, workGroupByParentIdMap));
        }
        return new ArrayList<>(subGroupIdSet);
    }


    private boolean isWorkGroupLocked(List<FnWeekShareStatus> fnWeekShareStatusLockedList, Long manageGroupId) {
        boolean groupLockFlag = false;
        for (FnWeekShareStatus lockedWorkGroup : fnWeekShareStatusLockedList) {
            if (lockedWorkGroup.getId().equals(manageGroupId)) {
                groupLockFlag = true;
                break;
            }
        }
        return groupLockFlag;
    }

    private boolean getUserPlatShareManagerFlag(Long userId, Long platId) {
        // 业务需求，后台项目管理这个角色，也拥有和平台分摊负责人相同的权限
        return getUserPlatShareRoleFlag(userId, platId, Arrays.asList(ProjectRole.Role.plat_share_manager_id, ProjectRole.Role.pm_manager_id));
    }

    private boolean getUserPlatShareObserverFlag(Long userId, Long platId) {
        return getUserPlatShareRoleFlag(userId, platId, Arrays.asList(ProjectRole.Role.plat_share_observer_id));
    }

    private boolean getUserPlatShareRoleFlag(Long userId, Long platId, List<Long> roleIdList) {
        if (!CollectionUtils.isEmpty(roleIdList)) {
            List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByUserId(userId);
            for (RUserProjectPerm item : rUserProjectPerms) {
                if (roleIdList.contains(item.getProjectRoleId()) && (item.getProjectId() == null || item.getProjectId().equals(platId))) {
                    return true;
                }
            }
        }
        return false;
    }

    private FnPlatWeekShareConfig createNextMonthFnPlatWeekShareConfig(FnPlatWeekShareConfig config, BigDecimal totalShareAmount, BigDecimal sharePro, BigDecimal nextMonthsharePro) {
        FnPlatWeekShareConfig result = new FnPlatWeekShareConfig();

        Integer month = config.getMonth();
        BeanUtils.copyProperties(config, result);
        config.setSharePro(sharePro);
        config.setShareAmount(totalShareAmount.multiply(sharePro));
        // 这里出现跨年情况时，month=13，没问题，因为年-周必须统一才能汇总。此外，在按周生成月数据时，特殊处理month=13的情况。
        result.setMonth(month + 1);
        result.setShareAmount(totalShareAmount.subtract(config.getShareAmount()));
        result.setSharePro(nextMonthsharePro);
        return result;
    }

    private boolean isLeaderInGroupList(List<HrWorkGroupDto> groupDtoList, Long groupId, Long userId) {
        if (groupId != null && userId != null) {
            for (HrWorkGroupDto groupDto: groupDtoList) {
                if (groupId.equals(groupDto.getId()) && StringUtils.isNotBlank(groupDto.getLeaderIds())
                        && Arrays.asList(groupDto.getLeaderIds().split(",")).contains(String.valueOf(userId))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean chechUpdateWeekSharePermission(Long platId, Long workGroupId, Long userId) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        return currentUserId.equals(userId) || getUserPlatShareManagerFlag(platId, currentUserId) || rUserWorkGroupService.selectByUserIdAndWorkGroupId(workGroupId, currentUserId) != null;
    }


}
