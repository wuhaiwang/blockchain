package com.seasun.management.service.dataCenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.annotation.DataSourceTarget;
import com.seasun.management.common.KsHttpComponent;
import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.constant.BossDataConstant;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.dataCenter.*;
import com.seasun.management.helper.dataCenter.ProjectConfigIdHelper;
import com.seasun.management.mapper.CfgSystemParamMapper;
import com.seasun.management.mapper.PmQualityProjectRelationMapper;
import com.seasun.management.mapper.dataCenter.*;
import com.seasun.management.model.PmQualityProjectRelation;
import com.seasun.management.model.dataCenter.Jx2Loginstat;
import com.seasun.management.model.dataCenter.VDWChargeLog;
import com.seasun.management.util.MyDateUtils;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.BaseQueryResponseVo;
import com.seasun.management.vo.dataCenter.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.seasun.management.helper.dataCenter.TimeDivisionHelper.*;

@Service
public class DataCenterSampleServiceImpl implements DataCenterSampleService {

    private static final Logger logger = LoggerFactory.getLogger(DataCenterSampleServiceImpl.class);

    @Autowired
    Jx2LoginstatMapper jx2LoginstatMapper;

    @Autowired
    StatOnlineAccountMapper statOnlineAccountMapper;

    @Autowired
    VDWChargeLogMapper vDWChargeLogMapper;

    @Autowired
    VFactJx3StatDMapper vFactJx3StatDMapper;

    @Autowired
    CfgSystemParamMapper cfgSystemParamMapper;

    @Autowired
    VStatChargeRealTimeMapper vStatChargeRealTimeMapper;

    @Value("${data.center.url}")
    private String address;


    @Value("${data.quality.request.url}")
    private String qualityRequestAddress;

    @Value("${data.quality.response.url}")
    private String qualityResponseAddress;


    @Autowired
    private PmQualityProjectRelationMapper pmQualityProjectRelationMapper;
    @Override
    @DataSourceTarget(name = "secondDataSource")
    public List<Jx2Loginstat> getAllLoginInfoByCond(String startTime, String endTime, String type) {
        return jx2LoginstatMapper.selectAllWithDateAndType(startTime, endTime, type);
    }

    @Override
    @DataSourceTarget(name = "secondDataSource")
    public RealTimeOnlineDataVo getRealTimeDataCollection(Date onlineDate, Date chargeSumDate,
                                                          Date dailyDate, String project) {
        RealTimeOnlineDataVo realTimeOnlineDataVo = new RealTimeOnlineDataVo();
        //非实时数据
        DataSourceContextHolder.usePrimary();
        if (!MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.ReportBossRealTimeFLag,
                MySystemParamUtils.DefaultValue.reportBossRealTimeFlag)) {
            logger.info("not real time data...");
            Calendar onlineCalender = Calendar.getInstance();
            onlineCalender.set(2017, 5, 8);//2017-6-8
            onlineDate = onlineCalender.getTime();

            Calendar chargeSumCalender = Calendar.getInstance();
            chargeSumCalender.set(2017, 5, 7);//2017-6-7
            chargeSumDate = chargeSumCalender.getTime();

            Calendar dailyCalender = Calendar.getInstance();
            dailyCalender.set(2017, 5, 7);//2017-6-7
            dailyDate = dailyCalender.getTime();
        }

        DataSourceContextHolder.useSecond();
        logger.info("real time data ...");
        //实时数据v2
        realTimeOnlineDataVo.setStatOnlineNum(getStatOnlineNum(onlineDate, project));
        realTimeOnlineDataVo.setOnlineChargeSum(getOnlineChargeSum(chargeSumDate, project));
        realTimeOnlineDataVo.setDaily(getDaily(dailyDate));

        return realTimeOnlineDataVo;
    }

    /**
     * 当天和前一天的实时在线人数
     *
     * @return 时间和在线人数集合，时间从 00:00 到 24:00 每隔半小时
     */
    private StatOnlineNumVo getStatOnlineNum(Date onlineDate, String project) {
        logger.info("get Stat Online Num ...");
        StatOnlineNumVo statOnlineNumVo = new StatOnlineNumVo();
        List<String> timeStrings = getPreCurNexTimeStrings(onlineDate);
        List<StatOnlineAccountDto> JX3TimeRangePeopleAccountList = statOnlineAccountMapper.selectStatAccountByConfGameId(timeStrings.get(0), timeStrings.get(2), ProjectConfigIdHelper.projectConfigIdMap.get(project));
        if (JX3TimeRangePeopleAccountList == null || JX3TimeRangePeopleAccountList.size() == 0) {
            return statOnlineNumVo;
        }
        List<List<StatOnlineAccountDto>> previousAndCurrentData = dividedPreviousCurrentData(JX3TimeRangePeopleAccountList, timeStrings.get(0), timeStrings.get(1));

        //将分类的dto 中，时间点补齐返回
        statOnlineNumVo.setPreviousData(filledAndAccurateStatOnlineData(previousAndCurrentData.get(0), timeStrings.get(0) + " "));
        statOnlineNumVo.setCurrentData(filledAndAccurateStatOnlineData(previousAndCurrentData.get(1), timeStrings.get(1) + " "));
        return statOnlineNumVo;
    }


    /**
     * 从充值记录中查询出当天每隔半小时，三类充值
     *
     * @return 当天实时充值记录
     */
    private List<OnlineChargeVo> getOnlineCharge(Date chargeDate) {
        List<String> timeStrings = getPreCurNexTimeStrings(chargeDate);
        List<VDWChargeLogDto> onlineChargeDtoList = vDWChargeLogMapper.selectOnlineChargeDto(timeStrings.get(1), timeStrings.get(2));
        List<VDWChargeLogDto> onlineChargeAdjustedDtoList = adjustChargeLogDtoTime(onlineChargeDtoList);
        List<OnlineChargeVo> typeCalculatedVoList = calculateChargeFromType(onlineChargeAdjustedDtoList);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dayString = simpleDateFormat.format(chargeDate);
        return addAllHalfHourChargeData(fillChargeData(calculateChargeFromTime(typeCalculatedVoList), dayString + " "));
    }

    private List<OnlineChargeSumVo> getOnlineChargeSum(Date chargeDate, String project) {
        logger.info("get Online Online Charge Sum ...");
        List<String> timeStrings = getPreCurNexTimeStrings(chargeDate);
        List<VStatChargeRealTimeDto> onlineChargeSumList = vStatChargeRealTimeMapper.selectOnlineChargeSumByConfGameId(timeStrings.get(1), timeStrings.get(2), ProjectConfigIdHelper.projectConfigIdMap.get(project));
        List<VStatChargeRealTimeDto> onlineChargeSumAdjustedDtoList = adjustChargeSumDtoTime(onlineChargeSumList);
        logger.info("after adjusted charge sum time ,list.size=" + onlineChargeSumAdjustedDtoList.size());
        List<VStatChargeRealTimeDto> calculatedList = calculateChargeSumFromTime(onlineChargeSumAdjustedDtoList);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dayString = simpleDateFormat.format(chargeDate);
        return addAllHalfHourChargeSumData(filledChargeSumVo(calculatedList, dayString + " "));
    }


    /**
     * 每日总登录账号数，每日首登账号数，每日总充值金额(元)，每日消耗金额(元)
     * 增值服务消耗，每日充值账号数，每日新增充值账号数 的指标、日环比、月环比
     *
     * @return 日报
     */
    private DailyVo getDaily(Date dailyDate) {
        logger.info("get Daily ...");
        DateStringCollection times = getTimeStrings(dailyDate);
        List<VFactJx3StatDDto> jx3StatDList = vFactJx3StatDMapper.selectJX3TimeRangeStatD(times.getLastBeginOfMonthDate(), times.getCurrentDate());
        return calculateTargetGrowthTrend(jx3StatDList, times);
    }


    private List<List<StatOnlineAccountDto>> dividedPreviousCurrentData(List<StatOnlineAccountDto> totalDataList, String previousDate, String currentDate) {
        List<List<StatOnlineAccountDto>> result = new ArrayList<>();
        List<StatOnlineAccountDto> previousAccountDto = new ArrayList<>();
        List<StatOnlineAccountDto> currentAccountDto = new ArrayList<>();
        for (StatOnlineAccountDto dto : totalDataList) {
            String dtoTime = dto.getStatTime();
            if (isContainsLegitimateTime(dtoTime) && dtoTime.contains(previousDate)) {
                previousAccountDto.add(dto);
            } else if (isContainsLegitimateTime(dtoTime) && dtoTime.contains(currentDate)) {
                currentAccountDto.add(dto);
            }
        }
        result.add(previousAccountDto);
        result.add(currentAccountDto);
        return result;
    }

    /**
     * 将所有的记录，整理，中间不留时间点空隙，并且将“实时在线人数”单位转换为k
     *
     * @param originList 原始集
     * @param dayString  要填充的时间点的日期
     * @return 转换后的list
     */
    private List<StatOnlineAccountDto> filledAndAccurateStatOnlineData(List<StatOnlineAccountDto> originList, String dayString) {
        if (originList.size() >= 48) {
            for (StatOnlineAccountDto dto : originList) {
                dto.setAccountSum(dto.getAccountSum().divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_EVEN));
            }
            return originList;
        }
        List<StatOnlineAccountDto> resultDtoList = new ArrayList<>();
        String[] formatTimeString = getLegitimateTimeStamp();
        int i = 0;
        for (int j = 0; j < originList.size() && i < formatTimeString.length; ) {
            if (formatTimeString[i].equals(originList.get(j).getStatTime().substring(11))) {
                originList.get(j).setAccountSum(originList.get(j).getAccountSum().divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_EVEN));
                resultDtoList.add(originList.get(j));
                j++;
            } else {
                StatOnlineAccountDto noneAccountTimeDto = new StatOnlineAccountDto();
                noneAccountTimeDto.setStatTime(dayString + formatTimeString[i]);
                noneAccountTimeDto.setAccountSum(new BigDecimal(0));
                resultDtoList.add(noneAccountTimeDto);
            }
            i++;
        }

        return resultDtoList;
    }

    /**
     * 将所有半个小时内的充值总值，整理，中间不留时间点空隙
     *
     * @param calculatedList 已经按类型时间汇总的 充值总额
     * @param dayString      要填充的时间点的日期
     * @return 转换后的list
     */
    private List<OnlineChargeVo> fillChargeData(List<OnlineChargeVo> calculatedList, String dayString) {
        List<OnlineChargeVo> result = new ArrayList<>();
        OnlineChargeVo filledBegin = new OnlineChargeVo();
        filledBegin.setTime(dayString + "00:00");
        filledBegin.setGameCoins(new BigDecimal(0));
        filledBegin.setMonthCard(new BigDecimal(0));
        filledBegin.setDayCard(new BigDecimal(0));
        result.add(filledBegin);

        if (calculatedList.size() >= 48) {
            result.addAll(calculatedList);
            return result;
        }
        List<OnlineChargeVo> resultChargeVo = new ArrayList<>();
        String[] formatTimeString = getLegitimateTimeStamp();
        int i = 1;
        for (OnlineChargeVo onlineChargeVo : calculatedList) {
            if ((dayString + formatTimeString[i]).equals(onlineChargeVo.getTime())) {
                resultChargeVo.add(onlineChargeVo);
            } else {
                OnlineChargeVo filledVo = new OnlineChargeVo();
                filledVo.setTime(dayString + formatTimeString[i]);
                filledVo.setGameCoins(new BigDecimal(0));
                filledVo.setMonthCard(new BigDecimal(0));
                filledVo.setDayCard(new BigDecimal(0));
                resultChargeVo.add(filledVo);
            }
            i++;
        }
        result.addAll(resultChargeVo);
        return result;
    }

    /**
     * 当calcutedList 刚好48 个时刻时（没有零点）,补齐零点
     * 当不足够 48 个点时： 添加00:00 ，补齐  00:30~23:30
     *
     * @param calculatedList calculatedList 一定是小于48 时，补齐 00:30 以后的断点
     * @param dayString      今天日期
     * @return 补齐以后的结果集
     */
    private List<OnlineChargeSumVo> filledChargeSumVo(List<VStatChargeRealTimeDto> calculatedList, String dayString) {
        // 补齐开头
        List<OnlineChargeSumVo> resultChargeSumVo = new ArrayList<>();
        OnlineChargeSumVo filledBegin = new OnlineChargeSumVo();
        filledBegin.setTime(dayString + "00:00");
        filledBegin.setChargeValue(new BigDecimal(0));
        resultChargeSumVo.add(filledBegin);

        if (calculatedList.size() >= 48) {
            for (VStatChargeRealTimeDto onlineChargeVo : calculatedList) {
                OnlineChargeSumVo filledVo = new OnlineChargeSumVo();
                filledVo.setTime(onlineChargeVo.getEndTime());
                filledVo.setChargeValue(onlineChargeVo.getChargeValue());
                resultChargeSumVo.add(filledVo);
            }
            return resultChargeSumVo;
        }

        // 补齐后续
        String[] formatTimeString = getLegitimateTimeStamp();
        int i = 1;
        for (VStatChargeRealTimeDto onlineChargeVo : calculatedList) {
            if ((dayString + formatTimeString[i]).equals(onlineChargeVo.getEndTime())) {
                OnlineChargeSumVo filledVo = new OnlineChargeSumVo();
                filledVo.setTime(onlineChargeVo.getEndTime());
                filledVo.setChargeValue(onlineChargeVo.getChargeValue());
                resultChargeSumVo.add(filledVo);
            } else {
                OnlineChargeSumVo filledVo = new OnlineChargeSumVo();
                filledVo.setTime(dayString + formatTimeString[i]);
                filledVo.setChargeValue(new BigDecimal(0));
                resultChargeSumVo.add(filledVo);
            }
            i++;
        }
        return resultChargeSumVo;
    }

    /**
     * 将半个小时的值累加
     *
     * @param filledChargeData 每隔半个小时的充值记录
     * @return
     */
    private List<OnlineChargeVo> addAllHalfHourChargeData(List<OnlineChargeVo> filledChargeData) {
        BigDecimal dayCardSumAccount = new BigDecimal(0);
        BigDecimal monthCardSumAccount = new BigDecimal(0);
        BigDecimal gameCoinsSumAccount = new BigDecimal(0);
        for (OnlineChargeVo vo : filledChargeData) {
            dayCardSumAccount = dayCardSumAccount.add(vo.getDayCard());
            monthCardSumAccount = monthCardSumAccount.add(vo.getMonthCard());
            gameCoinsSumAccount = gameCoinsSumAccount.add(vo.getGameCoins());
            vo.setDayCard(dayCardSumAccount.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN));
            vo.setMonthCard(monthCardSumAccount.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN));
            vo.setGameCoins(gameCoinsSumAccount.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN));
        }
        return filledChargeData;
    }

    private List<OnlineChargeSumVo> addAllHalfHourChargeSumData(List<OnlineChargeSumVo> onlineChargeSumVoList) {
        BigDecimal chargeValue = new BigDecimal(0);
        for (OnlineChargeSumVo vo : onlineChargeSumVoList) {
            chargeValue = chargeValue.add(vo.getChargeValue());
            vo.setChargeValue(chargeValue.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN));
        }
        return onlineChargeSumVoList;
    }


    private List<OnlineChargeVo> calculateChargeFromTime(List<OnlineChargeVo> typeCalculatedVoList) {
        List<OnlineChargeVo> resultList = new ArrayList<>();
        if (typeCalculatedVoList == null || typeCalculatedVoList.size() == 0) {
            return resultList;
        }
        resultList.add(typeCalculatedVoList.get(0));
        for (int i = 1; i < typeCalculatedVoList.size(); i++) {
            if (typeCalculatedVoList.get(i).getTime().equals(resultList.get(resultList.size() - 1).getTime())) {
                OnlineChargeVo lastElement = resultList.get(resultList.size() - 1);
                lastElement.setDayCard(lastElement.getDayCard().add(typeCalculatedVoList.get(i).getDayCard()));
                lastElement.setGameCoins(lastElement.getGameCoins().add(typeCalculatedVoList.get(i).getGameCoins()));
                lastElement.setMonthCard(lastElement.getMonthCard().add(typeCalculatedVoList.get(i).getMonthCard()));
            } else {
                resultList.add(typeCalculatedVoList.get(i));
            }
        }
        return resultList;
    }


    private List<VStatChargeRealTimeDto> calculateChargeSumFromTime(List<VStatChargeRealTimeDto> chargeSumList) {
        List<VStatChargeRealTimeDto> resultList = new ArrayList<>();
        if (chargeSumList == null || chargeSumList.size() == 0) {
            return chargeSumList;
        }
        resultList.add(chargeSumList.get(0));
        for (int i = 1; i < chargeSumList.size(); i++) {
            if (chargeSumList.get(i).getEndTime().equals(resultList.get(resultList.size() - 1).getEndTime())) {
                VStatChargeRealTimeDto lastElement = resultList.get(resultList.size() - 1);
                lastElement.setChargeValue(lastElement.getChargeValue().add(chargeSumList.get(i).getChargeValue()));
            } else {
                resultList.add(chargeSumList.get(i));
            }
        }
        return resultList;
    }

    private List<OnlineChargeVo> calculateChargeFromType(List<VDWChargeLogDto> vdwChargeLogDtoList) {
        List<OnlineChargeVo> onlineChargeVoList = new ArrayList<>();
        for (VDWChargeLogDto dto : vdwChargeLogDtoList) {
            OnlineChargeVo vo = new OnlineChargeVo();
            BigDecimal dayCardAmount = new BigDecimal(0);
            BigDecimal monthCardAmount = new BigDecimal(0);
            BigDecimal gameCoinsAmount = new BigDecimal(0);
            switch (dto.getFillType().intValue()) {
                case VDWChargeLog.FillType.dayCardType:
                    dayCardAmount = dayCardAmount.add(dto.getCardType().multiply(dto.getCardAmount()));
                    break;
                case VDWChargeLog.FillType.monthCardType:
                    monthCardAmount = monthCardAmount.add(dto.getCardType().multiply(dto.getCardAmount()));
                    break;
                case VDWChargeLog.FillType.gameCoinsType:
                    gameCoinsAmount = gameCoinsAmount.add(dto.getCardType().multiply(dto.getCardAmount()));
                    break;
                default:
                    break;
            }
            vo.setDayCard(dayCardAmount);
            vo.setMonthCard(monthCardAmount);
            vo.setGameCoins(gameCoinsAmount);
            vo.setTime(dto.getFillDatetime());
            onlineChargeVoList.add(vo);
        }
        return onlineChargeVoList;
    }

    private List<VDWChargeLogDto> adjustChargeLogDtoTime(List<VDWChargeLogDto> originChargeLogDtoList) {
        for (VDWChargeLogDto vdwChargeLogDto : originChargeLogDtoList) {
            String originTime = getFormatTime(vdwChargeLogDto.getFillDatetime());
            vdwChargeLogDto.setFillDatetime(originTime);
        }
        return originChargeLogDtoList;
    }

    private List<VStatChargeRealTimeDto> adjustChargeSumDtoTime(List<VStatChargeRealTimeDto> originList) {
        for (VStatChargeRealTimeDto dto : originList) {
            String adjustedTime = getFormatTime(dto.getEndTime());
            dto.setEndTime(adjustedTime);
        }
        return originList;
    }


    private List<String> getPreCurNexTimeStrings(Date currentDate) {
        List<String> timeStrings = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -1);
        Date previousDate = c.getTime();
        c.add(Calendar.DATE, 2);
        Date nextDate = c.getTime();

        String previousDateString = sdf.format(previousDate);
        String currentDateString = sdf.format(currentDate);
        String nextDateString = sdf.format(nextDate);
        timeStrings.add(previousDateString);
        timeStrings.add(currentDateString);
        timeStrings.add(nextDateString);
        return timeStrings;
    }

    private DateStringCollection getTimeStrings(Date currentDate) {

        DateStringCollection dateStringCollection = new DateStringCollection();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //当天 eg：2017-01-05
        dateStringCollection.setCurrentDate(simpleDateFormat.format(currentDate));

        //昨天 eg: 2017-01-04
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.setTime(currentDate);
        yesterdayCalendar.add(Calendar.DATE, -1);
        dateStringCollection.setYesterdayDate(simpleDateFormat.format(yesterdayCalendar.getTime()));

        //大前天 eg: 2017-01-03
        yesterdayCalendar.setTime(yesterdayCalendar.getTime());
        yesterdayCalendar.add(Calendar.DATE, -1);
        dateStringCollection.setBeforeYesterdayDate(simpleDateFormat.format(yesterdayCalendar.getTime()));

        //月初 eg：2017-01-01
        GregorianCalendar gcFirstDate = (GregorianCalendar) Calendar.getInstance();
        gcFirstDate.setTime(currentDate);
        gcFirstDate.set(Calendar.DAY_OF_MONTH, 1);
        dateStringCollection.setBeginOfMonthDate(simpleDateFormat.format(gcFirstDate.getTime()));

        //上个月当天：2016-12-05
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTime(currentDate);
        lastCalendar.add(Calendar.MONTH, -1);
        dateStringCollection.setLastMonthDate(simpleDateFormat.format(lastCalendar.getTime()));

        //上个月月初：2016-12-01
        GregorianCalendar gcLastBegin = (GregorianCalendar) Calendar.getInstance();
        gcLastBegin.setTime(lastCalendar.getTime());
        gcLastBegin.set(Calendar.DAY_OF_MONTH, 1);
        dateStringCollection.setLastBeginOfMonthDate(simpleDateFormat.format(gcLastBegin.getTime()));

        List<String> preCurNexString = getPreCurNexTimeStrings(currentDate);
        dateStringCollection.setTomorrowDate(preCurNexString.get(2));

        //上个月当天前一天： 2016-12-04
        lastCalendar.add(Calendar.DATE, -1);
        List<String> preCurNexLastMonthString = getPreCurNexTimeStrings(lastCalendar.getTime());
        dateStringCollection.setLastBeforeYesterdayDate(preCurNexLastMonthString.get(0));
        dateStringCollection.setLastYesterdayDate(preCurNexLastMonthString.get(1));

        return dateStringCollection;
    }


    /**
     * 将计算出的数据，统一整合
     *
     * @param vFactJx3StatDDtoList 所有从上个月一号到昨天的数据记录
     * @param times                一些日期集合，辅助计算
     * @return 结果类Vo
     */
    private DailyVo calculateTargetGrowthTrend(List<VFactJx3StatDDto> vFactJx3StatDDtoList,
                                               DateStringCollection times) {
        DailyVo dailyVo = new DailyVo();
        DailyVo.DataDetail consumeItemValue = new DailyVo.DataDetail();
        DailyVo.DataDetail actvAccountNum = new DailyVo.DataDetail();
        DailyVo.DataDetail newAccountNum = new DailyVo.DataDetail();
        DailyVo.DataDetail cardMoneyValue = new DailyVo.DataDetail();
        DailyVo.DataDetail consumeValue = new DailyVo.DataDetail();
        DailyVo.DataDetail chargeAccountNum = new DailyVo.DataDetail();
        DailyVo.DataDetail newChargeAccntNum = new DailyVo.DataDetail();

        DividedDataByMonthDate dividedDataByMonthDate = dividedStatDDtoList(vFactJx3StatDDtoList, times);

        List<VFactJx3StatDDto> currentMonthDataDtoList = dividedDataByMonthDate.getCurrentMonthData();
        List<VFactJx3StatDDto> previousMonthDataDtoList = dividedDataByMonthDate.getPreviousMonthData();
        DailyDto previousMonthData = plusAllKindsIndex(previousMonthDataDtoList);
        DailyDto currentMonthData = plusAllKindsIndex(currentMonthDataDtoList);
        VFactJx3StatDDto previousDayData = dividedDataByMonthDate.getPreviousData();
        VFactJx3StatDDto beforeYesterdayData = dividedDataByMonthDate.getBeforeYesterday();


        consumeItemValue.setDoD(calculateDivide(previousDayData.getConsumeItemValue(), beforeYesterdayData.getConsumeItemValue()));
        consumeItemValue.setMoM(calculateDivide(currentMonthData.getConsumeItemValue(), previousMonthData.getConsumeItemValue()));
        consumeItemValue.setIndex(indexExactValue(previousDayData.getConsumeItemValue(), new BigDecimal(10000)));

        actvAccountNum.setIndex(previousDayData.getActvAccountNum());
        actvAccountNum.setMoM(calculateDivide(currentMonthData.getActvAccountNum(), previousMonthData.getActvAccountNum()));
        actvAccountNum.setDoD(calculateDivide(previousDayData.getActvAccountNum(), beforeYesterdayData.getActvAccountNum()));

        newAccountNum.setIndex(previousDayData.getNewAccountNum());
        newAccountNum.setMoM(calculateDivide(currentMonthData.getNewAccountNum(), previousMonthData.getNewAccountNum()));
        newAccountNum.setDoD(calculateDivide(previousDayData.getNewAccountNum(), beforeYesterdayData.getNewAccountNum()));

        cardMoneyValue.setMoM(calculateDivide(currentMonthData.getCardMoneyValue(), previousMonthData.getCardMoneyValue()));
        cardMoneyValue.setDoD(calculateDivide(previousDayData.getCardMoneyValue(), beforeYesterdayData.getCardMoneyValue()));
        cardMoneyValue.setIndex(indexExactValue(previousDayData.getCardMoneyValue(), new BigDecimal(10000)));

        consumeValue.setIndex(indexExactValue(previousDayData.getConsumeValue(), new BigDecimal(10000)));
        consumeValue.setDoD(calculateDivide(previousDayData.getConsumeValue(), beforeYesterdayData.getConsumeValue()));
        consumeValue.setMoM(calculateDivide(currentMonthData.getConsumeValue(), previousMonthData.getConsumeValue()));

        chargeAccountNum.setIndex(previousDayData.getChargeAccountNum());
        chargeAccountNum.setDoD(calculateDivide(previousDayData.getChargeAccountNum(), beforeYesterdayData.getChargeAccountNum()));
        chargeAccountNum.setMoM(calculateDivide(currentMonthData.getChargeAccountNum(), previousMonthData.getChargeAccountNum()));

        newChargeAccntNum.setIndex(previousDayData.getNewChargeAccntNum());
        newChargeAccntNum.setDoD(calculateDivide(previousDayData.getNewChargeAccntNum(), beforeYesterdayData.getNewChargeAccntNum()));
        newChargeAccntNum.setMoM(calculateDivide(currentMonthData.getNewChargeAccntNum(), previousMonthData.getNewChargeAccntNum()));

        dailyVo.setActvAccountNum(actvAccountNum);
        dailyVo.setConsumeItemValue(consumeItemValue);
        dailyVo.setNewAccountNum(newAccountNum);
        dailyVo.setCardMoneyValue(cardMoneyValue);
        dailyVo.setConsumeValue(consumeValue);
        dailyVo.setChargeAccountNum(chargeAccountNum);
        dailyVo.setNewChargeAccntNum(newChargeAccntNum);
        return dailyVo;
    }

    private DailyDto plusAllKindsIndex(List<VFactJx3StatDDto> monthDataDtoList) {
        DailyDto result = new DailyDto();
        BigDecimal monthConsumeItemValue = new BigDecimal(0);
        BigDecimal monthActvAccountNum = new BigDecimal(0);
        BigDecimal monthNewAccountNum = new BigDecimal(0);
        BigDecimal monthCardMoneyValue = new BigDecimal(0);
        BigDecimal monthConsumeValue = new BigDecimal(0);
        BigDecimal monthChargeAccountNum = new BigDecimal(0);
        BigDecimal monthNewChargeAccntNum = new BigDecimal(0);

        for (VFactJx3StatDDto currentDto : monthDataDtoList) {
            monthConsumeItemValue = monthConsumeItemValue.add(currentDto.getConsumeItemValue());
            monthActvAccountNum = monthActvAccountNum.add(currentDto.getActvAccountNum());
            monthNewAccountNum = monthNewAccountNum.add(currentDto.getNewAccountNum());
            monthCardMoneyValue = monthCardMoneyValue.add(currentDto.getCardMoneyValue());
            monthConsumeValue = monthConsumeValue.add(currentDto.getConsumeValue());
            monthChargeAccountNum = monthChargeAccountNum.add(currentDto.getChargeAccountNum());
            monthNewChargeAccntNum = monthNewChargeAccntNum.add(currentDto.getNewChargeAccntNum());
        }
        result.setConsumeItemValue(monthConsumeItemValue);
        result.setActvAccountNum(monthActvAccountNum);
        result.setNewAccountNum(monthNewAccountNum);
        result.setCardMoneyValue(monthCardMoneyValue);
        result.setConsumeValue(monthConsumeValue);
        result.setChargeAccountNum(monthChargeAccountNum);
        result.setNewChargeAccntNum(monthNewChargeAccntNum);
        return result;
    }

    private DividedDataByMonthDate dividedStatDDtoList(List<VFactJx3StatDDto> vFactJx3StatDDtoList, DateStringCollection times) {
        DividedDataByMonthDate dividedDataByMonthDate = new DividedDataByMonthDate();

        List<VFactJx3StatDDto> previousMonthData = new ArrayList<>();
        List<VFactJx3StatDDto> currentMonthData = new ArrayList<>();

        String beforeYesterdayDate = times.getBeforeYesterdayDate();
        String previousDateString = times.getYesterdayDate();

        String lastMonthBeginDate = times.getLastBeginOfMonthDate();
        String beginOfMonthDate = times.getBeginOfMonthDate();

        String lastMonthDate = times.getLastMonthDate();
        String currentDate = times.getCurrentDate();

        for (VFactJx3StatDDto dto : vFactJx3StatDDtoList) {
            String dtoString = dto.getStatDate();

            //上个月和这个月的数据存入 dividedDataByMonthDate 的两个List 中
            if (dtoString.compareTo(lastMonthBeginDate) >= 0 && dtoString.compareTo(lastMonthDate) < 0) {
                // 上个月同期的记录
                previousMonthData.add(dto);
            } else if (dtoString.compareTo(beginOfMonthDate) >= 0 && dtoString.compareTo(currentDate) < 0) {
                // 这个月 01 th - 昨天的记录
                currentMonthData.add(dto);
            }

            //前天和昨天的数据存入dividedDataByMonthDate
            if (dtoString.contains(beforeYesterdayDate)) {
                //前天记录
                dividedDataByMonthDate.setBeforeYesterday(dto);
            } else if (dtoString.contains(previousDateString)) {
                //昨天记录
                dividedDataByMonthDate.setPreviousData(dto);
            }
        }
        if (dividedDataByMonthDate.getBeforeYesterday() == null) {
            dividedDataByMonthDate.setBeforeYesterday(new VFactJx3StatDDto());
        }
        if (dividedDataByMonthDate.getPreviousData() == null) {
            dividedDataByMonthDate.setPreviousData(new VFactJx3StatDDto());
        }
        dividedDataByMonthDate.setCurrentMonthData(currentMonthData);
        dividedDataByMonthDate.setPreviousMonthData(previousMonthData);
        return dividedDataByMonthDate;
    }


    private BigDecimal calculateDivide(BigDecimal currentData, BigDecimal previousData) {
        if (currentData == null || previousData == null ||
                currentData.floatValue() == 0 || previousData.floatValue() == 0) {
            return null;
        }
        BigDecimal increaseData = currentData.subtract(previousData);
        return increaseData.divide(previousData, 2, BigDecimal.ROUND_HALF_EVEN);
    }

    private BigDecimal indexExactValue(BigDecimal index, BigDecimal divisor) {
        if (index == null) {
            return null;
        }
        return index.divide(divisor, 2, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * 老板数据接口
     *
     * @param code 项目编码
     * @return
     */
    public BossDataVo getBossInterfaceData(String code) {
        String url = address + "/jx3_report";
        //总数据
        BossDataVo bossDataVo = new BossDataVo();
        Map<String, String> param = new HashMap<String, String>() {
        };
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        KsHttpComponent component = new KsHttpComponent();
        //获取数据并处理
        String response = component.doGetHttpRequest(url, param, loginId, null, "GBK");
        List<JSONObject> list = JSON.parseArray(response, JSONObject.class);

        Map<String, List<StatOnlineAccountDto>> onlineAccountMap = new HashMap<String, List<StatOnlineAccountDto>>();
        Map<String, BossDailyVo> dailyMap = new HashMap<String, BossDailyVo>();//处理日报

        List<StatOnlineAccountDto> currentData = new ArrayList<StatOnlineAccountDto>();//实时在线人数当前
        List<StatOnlineAccountDto> previousData = new ArrayList<StatOnlineAccountDto>();//实时在线人数前一天
        List<BossOnlineSumVo> onlineChargeSum = new ArrayList<BossOnlineSumVo>();//实时充值
        List<BossOnlineSumVo> productIncome = new ArrayList<BossOnlineSumVo>();//新品总收入
        List<BossOnlineSumVo> productSales = new ArrayList<BossOnlineSumVo>();//新品总售卖件数
        List<BossOnlineSumVo> incrementConsume = new ArrayList<BossOnlineSumVo>();//增值消耗累计
        List<BossOnlineGameVo> dailyGameItems = new ArrayList<>();
        ;//日报数据
        List<BossDailyVo> dailyItems = new ArrayList<BossDailyVo>();

        for (JSONObject jsonObject : list) {
            String game_name = jsonObject.getString("game_name");
            if (game_name.equals("jx3")) {
                setOnlineAccount(onlineAccountMap, jsonObject);//在线人数
                setOnlineSum(onlineChargeSum, productIncome, productSales, incrementConsume, jsonObject);
                setDailyItems(dailyMap, jsonObject);
                setDailyGameItems(dailyGameItems, jsonObject);
            }
        }
        groupByOnlineAccount(onlineAccountMap, currentData, previousData);//分组设置在线人数
        setDailyItems(dailyMap, dailyItems);//设置日报数据
        bossDataVo.setCurrentData(currentData);
        bossDataVo.setPreviousData(previousData);
        bossDataVo.setOnlineChargeSum(onlineChargeSum);
        bossDataVo.setProductIncome(productIncome);
        bossDataVo.setProductSales(productSales);
        bossDataVo.setDailyGameItems(dailyGameItems);//游戏道具售卖
        bossDataVo.setIncrementConsume(incrementConsume);
        sortBossDailyVo(dailyItems);//排序
        bossDataVo.setDailyItems(dailyItems);
        return bossDataVo;
    }

    /**
     * 设置游戏道具售卖详情
     */
    private void setDailyGameItems(List<BossOnlineGameVo> dailyGameItems, JSONObject jsonObject) {
        if (dailyGameItems != null && jsonObject != null) {
            String report_name = jsonObject.getString("report_name");
            String st_date = MyDateUtils.getSt_date(jsonObject.getString("st_date"));
            String mer_name = jsonObject.getString("mer_name");
            mer_name = st_date + " " + mer_name;
            BigDecimal mer_value = jsonObject.getBigDecimal("mer_value");
            if (!bossIndex.containsValue(report_name) && !dailyItems.containsValue(report_name)) {
                dailyGameItems.add(new BossOnlineGameVo(mer_name, mer_value, report_name));
            }
        }
    }

    /**
     * 设置在线实时值
     * 实时充值累计\新品总收入\新品总售卖件数\增值消耗累计
     *
     * @param onlineChargeSum  实时充值
     * @param productIncome    新品总收入
     * @param productSales     新品总售卖件数
     * @param incrementConsume 增值消耗累计
     * @param jsonObject
     */
    private void setOnlineSum(List<BossOnlineSumVo> onlineChargeSum, List<BossOnlineSumVo> productIncome, List<BossOnlineSumVo> productSales, List<BossOnlineSumVo> incrementConsume, JSONObject jsonObject) {
        String report_name = jsonObject.getString("report_name");
        String st_date = MyDateUtils.getSt_date(jsonObject.getString("st_date"));
        String mer_name = st_date + " " + jsonObject.getString("mer_name");
        BigDecimal mer_value = jsonObject.getBigDecimal("mer_value");
        if (mer_value != null) {
            if (bossIndex.get("002").equalsIgnoreCase(report_name)) {
                mer_value = mer_value.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN);
                onlineChargeSum.add(new BossOnlineSumVo(mer_name, mer_value));
            }
            if (bossIndex.get("003").equalsIgnoreCase(report_name)) {
                mer_value = mer_value.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN);
                productIncome.add(new BossOnlineSumVo(mer_name, mer_value));
            }
            if (bossIndex.get("004").equalsIgnoreCase(report_name)) {
                mer_value = mer_value.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_EVEN);
                productSales.add(new BossOnlineSumVo(mer_name, mer_value));
            }
            if (bossIndex.get("005").equalsIgnoreCase(report_name)) {
                mer_value = mer_value.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_EVEN);
                incrementConsume.add(new BossOnlineSumVo(mer_name, mer_value));
            }
        }
    }

    /**
     * 设置在线人数
     *
     * @param onlineAccountMap
     * @param jsonObject
     */
    private void setOnlineAccount(Map<String, List<StatOnlineAccountDto>> onlineAccountMap, JSONObject jsonObject) {
        if (onlineAccountMap != null && jsonObject != null) {
            String reportName = jsonObject.getString("report_name");
            if (bossIndex.get("001").equalsIgnoreCase(reportName)) {
                String st_date = jsonObject.getString("st_date");
                BigDecimal merValue = jsonObject.getBigDecimal("mer_value");
                StatOnlineAccountDto dto = null;
                List<StatOnlineAccountDto> items = null;
                if (merValue != null) {
                    merValue = merValue.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_EVEN);
                }
                //对mer_name进行处理,没30min获取一次数据
                String mer_name = jsonObject.getString("mer_name");
                if (mer_name.substring(3, 5).equals("00") || mer_name.substring(3, 5).equals("30")) {
                    //日期加时间
                    mer_name = MyDateUtils.getSt_date(st_date) + " " + mer_name;
                    if (onlineAccountMap.containsKey(st_date)) {
                        onlineAccountMap.get(st_date).add(new StatOnlineAccountDto(mer_name, merValue));
                    } else {
                        items = new ArrayList<StatOnlineAccountDto>();
                        items.add(new StatOnlineAccountDto(mer_name, merValue));
                        onlineAccountMap.put(st_date, items);
                    }
                }
            }
        }
    }

    /**
     * 分组设置在线人数
     *
     * @param onlineAccountMap
     * @param currentData
     * @param previousData
     */
    private void groupByOnlineAccount(Map<String, List<StatOnlineAccountDto>> onlineAccountMap, List<StatOnlineAccountDto> currentData, List<StatOnlineAccountDto> previousData) {
        if (onlineAccountMap != null && currentData != null && previousData != null) {
            Set<String> keys = onlineAccountMap.keySet();
            List<Integer> items = new ArrayList<Integer>();
            for (String key : keys) {
                items.add(Integer.parseInt(key));
            }
            Collections.sort(items);
            if (items.size() > 1) {
                currentData.addAll(onlineAccountMap.get(String.valueOf(items.get(1))));
                previousData.addAll(onlineAccountMap.get(String.valueOf(items.get(0))));
            }
        }
    }

    /**
     * 设置日报
     *
     * @param dailyMap
     * @param jsonObject
     */
    private void setDailyItems(Map<String, BossDailyVo> dailyMap, JSONObject jsonObject) {
        if (dailyMap != null && jsonObject != null) {
            String reportName = jsonObject.getString("report_name");
            if (!bossIndex.containsValue(reportName) && dailyItems.containsValue(reportName)) {
                StatOnlineAccountDto dto = null;
                List<StatOnlineAccountDto> items = null;
                if (dailyMap.containsKey(reportName)) {
                    BossDailyVo bossDailyVo = dailyMap.get(reportName);
                    setBossDailyVo(bossDailyVo, jsonObject);
                } else {
                    BossDailyVo bossDailyVo = new BossDailyVo();
                    bossDailyVo.setName(reportName);//名称
                    setBossDailyVo(bossDailyVo, jsonObject);
                    dailyMap.put(reportName, bossDailyVo);
                }
            }
        }
    }

    /**
     * 设置VO值
     *
     * @param bossDailyVo
     * @param jsonObject
     */
    private void setBossDailyVo(BossDailyVo bossDailyVo, JSONObject jsonObject) {
        if (bossDailyVo != null && jsonObject != null) {
            String reportName = jsonObject.getString("report_name");
            String merName = jsonObject.getString("mer_name");
            BigDecimal merValue = jsonObject.getBigDecimal("mer_value");
            if (dailyIndex.get("d").equalsIgnoreCase(merName))
                bossDailyVo.setDayMom(new DecimalFormat(",###.####").format(jsonObject.getBigDecimal("mer_value")));//日环比
            else if (dailyIndex.get("w").equalsIgnoreCase(merName)) {
                bossDailyVo.setWeekAn(new DecimalFormat(",###.####").format(jsonObject.getBigDecimal("mer_value")));//周同比
            } else if (dailyIndex.get("m").equalsIgnoreCase(merName)) {
                bossDailyVo.setMonthAn(new DecimalFormat(",###.####").format(jsonObject.getBigDecimal("mer_value")));//月同比
            } else if (dailyIndex.get("y").equalsIgnoreCase(merName)) {
                bossDailyVo.setYearMom(new DecimalFormat(",###.####").format(jsonObject.getBigDecimal("mer_value")));//年环比
            } else if (dailyIndex.get("index").equalsIgnoreCase(merName)) {
                bossDailyVo.setIndexValue(new DecimalFormat(",###.##").format(jsonObject.getBigDecimal("mer_value")));//指标值
            }
        }
    }

    /**
     * 设置日报数据
     *
     * @param dailyMap
     * @param dailyItems
     */
    private void setDailyItems(Map<String, BossDailyVo> dailyMap, List<BossDailyVo> dailyItems) {
        if (dailyMap != null && dailyItems != null) {
            Set<String> keys = dailyMap.keySet();
            for (String key : keys) {
                dailyItems.add(dailyMap.get(key));
            }
        }
    }

    private static Map<String, String> bossIndex = new HashMap<String, String>() {
        {
            put("001", BossDataConstant.BossIndex_1);
            put("002", BossDataConstant.BossIndex_2);
            put("003", BossDataConstant.BossIndex_3);
            put("004", BossDataConstant.BossIndex_4);
            put("005", BossDataConstant.BossIndex_5);
        }
    };
    private static Map<String, String> dailyItems = new HashMap<String, String>() {
        {
            put("001", BossDataConstant.BossDaily_1);
            put("002", BossDataConstant.BossDaily_2);
            put("003", BossDataConstant.BossDaily_3);
            put("004", BossDataConstant.BossDaily_33);
            put("005", BossDataConstant.BossDaily_4);
            put("006", BossDataConstant.BossDaily_5);
            put("007", BossDataConstant.BossDaily_6);
            put("008", BossDataConstant.BossDaily_7);
            put("009", BossDataConstant.BossDaily_8);
            put("0010", BossDataConstant.BossDaily_9);
            put("0011", BossDataConstant.BossDaily_10);
            put("0012", BossDataConstant.BossDaily_11);
            put("0013", BossDataConstant.BossDaily_12);
            put("0014", BossDataConstant.BossDaily_13);
            put("0015", BossDataConstant.BossDaily_14);
            put("0016", BossDataConstant.BossDaily_15);
            put("0017", BossDataConstant.BossDaily_16);
            put("0018", BossDataConstant.BossDaily_17);
            put("0019", BossDataConstant.BossDaily_18);
            put("0020", BossDataConstant.BossDaily_19);
            put("0021", BossDataConstant.BossDaily_20);
            put("0022", BossDataConstant.BossDaily_22);
            put("0023", BossDataConstant.BossDaily_21);
            put("0024", BossDataConstant.BossDaily_23);
            put("0025", BossDataConstant.BossDaily_24);
            put("0026", BossDataConstant.BossDaily_25);
            put("0027", BossDataConstant.BossDaily_26);
            put("0028", BossDataConstant.BossDaily_27);
            put("0029", BossDataConstant.BossDaily_28);
            put("0030", BossDataConstant.BossDaily_29);
            put("0031", BossDataConstant.BossDaily_30);
            put("0032", BossDataConstant.BossDaily_32);
        }
    };
    private static Map<String, String> dailyIndex = new HashMap<String, String>() {
        {
            put("d", BossDataConstant.BossDailyIndex_D);
            put("w", BossDataConstant.BossDailyIndex_W);
            put("m", BossDataConstant.BossDailyIndex_M);
            put("y", BossDataConstant.BossDailyIndex_Y);
            put("index", BossDataConstant.BossDailyIndex);
        }
    };
    private static Map<String, String> unitIndex = new HashMap<String, String>() {
        {

            put("unit", "万金");

            put("index", "指标值");
        }
    };

    /**
     * 排序
     */
    private void sortBossDailyVo(List<BossDailyVo> dailyItems) {
        //co.equals(acu)
        for (BossDailyVo co : dailyItems) {
            if (co.getName().equals(BossDataConstant.BossDaily_1)) {
                co.setSort(1);
            } else if (co.getName().equals(BossDataConstant.BossDaily_2)) {
                co.setSort(2);
            } else if (co.getName().equals(BossDataConstant.BossDaily_3)) {
                co.setSort(3);
            } else if (co.getName().equals(BossDataConstant.BossDaily_33)) {
                co.setSort(4);
            } else if (co.getName().equals(BossDataConstant.BossDaily_4)) {
                co.setSort(5);
            } else if (co.getName().equals(BossDataConstant.BossDaily_5)) {
                co.setSort(6);
            } else if (co.getName().equals(BossDataConstant.BossDaily_6)) {
                co.setSort(7);
            } else if (co.getName().equals(BossDataConstant.BossDaily_7)) {
                co.setSort(8);
            } else if (co.getName().equals(BossDataConstant.BossDaily_8)) {
                co.setSort(9);
            } else if (co.getName().equals(BossDataConstant.BossDaily_9)) {
                co.setSort(10);
            } else if (co.getName().equals(BossDataConstant.BossDaily_10)) {
                co.setSort(11);
            } else if (co.getName().equals(BossDataConstant.BossDaily_11)) {
                co.setSort(12);
            } else if (co.getName().equals(BossDataConstant.BossDaily_12)) {
                co.setSort(13);
            } else if (co.getName().equals(BossDataConstant.BossDaily_13)) {
                co.setSort(14);
            } else if (co.getName().equals(BossDataConstant.BossDaily_14)) {
                co.setSort(15);
            } else if (co.getName().equals(BossDataConstant.BossDaily_15)) {
                co.setSort(16);
            } else if (co.getName().equals(BossDataConstant.BossDaily_16)) {
                co.setSort(17);
            } else if (co.getName().equals(BossDataConstant.BossDaily_17)) {
                co.setSort(18);
            } else if (co.getName().equals(BossDataConstant.BossDaily_18)) {
                co.setSort(19);
            } else if (co.getName().equals(BossDataConstant.BossDaily_19)) {
                co.setSort(20);
            } else if (co.getName().equals(BossDataConstant.BossDaily_20)) {
                co.setSort(21);
            } else if (co.getName().equals(BossDataConstant.BossDaily_21)) {
                co.setSort(22);
            } else if (co.getName().equals(BossDataConstant.BossDaily_22)) {
                co.setSort(23);
            } else if (co.getName().equals(BossDataConstant.BossDaily_23)) {
                co.setSort(24);
            } else if (co.getName().equals(BossDataConstant.BossDaily_24)) {
                co.setSort(25);
            } else if (co.getName().equals(BossDataConstant.BossDaily_25)) {
                co.setSort(29);
            } else if (co.getName().equals(BossDataConstant.BossDaily_26)) {
                co.setSort(30);
            } else if (co.getName().equals(BossDataConstant.BossDaily_27)) {
                co.setSort(31);
            } else if (co.getName().equals(BossDataConstant.BossDaily_28)) {
                co.setSort(32);
            } else if (co.getName().equals(BossDataConstant.BossDaily_29)) {
                co.setSort(33);
            } else if (co.getName().equals(BossDataConstant.BossDaily_30)) {
                co.setSort(34);
            } else if (co.getName().equals(BossDataConstant.BossDaily_32)) {
                co.setSort(35);
            }
        }
        Collections.sort(dailyItems, new Comparator<BossDailyVo>() {
            @Override
            public int compare(BossDailyVo o1, BossDailyVo o2) {
                if (o1.getSort() == null) {
                    o1.setSort(0);
                }
                if (o2.getSort() == null) {
                    o2.setSort(0);
                }
                return o1.getSort().compareTo(o2.getSort());
            }
        });
    }
    @Override
    public Map<String,String> getQualityUrl(Long projectId) {
        String requestUrl = qualityRequestAddress+"/api/login_by_secret";
        String returnUrl = null;
        Map<String,String> map = new HashMap<String,String>();
        map.put("ret", String.valueOf(ErrorCode.USER_INVALID_OPERATE_ERROR));
        map.put("url","系统异常");
        PmQualityProjectRelation pmQualityProjectRelation = pmQualityProjectRelationMapper.selectByItProjectId(projectId);
        logger.warn("获取项目id："+projectId);
        if(pmQualityProjectRelation != null && pmQualityProjectRelation.getQualityProjectId()!=null) {
            List< BasicNameValuePair > postBody= new ArrayList<BasicNameValuePair>();
            Map<String, String> param = new HashMap<String, String>();
            String loginId = MyTokenUtils.getCurrentUser().getLoginId();
            Long qualityProjectId = pmQualityProjectRelation.getQualityProjectId();
            KsHttpComponent component = new KsHttpComponent();
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair("account", loginId);
            postBody.add(basicNameValuePair);
            basicNameValuePair =new BasicNameValuePair("secret", "213393edbf20fe83b6abb4e520c4baee4da856a0");
            postBody.add(basicNameValuePair);
            String str = component.doPostHttpRequest(requestUrl,postBody,loginId,null);
            if(StringUtils.isNotEmpty(str)) {
                Map<String, String> data = JSON.parseObject(str, Map.class);
                if(data != null){
                    String ret = String.valueOf(data.get("ret"));
                    if("1".equalsIgnoreCase(ret)) {
                        String token = data.get("token");
                        try {
                            token = URLEncoder.encode(token, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.error("urlEncode error...");
                            e.printStackTrace();
                        }
                        returnUrl = qualityResponseAddress + "/?token=" + token + "&seasunapp=true&redirect_url=" + qualityResponseAddress + "/project/" + qualityProjectId + "/dashboard";
                        map.put("ret","0");
                        map.put("url",returnUrl);
                    }
                }
                logger.warn("返回URL："+returnUrl);
            }
        }
        logger.warn("执行完返回");
        return map;
    }
}