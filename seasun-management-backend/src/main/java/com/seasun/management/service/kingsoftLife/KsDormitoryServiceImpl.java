package com.seasun.management.service.kingsoftLife;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.common.KsHttpComponent;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.*;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.KsException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.DormPayment;
import com.seasun.management.model.UserDormitoryParam;
import com.seasun.management.model.UserDormitoryReservation;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class KsDormitoryServiceImpl implements KsDormitoryService {
    private static final Logger logger = LoggerFactory.getLogger(KsDormitoryServiceImpl.class);
    public static final Map<String, String> cellNameMap = new LinkedHashMap<>(); //为保证有序
    private static final Map<String, String> roomMap = new HashMap<>();
    private static final Map<String, String> doomMap = new HashMap<>();
    private static final Map<String, String> roomStateMap = new HashMap<>();
    private static final Map<String, String> bedroomMap = new HashMap<>();
    private static final Map<String, String> berthMap = new HashMap<>();
    private static final String fileNamepRrefix = "/西山居宿舍预定明细表";
    private static final String dormitoryReverseFileNamePrefix = "/宿舍费用分摊报表_";
    private static final String tailData[][] = {
            {"计算方法", "员工宿舍占比 = （项目员工宿舍总人天数）/ ( 员工宿舍总床位数 * 当月天数 )"},
            {"", "人才主卧占比 = （项目人才主卧总人天数）/ ( 人才主卧总床位数 * 当月天数 )"},
            {"", "人才次卧占比 = （项目人才次卧总人天数）/ ( 人才次卧总床位数 * 当月天数 )"},
            {"", "人才第三间占比 = （项目第三间总人天数）/ ( 人才第三间总床位数 * 当月天数 )"},
    };

    private static final String DORMITORY_A_ROOM1 = "A_ROOM1";//主卧
    private static final String DORMITORY_B_ROOM2 = "B_ROOM2";//次卧
    private static final String DORMITORY_C_ROOM3 = "C_ROOM3";//第三房

    private static final String A_UPPER = "A_UPPER";
    private static final String B_LOWER = "B_LOWER";

    private static final String DORMITORY_All_ROOM = "All_ROOM";
    private static final String DORMITORY_A_ROOM = "A_ROOM";
    private static final String DORMITORY_B_ROOM = "B_ROOM";
    private static final String DORMITORY_C_ROOM = "C_ROOM";

    static {
        cellNameMap.put("roomNo", "房间号");
        cellNameMap.put("bedroom", "卧室");
        cellNameMap.put("berth", "床位");
        cellNameMap.put("name", "姓名");
        cellNameMap.put("idCode", "身份证");
        cellNameMap.put("mobile", "手机号");
        cellNameMap.put("liveState", "入住状态");
        cellNameMap.put("expectCheckInDate", "预计入住时间");
        cellNameMap.put("expectCheckOutDate", "预计退房时间");
        cellNameMap.put("checkInDate", "实际入住时间");
        cellNameMap.put("checkOutDate", "实际退房时间");
        cellNameMap.put("bookBy", "经办人");
        //"companyCode", "projectName", "allDormitoryRoom", "allDormitoryPercent", "allApartmentRoom", "aRoom", "aRoomPercent", "bRoom", "bRoomPercent", "cRoom", "cRoomPercent"
        cellNameMap.put("companyCode", "公司");
        cellNameMap.put("projectName", "项目");
        cellNameMap.put("allDormitoryRoom", "员工宿舍入住人天数");
        cellNameMap.put("allDormitoryPercent", "占比");
        cellNameMap.put("allApartmentRoom", "公寓总人天数");
        cellNameMap.put("aRoom", "主卧人天数");
        cellNameMap.put("aRoomPercent", "主卧占比");
        cellNameMap.put("bRoom", "次卧人天数");
        cellNameMap.put("bRoomPercent", "次卧占比");
        cellNameMap.put("cRoom", "第三间房人天数");
        cellNameMap.put("cRoomPercent", "第三间房占比");
        cellNameMap.put("fee", "费用");
        cellNameMap.put("afee", "主卧费用");
        cellNameMap.put("bfee", "次卧费用");
        cellNameMap.put("cfee", "第三间房费用");


        roomMap.put("STAFF_DORM", "员工宿舍");
        roomMap.put("APARTMENT", "人才公寓");

        roomStateMap.put("EMPTY", "空房");
        roomStateMap.put("BOOK", "有预定");
        roomStateMap.put("CHECKIN", "入住");
        roomStateMap.put("CLEAN", "清洁房");
        roomStateMap.put("REPAIR", "维修房");

        bedroomMap.put("A_ROOM1", "主卧");
        bedroomMap.put("B_ROOM2", "次卧");
        bedroomMap.put("C_ROOM3", "第三间房");

        doomMap.put("A_ROOM1", "A房");
        doomMap.put("B_ROOM2", "B房");
        doomMap.put("C_ROOM3", "C房");

        bedroomMap.put("L_LIVING_ROOM", "客厅");

        berthMap.put("A_UPPER", "上铺");
        berthMap.put("B_LOWER", "下铺");
        berthMap.put("NONE", "--");
    }

    @Value("${ks.life.address}")
    private String address;
    private final Integer SUCCESS_CODE = 1200;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserDormitoryReservationMapper userDormitoryReservationMapper;

    @Autowired
    private DormPaymentMapper dormPaymentMapper;

    @Value("${file.sys.prefix}")
    String filePathPrefix;
    @Value("${export.excel.path}")
    String exportFolder;
    @Value("${backup.excel.url}")
    String filePathBackup;
    @Autowired
    private UserDormitoryParamMapper userDormitoryParamMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private SubcompanyMapper subcompanyMapper;


    @Override
    public ResponseEntity<?> getDormitory(KsDormitoryVo ksDormitoryVo) {
        String url = "/kshome/house_room/list";
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        KsHttpComponent component = new KsHttpComponent();
        BaseQueryResponseVo result = new BaseQueryResponseVo();
        Map<String, String> param = new HashMap<String, String>() {{
            put("houseBuildingCode", ksDormitoryVo.getHouseBuildingCode());
            put("floor", ksDormitoryVo.getFloor());
            put("roomNo", ksDormitoryVo.getRoomNo());
            put("houseRoomProperty", ksDormitoryVo.getHouseRoomProperty());
            put("houseRoomState", ksDormitoryVo.getHouseRoomState());
            put("date", ksDormitoryVo.getDate());
            put("pageNo", String.valueOf(ksDormitoryVo.getCurrentPage()));
            put("pageSize", String.valueOf(ksDormitoryVo.getPageSize()));
            if (StringUtils.isNotEmpty(ksDormitoryVo.getLeftAvailableCount())) {
                put("leftAvailableCount", String.valueOf(ksDormitoryVo.getLeftAvailableCount()));
            }
        }};
        String response = component.doGetHttpRequest(address + url, param, loginId, null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) == 0) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            KsDormitoryVo ksDormitory = JSON.parseObject(decryptedData, KsDormitoryVo.class);
            result.setItems(ksDormitory.getModelSet());
            result.setTotalRecord(ksDormitory.getTotal());
            return ResponseEntity.ok(new CommonAppResponse(0, result));
        } else {
            if (ksResult != null) {
                return ResponseEntity.ok(new CommonResponse(ksResult.getCode(), ksResult.getMsg()));
            } else {
                return ResponseEntity.ok(new CommonResponse(999, "系统未知异常"));
            }
        }
    }

    @Override
    public ResponseEntity<?> addDormitoryReserve(KsDormitoryReserveVo ksDormitoryReserveVo) throws Exception {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String url = "/kshome/house_room_live/book";
        KsHttpComponent component = new KsHttpComponent();
        ksDormitoryReserveVo.setOrg("西山居");
        ksDormitoryReserveVo.setExpectCheckInDate(ksDormitoryReserveVo.getExpectCheckInDate() + " 00:00:00");
        ksDormitoryReserveVo.setExpectCheckOutDate(ksDormitoryReserveVo.getExpectCheckOutDate() + " 15:00:00");
        String json = JSON.toJSONString(ksDormitoryReserveVo);
        logger.info("addDormitoryReserve send message：", json);
        String response = component.doPostHttpRequest(address + url, json, loginId);
        logger.info("addDormitoryReserve get message：", response);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) == 0) {
            UserDormitoryReservation record = new UserDormitoryReservation();
            record.setName(ksDormitoryReserveVo.getName());
            record.setCertificateType(ksDormitoryReserveVo.getCertificateType());
            record.setIdCode(MyEncryptorUtils.encryptByAES(ksDormitoryReserveVo.getIdCode()));
            record.setUserId(ksDormitoryReserveVo.getUserId());
            record.setBedroomType(ksDormitoryReserveVo.getBedroom());
            record.setBerthType(ksDormitoryReserveVo.getBerth());
            record.setExpectCheckinDate(MyDateUtils.strToDate(ksDormitoryReserveVo.getExpectCheckInDate(), MyDateUtils.DATE_FORMAT_YYYY_MM_DD_HHMMSS));
            record.setExpectCheckoutDate(MyDateUtils.strToDate(ksDormitoryReserveVo.getExpectCheckOutDate(), MyDateUtils.DATE_FORMAT_YYYY_MM_DD_HHMMSS));
            record.setStatus("BOOK");
            record.setCreateDate(new Date());
            record.setRoomNo(ksDormitoryReserveVo.getRoomNo());
            record.setType(ksDormitoryReserveVo.getType());
            record.setProjectId(ksDormitoryReserveVo.getProjectId());
            record.setCompanyId(ksDormitoryReserveVo.getCompanyId());
            record.setMobile(MyEncryptorUtils.encryptByAES(ksDormitoryReserveVo.getMobile()));
            record.setGender(ksDormitoryReserveVo.getGender());
            record.setLoginId(loginId);
            if (ksResult.getData() != null) {
                String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
                logger.info("预订成功返回报文 get message：", decryptedData);
                KsDormitoryReserveVo kvo = JSON.parseObject(decryptedData, KsDormitoryReserveVo.class);
                if (kvo != null) {
                    logger.info("预订成功返回订单号 get LiveNo：", kvo.getLiveNo());
                    record.setLiveNo(kvo.getLiveNo());
                }
            }
            userDormitoryReservationMapper.insert(record);
            return ResponseEntity.ok(new CommonResponse(0, "success"));
        } else {
            if (ksResult != null) {
                return ResponseEntity.ok(new CommonResponse(ksResult.getCode(), ksResult.getMsg()));
            } else {
                return ResponseEntity.ok(new CommonResponse(999, "系统未知异常"));
            }
        }
    }

    @Override
    public ResponseEntity<?> getDormitoryReserveInfo(KsDormitoryReserveVo ksDormitoryReserveVo) {
        String url = "/kshome/house_room_live/list";
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        KsHttpComponent component = new KsHttpComponent();
        BaseQueryResponseVo result = new BaseQueryResponseVo();
        Integer totalRecord = 0;
        Map<String, String> param = new HashMap<String, String>() {{
            put("roomNo", ksDormitoryReserveVo.getRoomNo());
            put("name", ksDormitoryReserveVo.getName());
            put("idCode", ksDormitoryReserveVo.getIdCode());
            put("mobile", ksDormitoryReserveVo.getMobile());
            put("liveState", ksDormitoryReserveVo.getLiveState());
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getCheckInDateFrom())) {
                put("checkInDateFrom", ksDormitoryReserveVo.getCheckInDateFrom() + "%2000:00:00");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getCheckInDateTo())) {
                put("checkInDateTo", ksDormitoryReserveVo.getCheckInDateTo() + "%2000:00:00");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getCheckOutDateFrom())) {
                put("checkOutDateFrom", ksDormitoryReserveVo.getCheckOutDateFrom() + "%2023:59:59");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getCheckOutDateTo())) {
                put("checkOutDateTo", ksDormitoryReserveVo.getCheckOutDateTo() + "%2023:59:59");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getExpectCheckInDateFrom())) {
                put("expectCheckInDateFrom", ksDormitoryReserveVo.getExpectCheckInDateFrom() + "%2000:00:00");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getExpectCheckInDateTo())) {
                put("expectCheckInDateTo", ksDormitoryReserveVo.getExpectCheckInDateTo() + "%2000:00:00");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getExpectCheckOutDateFrom())) {
                put("expectCheckOutDateFrom", ksDormitoryReserveVo.getExpectCheckOutDateFrom() + "%2023:59:59");
            }
            if (StringUtils.isNotEmpty(ksDormitoryReserveVo.getExpectCheckOutDateTo())) {
                put("expectCheckOutDateTo", ksDormitoryReserveVo.getExpectCheckOutDateTo() + "%2023:59:59");
            }
            put("pageNo", String.valueOf(ksDormitoryReserveVo.getCurrentPage()));
            put("pageSize", String.valueOf(ksDormitoryReserveVo.getPageSize()));
            put("bookBy", ksDormitoryReserveVo.getBookBy());
            if (ksDormitoryReserveVo.getQueryRealTimeLiveByDate() != null) {
                put("queryRealTimeLiveByDate", ksDormitoryReserveVo.getQueryRealTimeLiveByDate().toString());
            }
            if (ksDormitoryReserveVo.getExpectCheckInDate() != null) {
                put("expectCheckInDate", ksDormitoryReserveVo.getExpectCheckInDate()+ "%2000:00:00");
            }
        }};
        String response = component.doGetHttpRequest(address + url, param, loginId, null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) == 0) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            KsDormitoryReserveVo KsDormitoryReserve = JSON.parseObject(decryptedData, KsDormitoryReserveVo.class);
            result.setItems(KsDormitoryReserve.getModelSet());
            result.setTotalRecord(KsDormitoryReserve.getTotal());
            return ResponseEntity.ok(new CommonAppResponse(0, result));
        } else {
            if (ksResult != null) {
                return ResponseEntity.ok(new CommonResponse(ksResult.getCode(), ksResult.getMsg()));
            } else {
                return ResponseEntity.ok(new CommonResponse(999, "系统未知异常"));
            }
        }
    }

    @Override
    public ResponseEntity<?> deleteDormitoryReserve(Long id, String liveNo) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String url = "/kshome/house_room_live/cancelbook?id=" + id;
        KsHttpComponent component = new KsHttpComponent();
        String json = JSON.toJSONString(id);
        String response = component.doPostHttpRequest(address + url, null, loginId);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) == 0) {
            userDormitoryReservationMapper.deleteByLiveNo(liveNo);
            return ResponseEntity.ok(new CommonResponse(0, "success"));
        } else {
            if (ksResult != null) {
                return ResponseEntity.ok(new CommonResponse(ksResult.getCode(), ksResult.getMsg()));
            } else {
                return ResponseEntity.ok(new CommonResponse(999, "系统未知异常"));
            }
        }
    }

    @Override
    public ResponseEntity<?> exportDormitoryReserveInfo(KsDormitoryReserveVo ksDormitoryReserveVo, LinkedHashSet<String> fields) throws IOException {
        if (fields == null || fields.isEmpty()) throw new ParamException("必须选中列");
        ResponseEntity<?> responseEntity = (ResponseEntity<?>) this.getDormitoryReserveInfo(ksDormitoryReserveVo);
        logger.debug("responseEntity -> {}", responseEntity);
        if (responseEntity.getBody().getClass().getName().equals("com.seasun.management.controller.response.CommonResponse"))
            return responseEntity;
        CommonAppResponse appResponse = (CommonAppResponse) responseEntity.getBody();
        BaseQueryResponseVo data = (BaseQueryResponseVo) appResponse.getData();
        String path = exportExcel(fields, (List<KsDormitoryReserveVo>) data.getItems());
        return ResponseEntity.ok(new CommonAppResponse(0, path));
    }

    @Override
    public UserMiniVo getDormitoryUserInfo(String loginId) throws Exception {
        UserMiniVo vo = userMapper.selectUserMiniVoByLoginId(loginId);
        if (vo != null) {
            if (StringUtils.isNotEmpty(vo.getMobile())) {
                vo.setMobile(MyEncryptorUtils.decryptByAES(vo.getMobile()));
            }
            if (StringUtils.isNotEmpty(vo.getIdCode())) {
                vo.setIdCode(MyEncryptorUtils.decryptByAES(vo.getIdCode()));
            }
        }
        return vo;
    }

    @Override
    public BaseQueryResponseVo getDormitoryReserveFinanceInfo(Integer year, Integer month, Integer currentPage, Integer pageSize) throws Exception {
        BaseQueryResponseVo result = new BaseQueryResponseVo();

        //        if (year == null || month == null) {
//            Calendar nowDate = Calendar.getInstance();
//            year = nowDate.get(Calendar.YEAR);
//            month = nowDate.get(Calendar.MONTH) + 1;
//        }

        // 发送第三方请求
        KsHttpComponent ksHttpComponent = new KsHttpComponent();
        Map<String, String> param = new HashMap<String, String>() {{
            put("liveDateFrom", year + "-" + (month < 10 ? "0" + month : month) + "-01");
            YearMonthDto yearMonthDto = MyDateUtils.yearMonthCalculation(year, month, 1);
            Integer nextMonth = yearMonthDto.getMonth();
            put("liveDateTo", year + "-" + (nextMonth < 10 ? "0" + nextMonth : nextMonth) + "-01");
            put("pageNo", String.valueOf(currentPage));
            put("pageSize", String.valueOf(pageSize));
        }};
        String response = ksHttpComponent.doGetHttpRequest(address + "/kshome/house_room_live/list", param, MyTokenUtils.getCurrentUser().getLoginId(), null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);

        // 验证
        if (ksResult == null || !SUCCESS_CODE.equals(ksResult.getCode())) {
            throw new ParamException(ErrorMessage.PERSISTENT_LAYER_MESSAGE);
        }

        String decryptedData = (String) ksHttpComponent.ksDecryptData(ksResult.getData().toString());
        KsDormitoryReserveVo KsDormitoryReserve = JSON.parseObject(decryptedData, KsDormitoryReserveVo.class);
        if (KsDormitoryReserve.getModelSet() == null || KsDormitoryReserve.getModelSet().isEmpty()) {
            result.setItems(new ArrayList<>(0));
            result.setTotalRecord(0);
        } else {
            // 当月缴费信息
            Map<String, DormPayment> dormPaymentByUserIdCodeMap = dormPaymentMapper.selectByYearAndMonth(year, month).stream().collect(Collectors.toMap(x -> x.getIdCode(), x -> x, (x, y) -> x));
            // 根据身份证找出用户
            Map<String, UserDormitoryReservationDto> userMiniVoByIdCodeMap = userDormitoryReservationMapper.selectDtoByIdCodes(KsDormitoryReserve.getModelSet().stream().map(x -> {
                String idCode = "";
                try {
                    idCode = MyEncryptorUtils.encryptByAES(x.getIdCode());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                return idCode;
            }).collect(Collectors.toList())).stream().collect(Collectors.toMap(x -> x.getIdCode(), x -> x, (x, y) -> y));

            // 回填页面所需信息
            for (KsDormitoryReserveVo ksDormitoryReserveVo : KsDormitoryReserve.getModelSet()) {
                if (ksDormitoryReserveVo.getIdCode() != null) {
                    String idCodeKey = MyEncryptorUtils.encryptByAES(ksDormitoryReserveVo.getIdCode());
                    UserDormitoryReservationDto userMiniVo = userMiniVoByIdCodeMap.get(idCodeKey);
                    if (userMiniVo == null) {
                        ksDormitoryReserveVo.setType(2);
                    } else {
                        copyUserMiniVoToKsDormitoryReserveVo(userMiniVo, ksDormitoryReserveVo);
                    }
                    DormPayment userDormPayInfo = dormPaymentByUserIdCodeMap.get(idCodeKey);
                    if (userDormPayInfo == null) {
                        ksDormitoryReserveVo.setPayFlag(false);
                    } else {
                        ksDormitoryReserveVo.setPayFlag(DormPayment.PayStatus.paid.equals(userDormPayInfo.getPayStatus()));
                    }
                }
            }
            result.setItems(KsDormitoryReserve.getModelSet());
            result.setTotalRecord(KsDormitoryReserve.getTotal());
        }
        return result;
    }

    @Override
    public void confirmUserDormPayInfo(DormPayment userDormPayInfo) {
        if (userDormPayInfo.getIdCode() == null) {
            throw new ParamException("参数异常，用户身份证未传");
        }
        userDormPayInfo.setOperatorId(MyTokenUtils.getCurrentUser().getId());
        try {
            userDormPayInfo.setIdCode(MyEncryptorUtils.encryptByAES(userDormPayInfo.getIdCode()));
        } catch (Exception e) {
            logger.error(String.format("身份证 %s 信息加密异常..."), userDormPayInfo.getIdCode());
            logger.error(e.getMessage());
        }

        DormPayment dormPayment = dormPaymentMapper.selectByIdCardAndYearMonth(userDormPayInfo.getIdCode(), userDormPayInfo.getYear(), userDormPayInfo.getMonth());
        if (dormPayment != null) {
            dormPayment.setOperatorId(userDormPayInfo.getOperatorId());
            dormPayment.setUpdateTime(new Date());
            dormPayment.setPayStatus(userDormPayInfo.getPayStatus());
            dormPaymentMapper.updateByPrimaryKeySelective(dormPayment);
        } else {
            userDormPayInfo.setCreateTime(new Date());
            dormPaymentMapper.insertSelective(userDormPayInfo);
        }
    }

    private void copyUserMiniVoToKsDormitoryReserveVo(UserDormitoryReservationDto resource, KsDormitoryReserveVo target) {
        if (resource == null) {
            return;
        }
        target.setUserId(resource.getUserId());
        target.setCompanyId(resource.getCompanyId());
        target.setCompanyName(resource.getCompanyName());
        target.setProjectId(resource.getProjectId());
        target.setProjectName(resource.getProjectName());
        target.setType(resource.getType());
    }

    @Override
    public String exportExcel(LinkedHashSet<String> fields, List<KsDormitoryReserveVo> models) throws IOException {
        List<String> filterFields = null;
        // 过滤fields
        if (null == fields || fields.isEmpty()) {
            filterFields = cellNameMap.keySet().stream().collect(Collectors.toList());
        } else {
            filterFields = cellNameMap.keySet().stream().filter(cellName -> {
                return fields.contains(cellName);
            }).collect(Collectors.toList());
        }
        final Workbook wb = new HSSFWorkbook();
        final Sheet sheet = wb.createSheet();
        final CellStyle midStyle = wb.createCellStyle();
        midStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        buildHeadWrapper(buildHead(sheet, midStyle, this.translateField(filterFields)), wb);
        buildBodys(sheet, buildCommonStyle(wb), filterFields, this.translateVo(models));
        buildSheetWidth(sheet, filterFields.size());
        String fileName = exportFolder + fileNamepRrefix + ".xls";
        String path = filePathPrefix + fileName;
        OutputStream os = null;

        try {
            os = new FileOutputStream(path);
            wb.write(os);
        } finally {
            os.close();
            wb.close();
        }

        return fileName;
    }

    @Override
    public void updateNonCheckInOrNonCheckOutOrNonUserId() {
        Map<String, UserDormitoryReservationVo> userDormitoryReservationMap = new HashMap<>();
        List<String> liveNos = new ArrayList<>();
        userDormitoryReservationMapper.updateUserIdBycertificationNo();//更新员工user_id
        List<UserDormitoryReservation> listInfo = userDormitoryReservationMapper.selectUserIsCompanyIdOrProjectIdNull();//查询子公司或者项目为空的数据
        if (listInfo != null && !listInfo.isEmpty()) {
            userDormitoryReservationMapper.updateUserCompanyIdOrProjectIdById(listInfo);//根据id更新子公司或者项目为空的数据
        }
        List<UserDormitoryReservationVo> list = userDormitoryReservationMapper.selectByNonCheckInOrNonCheckOut();
        logger.info("开始查询宿舍入住表入住时间或者出住时间为空的情况， result -> {}", list);

        if (list == null || list.isEmpty()) {
            return;
        }
        list.stream().forEach(item -> {
            userDormitoryReservationMap.put(item.getLiveNo(), item);
            liveNos.add(item.getLiveNo());
        });

        Integer pageSize = 10000;
        List<List<String>> totalList = new ArrayList<>();
        List<String> l = null;
        for (int i = 0; i < liveNos.size(); i++) {
            if (i % pageSize == 0) {
                l = new ArrayList<>(pageSize);
                totalList.add(l);
            }
            l.add(liveNos.get(i));
        }
        List<KsDormitoryReserveVo> voList = new LinkedList<>();
        for (List<String> liveNoList : totalList) {
            try {
                Future<BaseQueryResponseVo> future = this.getKsDormitoryReserveVoAsync(liveNoList, pageSize);
                logger.debug("future isDone -> {}", future.isDone());
                BaseQueryResponseVo vo = future.get();
                logger.debug("vo -> {}", vo);
                List<KsDormitoryReserveVo> items = (List<KsDormitoryReserveVo>) vo.getItems();
                items.stream().forEach(item -> {
                    voList.add(item);
                });
            } catch (InterruptedException e) {
                logger.error("异步调用http接口报错 {}", e.getMessage());
            } catch (ExecutionException e) {
                logger.error("异步调用http接口报错 {}", e.getMessage());
            }
        }

        List<UserDormitoryReservationVo> userDormitoryReservations = new ArrayList<>();

        voList.stream().forEach(item -> {
            UserDormitoryReservationVo userDormitoryReservation = userDormitoryReservationMap.get(item.getLiveNo());
            userDormitoryReservation.setRoomNo(item.getRoomNo());
            userDormitoryReservation.setBedroomType(item.getBedroom());
            userDormitoryReservation.setBerthType(item.getBerth());
            userDormitoryReservation.setStatus(item.getLiveState());
            userDormitoryReservation.setCheckInDate(item.getCheckInDate());
            userDormitoryReservation.setCheckOutDate(item.getCheckOutDate());
            userDormitoryReservations.add(userDormitoryReservation);
        });

        logger.info("过滤入住时间和出住时间全部都为空的情况: result -> {}", userDormitoryReservations);
        if (userDormitoryReservations != null && !userDormitoryReservations.isEmpty()) {
            int result = userDormitoryReservationMapper.batchUpdateCheckInAndCheckout(userDormitoryReservations);
            logger.info("批量更新入住信息， result -> {}", result);
        } else {
            logger.info("此次不需要更新入住的 checkInDate 和 checkoutDate");
        }

    }

    @Override
    @Async
    public BaseQueryResponseVo getDormitoryReserveInfoByLiveNos(List<String> liveNos, Integer pageSize) {
        if (liveNos == null || liveNos.isEmpty()) throw new ParamException("liveNos 为空 ");
        if (pageSize == null || pageSize == 0) pageSize = 20;
        String url = "/kshome/house_room_live/list" + "?pageSize=" + pageSize;
        String loginId = userMapper.selectRandomActive().getLoginId();
        logger.info("loginId -> {}", loginId);
        KsHttpComponent component = new KsHttpComponent();
        BaseQueryResponseVo result = new BaseQueryResponseVo();
        JSONObject data = new JSONObject();
        data.put("liveNos", liveNos);
        logger.debug("data -> {}", data);
        String response = component.doPostHttpRequest(address + url, data.toJSONString(), loginId);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        logger.info("ksResult -> {}", ksResult);
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) == 0) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            KsDormitoryReserveVo KsDormitoryReserve = JSON.parseObject(decryptedData, KsDormitoryReserveVo.class);
            result.setItems(KsDormitoryReserve.getModelSet());
            result.setTotalRecord(KsDormitoryReserve.getTotal());
        } else {
            if (ksResult.getCode() != 0) {
                throw new KsException(ksResult.getCode(), ksResult.getMsg());
            }
        }
        return result;
    }

    private CellStyle buildHead(final Sheet sheet, final CellStyle cellStyle, List<String> filterFields) {
        Row firstRow = sheet.createRow(0);
        this.buildRow(firstRow, cellStyle, filterFields);
        return cellStyle;
    }

    private void buildBodys(final Sheet sheet, final CellStyle cellStyle, List<String> filterFields, List<KsDormitoryReserveVo> models) {
        models.stream().forEach(model -> {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            buildRow(row, cellStyle, getKsDormitoryReserveVoDeclareFieldsValue(filterFields, model));
        });
    }

    @Override
    public List<String> getKsDormitoryReserveVoDeclareFieldsValue(List<String> filterFields, Object model) {

        List<String> values = new ArrayList<>();
        for (String fieldName : filterFields) {
            Field field = null;
            try {
                field = model.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(model);
                values.add(String.valueOf(value));
            } catch (NoSuchFieldException e) {
                logger.error("NoSuchFieldException {}", e.getMessage());
                continue;
            } catch (IllegalAccessException e1) {
                logger.error("IllegalAccessException {}", e1.getMessage());
                continue;
            }
        }
        return values;
    }

    private void buildRow(final Row row, final CellStyle cellStyle, List<String> filterFields) {
        Integer size = filterFields.size();
        for (Integer i = 0; i < size; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(filterFields.get(i));
        }
    }

    private void buildSheetWidth(final Sheet sheet, final Integer columeNums) {
        int length = "每月工作周期的工作日".getBytes().length * 256;
        for (Integer i = 0; i < columeNums; i++) {
            sheet.setColumnWidth(i, length);
        }
    }

    private void buildSheetWidth(final Sheet sheet, final List<String> fields) {
        for (Integer i = 0; i < fields.size(); i++) {
            Integer size = fields.get(i).getBytes().length * 256 + 3000;
            if (i == 3) {
                sheet.setColumnWidth(i, "本月人才公寓第三间房数".getBytes().length * 256 + 300);
                continue;
            }
            sheet.setColumnWidth(i, size);
        }
    }


    private List<String> translateField(List<String> fields) {
        return fields.stream().map(field -> {
            return cellNameMap.get(field);
        }).collect(Collectors.toList());
    }

    private List<KsDormitoryReserveVo> translateVo(List<KsDormitoryReserveVo> ksDormitoryReserveVoList) {
        return ksDormitoryReserveVoList.stream().map(ksDormitoryReserveVo -> {
            if (ksDormitoryReserveVo.getBerth().equals("NONE"))
                ksDormitoryReserveVo.setBedroom(bedroomMap.get(ksDormitoryReserveVo.getBedroom()));
            else ksDormitoryReserveVo.setBedroom(doomMap.get(ksDormitoryReserveVo.getBedroom()));
            ksDormitoryReserveVo.setBerth(berthMap.get(ksDormitoryReserveVo.getBerth()));
            ksDormitoryReserveVo.setLiveState(ksDormitoryReserveVo.getLiveStateDesc());
            if (!StringUtils.isBlank(ksDormitoryReserveVo.getCheckInDate())) {
                if (ksDormitoryReserveVo.getCheckInDate().startsWith("1970-01-01"))
                    ksDormitoryReserveVo.setCheckInDate("暂未入住");
            }

            if (!StringUtils.isBlank(ksDormitoryReserveVo.getCheckOutDate())) {
                if (ksDormitoryReserveVo.getCheckOutDate().startsWith("1970-01-01"))
                    ksDormitoryReserveVo.setCheckOutDate("暂未退房");
            }
            return ksDormitoryReserveVo;
        }).collect(Collectors.toList());
    }


    @Async
    @Override
    public Future<BaseQueryResponseVo> getKsDormitoryReserveVoAsync(List<String> liveNos, Integer pageSize) {
        logger.info("开启异步http请求 --- currentThread {}", Thread.currentThread().getName());
        return new AsyncResult<BaseQueryResponseVo>(this.getDormitoryReserveInfoByLiveNos(liveNos, pageSize));
    }

    public List<UserDormitoryParamDto> getUserDormitoryParam(Integer year, Integer month) {
        List<UserDormitoryParam> list = userDormitoryParamMapper.selectAll(year, month);
        List<UserDormitoryParamDto> items = new ArrayList<UserDormitoryParamDto>();
        UserDormitoryParamDto vo = null;
        if (list != null) {
            for (UserDormitoryParam userDormitoryParam : list) {
                vo = new UserDormitoryParamDto();
                vo.setYear(userDormitoryParam.getYear());
                vo.setValue(userDormitoryParam.getValue());
                vo.setMonth(userDormitoryParam.getMonth());
                vo.setName(userDormitoryParam.getName());
                vo.setId(userDormitoryParam.getId());
                items.add(vo);
            }
        }
        return items;
    }

    @Override
    public boolean saveOrUpdateUserDormitoryParam(List<UserDormitoryParamDto> list) {
        for (UserDormitoryParamDto userDormitoryParamDto : list) {
            UserDormitoryParam record = new UserDormitoryParam();
            record.setId(userDormitoryParamDto.getId());
            record.setName(userDormitoryParamDto.getName());
            record.setMonth(userDormitoryParamDto.getMonth());
            record.setValue(userDormitoryParamDto.getValue());
            record.setYear(userDormitoryParamDto.getYear());
            if (record.getId() == null) {
                UserDormitoryParam userDormitoryParam = userDormitoryParamMapper.selectByKey(userDormitoryParamDto);
                if (userDormitoryParam != null) {
                    return false;
                }
                userDormitoryParamMapper.insertSelective(record);
            } else {
                userDormitoryParamMapper.updateByPrimaryKey(record);
            }
        }
        return true;
    }

    @Override
    public int deleteUserDormitoryParam(Long id) {
        return userDormitoryParamMapper.deleteByPrimaryKey(id);
    }

    public String userDormitoryReport(Integer year, Integer month) {
        String url = null;
        String startDate = null;
        String endDate = null;
        Calendar calendar = Calendar.getInstance();
        int now_y = calendar.get(Calendar.YEAR);//得到年份
        int now_m = calendar.get(Calendar.MONTH) + 1;//得到月份
        List<Date> listDate = MyDateUtils.getLastDay(year, month);
        Map<String, UserDormitoryParamDto> mapParam = getParam(year, month);
        if (checkParam(mapParam)) {
            if (now_y == year && now_m == month) {//当前年月
                startDate = MyDateUtils.dateToString(listDate.get(0), MyDateUtils.DATE_FORMAT_YYYY_MM_DD) + " 00:00:00";
                endDate = MyDateUtils.dateToString(new Date(), MyDateUtils.DATE_FORMAT_YYYY_MM_DD_HHMMSS);
            } else {//不是当前年月
                startDate = MyDateUtils.dateToString(listDate.get(0), MyDateUtils.DATE_FORMAT_YYYY_MM_DD) + " 00:00:00";
                endDate = MyDateUtils.dateToString(listDate.get(1), MyDateUtils.DATE_FORMAT_YYYY_MM_DD) + " 23:59:00";
            }
            Map<String, String> header = new HashMap<String, String>();
            Map<String, List<UserDormitoryReportDto>> dormitory = new HashMap<String, List<UserDormitoryReportDto>>();//员工宿舍
            Map<String, List<UserDormitoryReportDto>> apartmentARoom = new HashMap<String, List<UserDormitoryReportDto>>();//人才公寓主卧
            Map<String, List<UserDormitoryReportDto>> apartmentBRoom = new HashMap<String, List<UserDormitoryReportDto>>();//人才公寓次卧
            Map<String, List<UserDormitoryReportDto>> apartmentCRoom = new HashMap<String, List<UserDormitoryReportDto>>();//人才公寓第三间
            List<UserDormitoryReportDto> list = userDormitoryReservationMapper.selectUserDormitoryReport(startDate, endDate);
            if (list != null) {
                for (UserDormitoryReportDto vo : list) {
                    group(vo, dormitory, apartmentARoom, apartmentBRoom, apartmentCRoom, header);//分组
                }
            }
            DormitoryReportExportDto dormitoryReportExportDto = calculation(header, dormitory, apartmentARoom, apartmentBRoom, apartmentCRoom);//导出数据集合
            sumDormitoryInfo(mapParam, dormitoryReportExportDto, year, month);
            logger.debug("dormitoryReportExportDto -> {}", dormitoryReportExportDto);
            String fields[] = {"companyCode", "projectName", "allDormitoryRoom", "allDormitoryPercent", "fee", "allApartmentRoom", "aRoom", "aRoomPercent", "afee", "bRoom", "bRoomPercent", "bfee", "cRoom", "cRoomPercent", "cfee"};
            String showFields[] = {"companyCode", "projectName", "allDormitoryRoom", "allDormitoryPercent", "allApartmentRoom", "aRoom", "aRoomPercent", "bRoom", "bRoomPercent", "cRoom", "cRoomPercent"};
            url = exportExcel(Arrays.asList(fields), Arrays.asList(showFields), dormitoryReportExportDto, year, month);
        }
        return url;
    }

    @Override
    public CommonResponse importDormitoryReserve(MultipartFile multipartFile) throws Exception {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ParamException("上传文件错误,请按照模板填写表格数据");
        }
        List<KsDormitoryReserveVo> result = new ArrayList<KsDormitoryReserveVo>();
        LinkedList<String> warnlog = new LinkedList<>();
        LinkedList<String> errorlog = new LinkedList<>();
        Workbook wb;
        try {
            ExcelHelper.saveExcelBackup(multipartFile, filePathPrefix + filePathBackup);
            wb = WorkbookFactory.create(multipartFile.getInputStream());
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }
        List<SubCompanyProjectVO> subCompanyProjectVOs = subcompanyMapper.selectCompanyInfo();
        Map<String, Map<String, SubCompanyProjectVO>> projectVoMapByCompanyNameMap = new HashMap<>();
        for (SubCompanyProjectVO item : subCompanyProjectVOs) {
            if (projectVoMapByCompanyNameMap.containsKey(item.getCompanyName())) {
                projectVoMapByCompanyNameMap.get(item.getCompanyName()).put(item.getPlatName(), item);
            } else {
                Map<String, SubCompanyProjectVO> platVoByPlatNameMap = new HashMap<>();
                platVoByPlatNameMap.put(item.getPlatName(), item);
                projectVoMapByCompanyNameMap.put(item.getCompanyName(), platVoByPlatNameMap);
            }
        }
        List<UserCertificateVo> userCertificateVos = userMapper.selectUserCertificateInfo();
        //读取表格
        //for (int i = 0; i < wb.getNumberOfSheets(); i++) {
        Sheet sheetAt = wb.getSheetAt(0);
        int rowMaxNum = sheetAt.getLastRowNum();
        KsDormitoryReserveVo ksDormitoryReserveVo;
        aaa:
        for (int rowNum = 1; rowNum < rowMaxNum; rowNum++) {
            //是否存在错误
            Boolean isError = false;
            Row row = sheetAt.getRow(rowNum);
            if (MyCellUtils.getCellValue(row.getCell(0)).isEmpty() || row.getCell(0) == null) {
                break;
            }
            if (MyCellUtils.getCellValue(row.getCell(0)).isEmpty() || MyCellUtils.getCellValue(row.getCell(1)).isEmpty() || MyCellUtils.getCellValue(row.getCell(3)).isEmpty() || MyCellUtils.getCellValue(row.getCell(4)).isEmpty() || MyCellUtils.getCellValue(row.getCell(5)).isEmpty() ||
                    MyCellUtils.getCellValue(row.getCell(6)).isEmpty() || MyCellUtils.getCellValue(row.getCell(7)).isEmpty() || MyCellUtils.getCellValue(row.getCell(8)).isEmpty() || MyCellUtils.getCellValue(row.getCell(9)).isEmpty() || MyCellUtils.getCellValue(row.getCell(10)).isEmpty() || MyCellUtils.getCellValue(row.getCell(11)).isEmpty() || MyCellUtils.getCellValue(row.getCell(12)).isEmpty()) {
//            if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(3) == null || row.getCell(4) == null || row.getCell(5) == null || row.getCell(6) == null || row.getCell(7) == null || row.getCell(8) == null ||
//                    row.getCell(9) == null || row.getCell(10) == null || row.getCell(11) == null || row.getCell(12) == null) {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:必填项存在空数据\r\n");
                continue;
            }
            //读取表格信息并校验
            String roomNO = MyCellUtils.getCellValue(row.getCell(0));
            String bedroom = MyCellUtils.getCellValue(row.getCell(1));
            String berth = MyCellUtils.getCellValue(row.getCell(2));
            if (KsDormitoryReserveVo.bedrooms.contains(bedroom)) {
                if (KsDormitoryReserveVo.dormitory.contains(bedroom)) {
                    switch (berth) {
                        case KsDormitoryReserveVo.KsDormitoryExcelInfo.berth1:
                            berth = A_UPPER;
                            break;
                        case KsDormitoryReserveVo.KsDormitoryExcelInfo.berth2:
                            berth = B_LOWER;
                            break;
                        default:
                            errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:床位类型错误\r\n");
                            isError = true;
                    }
                } else {
                    berth = null;
                }
            }
            switch (bedroom) {
                case KsDormitoryReserveVo.KsDormitoryExcelInfo.bedroom1:
                    bedroom = DORMITORY_A_ROOM1;
                    break;
                case KsDormitoryReserveVo.KsDormitoryExcelInfo.bedroom2:
                    bedroom = DORMITORY_B_ROOM2;
                    break;
                case KsDormitoryReserveVo.KsDormitoryExcelInfo.bedroom3:
                    bedroom = DORMITORY_C_ROOM3;
                    break;
                case KsDormitoryReserveVo.KsDormitoryExcelInfo.bedroom4:
                    bedroom = DORMITORY_A_ROOM1;
                    break;
                case KsDormitoryReserveVo.KsDormitoryExcelInfo.bedroom5:
                    bedroom = DORMITORY_B_ROOM2;
                    break;
                case KsDormitoryReserveVo.KsDormitoryExcelInfo.bedroom6:
                    bedroom = DORMITORY_C_ROOM3;
                    break;
                default:
                    errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:卧室类型错误\r\n");
                    isError = true;
            }
            String workStatus = MyCellUtils.getCellValue(row.getCell(3));
            Integer type;
            if (KsDormitoryReserveVo.KsDormitoryExcelInfo.type2.equals(workStatus)) {
                type = 1;
            } else if (KsDormitoryReserveVo.KsDormitoryExcelInfo.type1.equals(workStatus)) {
                type = 2;
            } else if (KsDormitoryReserveVo.KsDormitoryExcelInfo.type3.equals(workStatus)) {
                type = 3;
            } else {
                type = -1;
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:人员类型填写错误\r\n");
                isError = true;
            }
            String name = MyCellUtils.getCellValue(row.getCell(4));
            String sex = MyCellUtils.getCellValue(row.getCell(5));
            Integer gender = -1;
            if ("男".equals(sex)) {
                gender = 0;
            } else if ("女".equals(sex)) {
                gender = 1;
            } else {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:性别填写错误\r\n");
                isError = true;
            }
            String mobile = MyCellUtils.getCellValue(row.getCell(6));
            if (!MyStringUtils.isPhoneLegal(mobile)) {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:手机号格式错误\r\n");
                isError = true;
            }
            String certificateType = MyCellUtils.getCellValue(row.getCell(7));
            if (!KsDormitoryReserveVo.certification.contains(certificateType)) {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:请检查证件类型,身份证或者护照\r\n");
                isError = true;
            }
            String idCode = MyCellUtils.getCellValue(row.getCell(8));
            //证件校验
            boolean nameMatchCertification = false;
            if (MyStringUtils.isIdCardOrPassport(idCode)) {
                if ("在职员工".equals(workStatus)) {
                    for (UserCertificateVo userCertificateVo : userCertificateVos) {
                        if (userCertificateVo != null && userCertificateVo.getCertificationNo() != null && userCertificateVo.getUserName() != null) {
                            if (userCertificateVo.getUserName().equals(name) && userCertificateVo.getCertificationNo().equals(MyEncryptorUtils.encryptByAES(idCode))) {
                                nameMatchCertification = true;
                                break;
                            }
                        }
                    }
                    //在用户名证件号码不匹配时添加错误信息
                    if (nameMatchCertification == false)
                        warnlog.add("--第 " + (rowNum + 1) + " 行:用户名和用户证件号码可能不匹配值\r\n");
//                        continue;
                }
            } else {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:请检查证件号码格式\r\n");
                isError = true;
            }
            String companyName = MyCellUtils.getCellValue(row.getCell(9));
            String projectName = MyCellUtils.getCellValue(row.getCell(10));
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateIn = row.getCell(11).getDateCellValue();
            Date dateOut = row.getCell(12).getDateCellValue();
            if (dateOut.before(dateIn)) {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:请检查日期\r\n");
                isError = true;
            }
            String expectCheckInDate = sdf.format(dateIn);
            String expectCheckOutDate = sdf.format(dateOut);
            ksDormitoryReserveVo = new KsDormitoryReserveVo(rowNum, roomNO, bedroom, berth, type, name, gender, mobile, certificateType, idCode, expectCheckInDate, expectCheckOutDate);

            //判断公司部门归属是否正确
            if (projectVoMapByCompanyNameMap.containsKey(companyName)) {
                Map<String, SubCompanyProjectVO> platVoByPlatNameMap = projectVoMapByCompanyNameMap.get(companyName);
                if (platVoByPlatNameMap.containsKey(projectName)) {
                    ksDormitoryReserveVo.setCompanyId(platVoByPlatNameMap.get(projectName).getCompanyId());
                    ksDormitoryReserveVo.setProjectId(platVoByPlatNameMap.get(projectName).getPlatId());
                } else {
                    errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:请检查该部门公司是否匹配\r\n");
                    isError = true;
                }
            } else {
                errorlog.add("--第 " + (rowNum + 1) + " 行:数据校验错误,错误原因:请检查该公司是否存在\r\n");
                isError = true;
            }
            //有错误信息就过滤掉该条信息
            if (isError == true) {
                continue aaa;
            }
            result.add(ksDormitoryReserveVo);
        }

//        }
        //复用原接口获取返回code message
        List<Integer> code = new ArrayList<>();
        List<String> message = new ArrayList<>();
        if (result == null || result.isEmpty()) {
            return null;
        }
        for (KsDormitoryReserveVo ksDormitoryReserveVo1 : result) {
            ResponseEntity<?> responseEntity;
            try {
                responseEntity = addDormitoryReserve(ksDormitoryReserveVo1);
            } catch (Exception e) {
                logger.error(e.getMessage());
                errorlog.add("--第 " + (ksDormitoryReserveVo1.getRow() + 1) + " 行:系统处理错误,未知原因\r\n");
                continue;
            }
            CommonResponse body = (CommonResponse) responseEntity.getBody();
            if (body.getCode() != 0) {
                code.add(body.getCode());
                message.add(body.getMessage());
                errorlog.add("--第 " + (ksDormitoryReserveVo1.getRow() + 1) + " 行:系统处理错误,错误原因:" + body.getMessage() + "\r\n");
                continue;
            } else {
                code.add(0);
                message.add("success");
            }
        }

        //将表格错误信息写入到txt
        Collections.sort(errorlog, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1.substring(4, 5)) - Integer.parseInt(o2.substring(4, 5));
            }
        });
        errorlog.addFirst("错误信息 \r\n");
        warnlog.addFirst("警告信息 \r\n");
        errorlog.addAll(warnlog);
        final String fileName = "宿舍预定反馈信息表";
//        ExcelHelper.saveErrorFile(errorlog, "dormitory-import/", fileName);
        MyFileUtils.outputListToFile(errorlog, filePathPrefix + filePathBackup + File.separator + fileName + ".txt");
        String downloadURL = exportFolder + File.separator + fileName + ".txt";
        if (errorlog.size() > 0) {
            MyFileUtils.copyFile(filePathPrefix + filePathBackup + File.separator + fileName + ".txt", filePathPrefix + downloadURL);
        }
        if (code.contains(0) || message.contains("success")) {
            return new CommonResponse(0, "success", downloadURL);
        }
        return new CommonResponse(-1, "表格存在错误数据,请重新检查表格中的数据", downloadURL);
    }

    /**
     * 住宿信息分组
     *
     * @param vo
     * @param dormitory
     * @param apartmentARoom
     * @param apartmentBRoom
     * @param apartmentCRoom
     */
    private void group(UserDormitoryReportDto vo, Map<String, List<UserDormitoryReportDto>> dormitory, Map<String, List<UserDormitoryReportDto>> apartmentARoom, Map<String, List<UserDormitoryReportDto>> apartmentBRoom, Map<String, List<UserDormitoryReportDto>> apartmentCRoom, Map<String, String> header) {
        Long companyId;//公司ID
        Long projectId;//项目ID
        String companyCode;//公司编码,报表导出使用
        String projectName;//项目,报表导出使用
        String bedroomType;//房间类型
        String berthType;//上下铺(公寓为空)
        String key = null;
        String value = null;
        List<UserDormitoryReportDto> dormitoryList = null;
        List<UserDormitoryReportDto> apartmentARoomList = null;
        List<UserDormitoryReportDto> apartmentBRoomList = null;
        List<UserDormitoryReportDto> apartmentCRoomList = null;
        companyId = vo.getCompanyId();
        projectId = vo.getProjectId();

        companyCode = vo.getCompanyCode();
        if (StringUtils.isEmpty(companyCode)) {
            companyCode = "行政专项";
        }
        projectName = vo.getProjectName();
        if (StringUtils.isEmpty(projectName)) {
            projectName = "行政专项";
        }
        key = companyId + "|" + projectId;
        value = companyCode + "|" + projectName;
        bedroomType = vo.getBedroomType();
        berthType = vo.getBerthType();
        header.put(key, value);
        if (StringUtils.isNotEmpty(berthType) && !"".equals(berthType.trim()) && !"NONE".equalsIgnoreCase(berthType.trim())) {//宿舍
            if (dormitory.containsKey(key)) {
                dormitoryList = dormitory.get(key);
                dormitoryList.add(vo);
            } else {
                dormitoryList = new ArrayList<UserDormitoryReportDto>();
                dormitoryList.add(vo);
                dormitory.put(key, dormitoryList);
            }
        } else {//公寓
            if (DORMITORY_A_ROOM1.equalsIgnoreCase(bedroomType)) {
                if (apartmentARoom.containsKey(key)) {
                    apartmentARoomList = apartmentARoom.get(key);
                    apartmentARoomList.add(vo);
                } else {
                    apartmentARoomList = new ArrayList<UserDormitoryReportDto>();
                    apartmentARoomList.add(vo);
                    apartmentARoom.put(key, apartmentARoomList);
                }
            } else if (DORMITORY_B_ROOM2.equalsIgnoreCase(bedroomType)) {
                if (apartmentBRoom.containsKey(key)) {
                    apartmentBRoomList = apartmentBRoom.get(key);
                    apartmentBRoomList.add(vo);
                } else {
                    apartmentBRoomList = new ArrayList<UserDormitoryReportDto>();
                    apartmentBRoomList.add(vo);
                    apartmentBRoom.put(key, apartmentBRoomList);
                }
            } else if (DORMITORY_C_ROOM3.equalsIgnoreCase(bedroomType)) {
                if (apartmentCRoom.containsKey(key)) {
                    apartmentCRoomList = apartmentCRoom.get(key);
                    apartmentCRoomList.add(vo);
                } else {
                    apartmentCRoomList = new ArrayList<UserDormitoryReportDto>();
                    apartmentCRoomList.add(vo);
                    apartmentCRoom.put(key, apartmentCRoomList);
                }
            }
        }

    }

    /**
     * 计算占比
     *
     * @param header
     * @param dormitory
     * @param apartmentARoom
     * @param apartmentBRoom
     * @param apartmentCRoom
     */
    private DormitoryReportExportDto calculation(Map<String, String> header, Map<String, List<UserDormitoryReportDto>> dormitory, Map<String, List<UserDormitoryReportDto>> apartmentARoom, Map<String, List<UserDormitoryReportDto>> apartmentBRoom, Map<String, List<UserDormitoryReportDto>> apartmentCRoom) {
        DormitoryReportExportDto dormitoryReportExportDto = new DormitoryReportExportDto();//导出数据集合
        List<UserDormitoryReportDto> list = new ArrayList<UserDormitoryReportDto>();
        UserDormitoryReportDto userDormitoryReportDto = null;
        Set<String> keys = header.keySet();
        String str = null;
        String[] codes = null;
        for (String key : keys) {
            str = header.get(key);
            codes = str.split("\\|");
            userDormitoryReportDto = new UserDormitoryReportDto();
            userDormitoryReportDto.setCompanyCode(codes[0]);//公司
            userDormitoryReportDto.setProjectName(codes[1]);//项目
            Integer itemsAllDay = getDays(dormitory.get(key));//宿舍总天数
            userDormitoryReportDto.setAllDormitoryRoom(itemsAllDay);
            Integer itemsARoomDay = getDays(apartmentARoom.get(key));//人才公寓主卧总天数
            userDormitoryReportDto.setaRoom(itemsARoomDay);
            Integer itemsBRoomDay = getDays(apartmentBRoom.get(key));//人才公寓次卧总天数
            userDormitoryReportDto.setbRoom(itemsBRoomDay);
            Integer itemsCRoomDay = getDays(apartmentCRoom.get(key));//人才公寓第三房总天数
            userDormitoryReportDto.setcRoom(itemsCRoomDay);
            userDormitoryReportDto.setAllApartmentRoom(itemsARoomDay + itemsBRoomDay + itemsCRoomDay);//人才公寓总人住天数
            list.add(userDormitoryReportDto);
        }

        dormitoryReportExportDto.setList(list);
        return dormitoryReportExportDto;
    }

    /**
     * 获取项目入住总天数
     *
     * @param list
     * @return
     */
    private Integer getDays(List<UserDormitoryReportDto> list) {
        Integer days = 0;
        if (list != null) {
            for (UserDormitoryReportDto dto : list) {
                days = days + dto.getDays();
            }
        }
        return days;
    }

    /**
     * 获取参数
     *
     * @return
     */
    private Map<String, UserDormitoryParamDto> getParam(Integer year, Integer month) {
        List<UserDormitoryParam> lsitParam = userDormitoryParamMapper.selectAll(year, month);
        if (lsitParam == null || lsitParam.size() < 1) {
            lsitParam = userDormitoryParamMapper.selectUserDormitoryParam();
        }
        Map<String, UserDormitoryParamDto> map = new HashMap<String, UserDormitoryParamDto>();
        UserDormitoryParamDto userDormitoryParamDto = null;
        for (UserDormitoryParam up : lsitParam) {
            userDormitoryParamDto = new UserDormitoryParamDto();
            userDormitoryParamDto.setName(up.getName());
            userDormitoryParamDto.setValue(up.getValue());
            if (DORMITORY_A_ROOM.equalsIgnoreCase(up.getName())) {
                userDormitoryParamDto.setFullName("本月人才公寓主卧数");
                map.put(DORMITORY_A_ROOM, userDormitoryParamDto);
            } else if (DORMITORY_B_ROOM.equalsIgnoreCase(up.getName())) {
                userDormitoryParamDto.setFullName("本月人才公寓次卧数");
                map.put(DORMITORY_B_ROOM, userDormitoryParamDto);
            } else if (DORMITORY_C_ROOM.equalsIgnoreCase(up.getName())) {
                userDormitoryParamDto.setFullName("本月人才公寓第三间房数");
                map.put(DORMITORY_C_ROOM, userDormitoryParamDto);
            }
            if (DORMITORY_All_ROOM.equalsIgnoreCase(up.getName())) {
                userDormitoryParamDto.setFullName("本月员工宿舍床位数");
                map.put(DORMITORY_All_ROOM, userDormitoryParamDto);
            }
        }
        return map;
    }

    /**
     * 计算占比
     *
     * @param year
     * @param month
     * @param key
     * @param currDays
     * @return
     */
    private String calPercentage(Map<String, UserDormitoryParamDto> map, Integer year, Integer month, String key, Integer currDays) {
        UserDormitoryParamDto userDormitoryParamDto = map.get(key);
        BigDecimal result = BigDecimal.ZERO;
        String percentage = "0";
        if (userDormitoryParamDto != null && userDormitoryParamDto.getValue() != null && userDormitoryParamDto.getValue() != 0) {
            BigDecimal m_days = new BigDecimal(MyDateUtils.geDaysByYYMM(year, month));//当月天数
            BigDecimal bedNum = new BigDecimal(userDormitoryParamDto.getValue());//床位数
            BigDecimal currentNum = new BigDecimal(currDays);//当前项目天数
            BigDecimal total = m_days.multiply(bedNum);
            result = currentNum.divide(total, 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("100").setScale(2, BigDecimal.ROUND_HALF_UP));
            DecimalFormat df = new DecimalFormat("0.00");
            percentage = df.format(result) + "%";
        }
        return percentage;
    }

    /**
     * 计算合计、占比信息
     *
     * @param dormitoryReportExportDto
     * @param year
     * @param month
     */
    private void sumDormitoryInfo(Map<String, UserDormitoryParamDto> mapParam, DormitoryReportExportDto dormitoryReportExportDto, Integer year, Integer month) {
        List<UserDormitoryReportDto> list = dormitoryReportExportDto.getList();
        Integer allDormitoryRoom = 0;//合计:员工宿舍入住人天数
        Integer allApartmentRoom = 0;//合计:公寓总人天数
        Integer aRoom = 0;//合计:公寓主卧人天数
        Integer bRoom = 0;//合计:公寓次卧人天数
        Integer cRoom = 0;//合计:公寓第三间房人天数
        String allDormitoryPercent;//员工宿舍占比
        String aRoomPercent;//公寓主卧占比
        String bRoomPercent;//公寓次卧占比
        String cRoomPercent;//公寓第三间房占比
        if (list != null) {
            for (UserDormitoryReportDto dto : list) {
                allDormitoryRoom = allDormitoryRoom + dto.getAllDormitoryRoom();
                allApartmentRoom = allApartmentRoom + dto.getAllApartmentRoom();
                aRoom = aRoom + dto.getaRoom();
                bRoom = bRoom + dto.getbRoom();
                cRoom = cRoom + dto.getcRoom();

                allDormitoryPercent = calPercentage(mapParam, year, month, DORMITORY_All_ROOM, dto.getAllDormitoryRoom());
                dto.setAllDormitoryPercent(allDormitoryPercent);
                aRoomPercent = calPercentage(mapParam, year, month, DORMITORY_A_ROOM, dto.getaRoom());
                dto.setaRoomPercent(aRoomPercent);
                bRoomPercent = calPercentage(mapParam, year, month, DORMITORY_B_ROOM, dto.getbRoom());
                dto.setbRoomPercent(bRoomPercent);
                cRoomPercent = calPercentage(mapParam, year, month, DORMITORY_C_ROOM, dto.getcRoom());
                dto.setcRoomPercent(cRoomPercent);
            }
            dormitoryReportExportDto.setAllDormitoryRoom(allDormitoryRoom);
            dormitoryReportExportDto.setAllApartmentRoom(allApartmentRoom);
            dormitoryReportExportDto.setaRoom(aRoom);
            dormitoryReportExportDto.setbRoom(bRoom);
            dormitoryReportExportDto.setcRoom(cRoom);
            dormitoryReportExportDto.setmDays(MyDateUtils.geDaysByYYMM(year, month));
            dormitoryReportExportDto.setParameters(mapParam);
        }
        ;


    }


    private void buildBodyWrapper(final CellStyle cellStyle, final Workbook wb) {

        Font font = wb.createFont();//#C0C0C0
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        cellStyle.setFont(font);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

    }


    private void buildHeadWrapper(final CellStyle cellStyle, final Workbook wb) {

        Font font = wb.createFont();//#C0C0C0
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setFontName("微软雅黑");
        cellStyle.setFont(font);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

    }

    private String exportExcel(List<String> fields, List<String> showFields, DormitoryReportExportDto dto, Integer year, Integer month) {
        final Workbook wb = new HSSFWorkbook();
        final Sheet sheet = wb.createSheet();
        final CellStyle midStyle = wb.createCellStyle();
        List<String> chineseField = this.translateField(fields);
        buildHeadWrapper(buildHead(sheet, midStyle, chineseField), wb);

        buildSheetWidth(sheet, chineseField);

        final CellStyle bodyStyle = wb.createCellStyle();

        final CellStyle specicalCellStyle = buildSpecialCellStyle(wb);

        Integer columns[] = {1, 2};

        buildBodyWrapper(buildBodys(sheet, bodyStyle, specicalCellStyle, Arrays.asList(columns), fields, showFields, dto), wb);

        Map<String, UserDormitoryParamDto> paramDtoMap = dto.getParameters();

        UserDormitoryParamDto allRoom = paramDtoMap.get("All_ROOM");
        UserDormitoryParamDto aRoom = paramDtoMap.get("A_ROOM");
        UserDormitoryParamDto bRoom = paramDtoMap.get("B_ROOM");
        UserDormitoryParamDto cRoom = paramDtoMap.get("C_ROOM");

        String datas[][] = {
                {"基础数据", "当月天数", dto.getmDays() + ""},
                {"", "本月员工宿舍床位数", allRoom != null ? allRoom.getValue() + "" : ""},
                {"", "本月人才公寓主卧数", aRoom != null ? aRoom.getValue() + "" : ""},
                {"", "本月人才公寓次卧数", bRoom != null ? bRoom.getValue() + "" : ""},
                {"", "本月人才公寓第三间房数", cRoom != null ? cRoom.getValue() + "" : ""}
        };

        Integer startRow = dto.getList().size() + 3;
        List<List<String>> data = generateTailData(datas);

        buildTail(sheet, buildTailStyle(wb), startRow, data);

        buildTail(sheet, buildTailStyle(wb), startRow + data.size() + 1, generateTailData(tailData));

        String fileName = exportFolder + dormitoryReverseFileNamePrefix + year + "_" + month + ".xls";
        String path = filePathPrefix + fileName;

        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            wb.write(os);
            wb.close();
        } catch (FileNotFoundException e) {
            logger.error("找不到文件", e);
        } catch (IOException e) {
            logger.error("生成excel异常", e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }


    private CellStyle buildBodys(final Sheet sheet, CellStyle cellStyle, CellStyle specialCellStyle, List<Integer> columns, List<String> fields, List<String> showFields, DormitoryReportExportDto dto) {

        // 过滤null

        List<UserDormitoryReportDto> nullCompCode = new LinkedList<>();
        List<UserDormitoryReportDto> notNullCompCode = new LinkedList<>();

        dto.getList().stream().forEach(item -> {
            if (item.getCompanyCode() == null) nullCompCode.add(item);
            else notNullCompCode.add(item);
        });

        notNullCompCode.sort((a, b) -> {
            return a.getCompanyCode().compareTo(b.getCompanyCode());
        });

        notNullCompCode.addAll(nullCompCode);

        dto.setList(notNullCompCode);

        Set<String> companyCodes = new HashSet<>();

        dto.getList().stream().forEach(model -> {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            if (!companyCodes.contains(model.getCompanyCode())) {
                companyCodes.add(model.getCompanyCode());
            } else {
                model.setCompanyCode("");
            }
            buildRow(row, cellStyle, specialCellStyle, columns, fields, showFields, getKsDormitoryReserveVoDeclareFieldsValue(showFields, model));
        });
        //合计

        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        String field[] = {"合计", " ", dto.getAllDormitoryRoom() + "", "", "", dto.getAllApartmentRoom() + "", dto.getaRoom() + "", "", "", dto.getbRoom() + "", "", "", dto.getcRoom() + "", "", ""};
        List<String> list = Arrays.asList(field);
        buildRow(row, cellStyle, specialCellStyle, columns, list, list, list);
        return cellStyle;
    }

    private void buildRow(Row row, CellStyle cellStyle, CellStyle specialCellStyle, List<Integer> columns, List<String> fields, List<String> showFields, List<String> values) {
        Integer pointer = 0;
        for (Integer i = 0; i < fields.size(); i++) {
            Cell cell = row.createCell(i);
            String filed = fields.get(i);
            if (showFields.contains(filed)) {
                cell.setCellValue(values.get(pointer));
                pointer++;
            }

            if (columns.contains(pointer)) {
                cell.setCellStyle(specialCellStyle);
            } else {
                cell.setCellStyle(cellStyle);
            }

        }
    }


    private CellStyle buildSpecialCellStyle(Workbook wb) {

        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();//#C0C0C0
        font.setColor(HSSFColor.GREY_50_PERCENT.index);
        font.setFontHeightInPoints((short) 11);
        font.setFontName(FontFamily.MODERN.name());
        cellStyle.setFont(font);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        return cellStyle;
    }

    private CellStyle buildTailStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();//#C0C0C0
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setFontName("微软雅黑");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        return cellStyle;
    }

    private CellStyle buildCommonStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();//#C0C0C0
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }


    private void buildTail(final Sheet sheet, final CellStyle cellStyle, Integer startRow, List<List<String>> values) {

        for (List<String> list : values) {
            Row row = sheet.createRow(startRow++);
            Integer startColumn = 2;
            for (String value : list) {
                Cell cell = row.createCell(startColumn++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(value);
            }
        }
    }

    private List<List<String>> generateTailData(String datas[][]) {

        List<List<String>> values = new ArrayList<>(5);

        for (String data[] : datas) {
            List<String> l = new ArrayList<>(4);
            for (String d : data) {
                l.add(d);
            }
            values.add(l);
        }
        return values;
    }

    /**
     * 校验床位信息是否配置，并且是否为0,分母不能为0
     * 员工宿舍占比 = （项目员工宿舍总人天数）/ ( 员工宿舍总床位数 * 当月天数 )
     *
     * @param mapParam
     * @return
     */
    private boolean checkParam(Map<String, UserDormitoryParamDto> mapParam) {
        UserDormitoryParamDto dormitoryParamDtoAll = mapParam.get(DORMITORY_All_ROOM);
        UserDormitoryParamDto dormitoryParamDtoA = mapParam.get(DORMITORY_A_ROOM);
        UserDormitoryParamDto dormitoryParamDtoB = mapParam.get(DORMITORY_B_ROOM);
        UserDormitoryParamDto dormitoryParamDtoC = mapParam.get(DORMITORY_C_ROOM);
        if (dormitoryParamDtoAll == null || dormitoryParamDtoA == null || dormitoryParamDtoB == null || dormitoryParamDtoC == null) {
            return false;
        } else if (dormitoryParamDtoAll.getValue() == 0) {
            return false;
        } else if (dormitoryParamDtoA.getValue() == 0) {
            return false;
        } else if (dormitoryParamDtoB.getValue() == 0) {
            return false;
        } else if (dormitoryParamDtoC.getValue() == 0) {
            return false;
        }
        return true;
    }


    /**
     * 查询房间是否有人在住
     *
     * @param ksDormitoryReserveVo
     * @return
     */
    public KsLifeCommonVo checkDormitoryStatus(KsDormitoryReserveVo ksDormitoryReserveVo) {
        String url = "/kshome/house_room_live/checkLive";
        KsLifeCommonVo ksLifeCommonVo = new KsLifeCommonVo();
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        KsHttpComponent component = new KsHttpComponent();
        BaseQueryResponseVo result = new BaseQueryResponseVo();
        Map<String, String> param = new HashMap<String, String>() {{
            put("roomNo", ksDormitoryReserveVo.getRoomNo());
            put("bedroom", ksDormitoryReserveVo.getBedroom());
            put("berth", ksDormitoryReserveVo.getBerth());
            put("expectCheckInDate", ksDormitoryReserveVo.getExpectCheckInDate() + "%2000:00:00");
            put("expectCheckOutDate", ksDormitoryReserveVo.getExpectCheckOutDate() + "15:00:00");
        }};
        String response = component.doGetHttpRequest(address + url, param, loginId, null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) == 0) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            KsLifeDormitCheckVo ksLifeDormitCheckVo = JSON.parseObject(decryptedData, KsLifeDormitCheckVo.class);
            ksLifeCommonVo.setCode(1200);
            ksLifeCommonVo.setMsg(ksLifeDormitCheckVo.getCheckMsg());
        }
        if (ksResult != null && ksResult.getCode() != null && SUCCESS_CODE.compareTo(ksResult.getCode()) != 0) {
            ksLifeCommonVo.setCode(ksResult.getCode());
            ksLifeCommonVo.setMsg(ksResult.getMsg());
        }
        return ksLifeCommonVo;
    }

    @Override
    public KsDormitoryChangeRepVo getDormitoryChangeInfo(Integer pageNo, Integer pageSize, String name) {
        String reqUrl = "/kshome/house_room_live_change_log/list";
        Map<String, String> param = new HashMap<>();
        param.put("pageSize", pageSize.toString());
        param.put("pageNo", pageNo.toString());
        param.put("name", name);

        KsHttpComponent component = new KsHttpComponent();
        String response = component.doGetHttpRequest(address + reqUrl, param, MyTokenUtils.getCurrentUser().getLoginId(), null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);

        if (ksResult == null) {
            throw new UserInvalidOperateException("调用Ks-life连接异常。");
        }
        if (!SUCCESS_CODE.equals(ksResult.getCode())) {
            throw new KsException(ksResult.getCode(), ksResult.getMsg());
        }

        KsDormitoryChangeRepVo result = JSONObject.toJavaObject(JSONObject.parseObject(component.ksDecryptData(ksResult.getData().toString()).toString()), KsDormitoryChangeRepVo.class);

        return result;
    }
}