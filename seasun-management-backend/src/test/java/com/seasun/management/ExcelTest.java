package com.seasun.management;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.FnPlatShareConfigUserDTO;
import com.seasun.management.dto.UserDto;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.FnPlatShareConfigService;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.vo.CfgPlatAttrVo;
import com.seasun.management.vo.UserMiniVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class ExcelTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserWorkExperienceMapper userWorkExperienceMapper;

    @Autowired
    private UserDormitoryReservationMapper userDormitoryReservationMapper;

    @Autowired
    private CfgPlatAttrMapper cfgPlatAttrMapper;

    @Autowired
    private FnWeekShareWorkdayStatusMapper fnWeekShareWorkdayStatusMapper;

    @Autowired
    private FnPlatWeekShareConfigMapper fnPlatWeekShareConfigMapper;

    @Autowired
    private FnSumWeekShareConfigMapper fnSumWeekShareConfigMapper;

    @Autowired
    @Qualifier("fnPlatShareConfigService")
    private FnPlatShareConfigService fnPlatShareConfigService;

    private List<String> exportUserWorkExperienceFields = new ArrayList<String>() {{
        add("BeginDate");
        add("EndDate");
        add("City");
        add("Company");
        add("Department");
        add("Post");
        add("Responsibility");
        add("LeaveReason");
    }};

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void getAllSheetName() {
        File file = new File("E://test.xlsx");

        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(file);
            int sheetSize = wb.getNumberOfSheets();
            List<String> sheetNameList = new ArrayList<>();
            for (int i = 0; i < sheetSize; i++) {
                sheetNameList.add(wb.getSheetAt(i).getSheetName());
            }
            ExcelHelper.saveErrorFile(sheetNameList, "sheetNames/", "");
        } catch (Exception e) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }
    }

    @Test
    // 导出女员工工号信息
    public void testCreateWomanEmployeeNo() throws Exception {
        CreateWomanEmployeeNoExcel(UserDetail.WorkStatus.cadet);
        CreateWomanEmployeeNoExcel(UserDetail.WorkStatus.regular);
    }

    private void CreateWomanEmployeeNoExcel(String workStatus) throws Exception {
        List<User> cadetUsers = userMapper.selectAllWomanByWorkStatus(workStatus);
        Workbook cadetWb = new HSSFWorkbook();
        Sheet sheet = cadetWb.createSheet();
        for (User user : cadetUsers) {
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell userEmpCell = row.createCell(row.getPhysicalNumberOfCells());
            userEmpCell.setCellValue(user.getEmployeeNo());
            Cell userNameCell = row.createCell(row.getPhysicalNumberOfCells());
            userNameCell.setCellValue(user.getName());
        }
        cadetWb.write(new FileOutputStream(new File("D:/" + workStatus + ".xls")));
        cadetWb.close();
    }

    @Test
    // 按地区导出员工工作经历
    public void generateUserWorkExperienceFileByCity() throws Exception {
        String city = "广州";
        String fileSavePath = "D:/" + city + "员工工作经历.xls";
        List<UserDto> users = userMapper.selectByCity(city);
        List<UserWorkExperience> userWorkExperiences = userWorkExperienceMapper.selectByCity(city);
        Map<Long, List<UserWorkExperience>> userWorkExperiencesByUserIdMap = userWorkExperiences.stream().filter(x -> null != x.getUserId()).collect(Collectors.groupingBy(x -> x.getUserId()));
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row firstRow = sheet.createRow(0);
        int cellIndex = 0;
        Cell employeeNoCell = firstRow.createCell(cellIndex++);
        employeeNoCell.setCellValue("工号");
        Cell nameCell = firstRow.createCell(cellIndex++);
        nameCell.setCellValue("姓名");
        Cell workNameCell = firstRow.createCell(cellIndex++);
        workNameCell.setCellValue("工作组");
        Cell workStatusCell = firstRow.createCell(cellIndex++);
        workStatusCell.setCellValue("工作状态");
        Cell beginCell = firstRow.createCell(cellIndex++);
        beginCell.setCellValue("开始时间");
        Cell endCell = firstRow.createCell(cellIndex++);
        endCell.setCellValue("结束时间");
        Cell cityCell = firstRow.createCell(cellIndex++);
        cityCell.setCellValue("所在城市");
        Cell companyCell = firstRow.createCell(cellIndex++);
        companyCell.setCellValue("公司");
        Cell depCell = firstRow.createCell(cellIndex++);
        depCell.setCellValue("部门");
        Cell postCell = firstRow.createCell(cellIndex++);
        postCell.setCellValue("岗位");
        Cell sss = firstRow.createCell(cellIndex++);
        sss.setCellValue("职责");
        Cell leaveCell = firstRow.createCell(cellIndex++);
        leaveCell.setCellValue("离职原因");

        for (UserDto user : users) {
            if (user == null) {
                continue;
            }
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());

            Cell c1 = row.createCell(0);
            if (user.getEmployeeNo() != null) {
                c1.setCellValue(user.getEmployeeNo());
            }
            Cell c2 = row.createCell(1);
            if (user.getName() != null) {
                c2.setCellValue(user.getName());
            }
            Cell c3 = row.createCell(2);
            if (user.getWorkGroupName() != null) {
                c3.setCellValue(user.getWorkGroupName());
            }
            Cell c4 = row.createCell(3);
            if (user.getWorkStatus() != null) {
                c4.setCellValue(user.getWorkStatus());
            }
            if (userWorkExperiencesByUserIdMap.containsKey(user.getId())) {
                List<UserWorkExperience> userWorkExperiences1 = userWorkExperiencesByUserIdMap.get(user.getId());
                Collections.sort(userWorkExperiences1, new Comparator<UserWorkExperience>() {
                    @Override
                    public int compare(UserWorkExperience o1, UserWorkExperience o2) {
                        return o1.getBeginDate().compareTo(o2.getBeginDate());
                    }
                });
                generateUserWorkExperiences(row, userWorkExperiences1.get(0));
                if (userWorkExperiences1.size() > 1) {
                    for (int i = 1; i < userWorkExperiences1.size(); i++) {
                        Row nextRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                        generateUserWorkExperiences(nextRow, userWorkExperiences1.get(i));
                    }
                }
            }
        }
        wb.write(new FileOutputStream(new File(fileSavePath)));
        wb.close();
    }

    private void generateUserWorkExperiences(Row row, UserWorkExperience userWorkExperience)
            throws Exception {
        int index = 4;
        for (String field : exportUserWorkExperienceFields) {
            Cell cell = row.createCell(index++);
            Method method = UserWorkExperience.class.getMethod("get" + field);
            Object invoke = method.invoke(userWorkExperience);
            if (invoke != null) {
                if (field.endsWith("Date")) {
                    cell.setCellValue(simpleDateFormat.format((Date) invoke));
                } else {
                    cell.setCellValue(invoke.toString());
                }
            }
        }
    }

    @Test
    public void updateUserDormitoryReservations() throws Exception {
        List<UserDormitoryReservation> userDormitoryReservations = userDormitoryReservationMapper.selectAll();
        Map<String, UserMiniVo> userMiniVosByUserNameMap = userMapper.selectAllUserMiniVo().stream().filter(x -> x.getIdCode() != null)
                .collect(Collectors.toMap(x -> x.getIdCode(), x -> x, (x, y) -> y));
        for (UserDormitoryReservation userDormitoryReservation : userDormitoryReservations) {
            userDormitoryReservation.setIdCode(MyEncryptorUtils.encryptByAES(userDormitoryReservation.getIdCode()));
            userDormitoryReservation.setMobile(MyEncryptorUtils.encryptByAES(userDormitoryReservation.getMobile()));
            if (userMiniVosByUserNameMap.containsKey(userDormitoryReservation.getIdCode())) {
                UserMiniVo userMiniVo = userMiniVosByUserNameMap.get(userDormitoryReservation.getIdCode());
                userDormitoryReservation.setUserId(userMiniVo.getId());
                userDormitoryReservation.setProjectId(userMiniVo.getProjectId());
                userDormitoryReservation.setCompanyId(userMiniVo.getCompanyId());
            }
        }
        userDormitoryReservationMapper.deleteAll();
        userDormitoryReservationMapper.batchInsert(userDormitoryReservations);
    }

    @Test
    // 帮助以前员工住宿时未登记信息的情况，为其他部门回填当时入住人的信息
    public void generateUserDormitoryInfo() throws Exception {
        // 导入文件路径
        String inputFilePath = "D:aa.xls";
        // 导出文件路径
        String outputExcelFilePath = "D:bb.xls";
        // 错误提示文件路径
        String outputErrorTxtFilePath = "D:error.txt";

        PrintWriter pw = new PrintWriter(outputErrorTxtFilePath);
        Workbook wb = WorkbookFactory.create(new FileInputStream(new File(inputFilePath)));
        Map<String, List<UserMiniVo>> userMiniVosByUserNameMap = userMapper.selectAllUserMiniVo().stream().collect(Collectors.groupingBy(x -> x.getName()));
        // 数据库中没有记录
        List<String> noUserList = new ArrayList<>();
        // 数据库中有记录但是没有身份证号码
        List<String> noIdCardUserList = new ArrayList<>();
        // 数据库中有记录但是没有电话号码
        List<String> noPhoneUserList = new ArrayList<>();
        // 数据库中重名
        List<String> repetitionNameList = new ArrayList<>();

        Sheet sheetAt = wb.getSheetAt(0);
        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
        for (int i = 0; i < physicalNumberOfRows; i++) {
            Row currentRow = sheetAt.getRow(i);
            Cell userNameCell = currentRow.getCell(6);
            if (userNameCell == null || userNameCell.getStringCellValue().isEmpty()) {
                continue;
            }
            String userNmae = StringUtils.trim(userNameCell.getStringCellValue());

            if (!userMiniVosByUserNameMap.containsKey(userNmae)) {
                noUserList.add("第 " + (i + 1) + " 行，用户名: " + userNmae + " 没有在系统中找到该用户。\r\n");
                continue;
            }
            if (userMiniVosByUserNameMap.get(userNmae).size() > 1) {
                repetitionNameList.add("第 " + (i + 1) + " 行，用户名: " + userNmae + " 有重名情况，无法在系统中为其匹配手机号和身份证号码。\r\n");
                continue;
            }

            UserMiniVo userMiniVo = userMiniVosByUserNameMap.get(userNmae).get(0);

            // 回填性别
            Cell genderCell = currentRow.getCell(7);
            if (genderCell == null) {
                genderCell = currentRow.createCell(7);
            }
            if (MyCellUtils.getCellValue(genderCell).isEmpty()) {
                if (userMiniVo.getGender() != null) {
                    genderCell.setCellValue(userMiniVo.getGender() == 0 ? "男" : "女");
                }
            }

            // 回填手机号
            Cell phoneCell = currentRow.getCell(9);
            if (phoneCell == null) {
                phoneCell = currentRow.createCell(9);
            }
            if (MyCellUtils.getCellValue(phoneCell).isEmpty()) {
                if (userMiniVo.getMobile() != null) {
                    phoneCell.setCellValue(MyEncryptorUtils.decryptByAES(userMiniVo.getMobile()));
                } else {
                    noPhoneUserList.add("第 " + (i + 1) + " 行，用户名: " + userNmae + " 在数据库中有记录，但是没有电话号码。\r\n");
                }
            }

            // 回填身份证
            Cell idCardCell = currentRow.getCell(10);
            if (idCardCell == null) {
                idCardCell = currentRow.createCell(10);
            }
            if (MyCellUtils.getCellValue(idCardCell).isEmpty()) {
                if (userMiniVo.getIdCode() != null) {
                    if (!"身份证".equals(userMiniVo.getCertificateType())) {
                        if (userMiniVo.getCertificateType() == null) {
                            idCardCell.setCellValue(MyEncryptorUtils.decryptByAES(userMiniVo.getIdCode()) + "(不明证件)");
                        }
                        idCardCell.setCellValue(MyEncryptorUtils.decryptByAES(userMiniVo.getIdCode()) + "(" + userMiniVo.getCertificateType() + ")");
                    } else {
                        idCardCell.setCellValue(MyEncryptorUtils.decryptByAES(userMiniVo.getIdCode()));
                    }
                } else {
                    noIdCardUserList.add("第 " + (i + 1) + " 行，用户名: " + userNmae + " 在数据库中有记录，但是没有身份证号码。\r\n");
                }
            }
        }
        noUserList.addAll(noIdCardUserList);
        noUserList.addAll(noPhoneUserList);
        noUserList.addAll(repetitionNameList);
        if (!noUserList.isEmpty()) {
            for (String s : noUserList) {
                pw.write(s);
            }
        }

        wb.write(new FileOutputStream(new File(outputExcelFilePath)));
        pw.flush();
        pw.close();
    }

    @Test
    public void exportSeasunWorldGameShareConfig() throws Exception {
        List<CfgPlatAttrVo> cfgPlatAttrVos = cfgPlatAttrMapper.selectAllWithPlatName();
        List<Long> platIds = cfgPlatAttrVos.stream().filter(x -> x.getPlatName().contains("世游") && !Project.Id.BEIJING_WORLDGAME_TECGNOLOGY_CENTER.equals(x.getPlatId())).map(x -> x.getPlatId()).collect(Collectors.toList());
        fnPlatShareConfigService.exportPlatMonthShareConfig(2018, 3, platIds, "D:世游.xls");
    }

    @Test
    // 导出技术中心下面平台的周分摊数据
    public void exportZhuhaiTechnologyCenterShareConfig() throws Exception {
        int year = 2018;
        int week = 17;
        long platId = 5045;
        List<Long> selectCond = new ArrayList<>(2);
        selectCond.add(platId);
        List<Long> projectIds = projectMapper.selectByIdsOrParentHrIds(selectCond).stream().map(x -> x.getId()).collect(Collectors.toList());
        if (!projectIds.isEmpty()) {

            List<FnPlatShareConfigUserDTO> configUserDTOS = fnPlatWeekShareConfigMapper.selectConfigUserDTOByPlatIdsAndYearAndMonth(projectIds, year, week);
            FnWeekShareWorkdayStatus workdayStatus = fnWeekShareWorkdayStatusMapper.selectByYearAndWeek(year, week);

            if (workdayStatus == null || new BigDecimal(0).compareTo(workdayStatus.getWorkday()) == 0) {
                throw new UserInvalidOperateException("本周未开启周分摊或者本周工作日为0，无法导出。");
            }
            List<Long> userIds = userMapper.selectActiveUserIdsByPlatIds(projectIds);
            Map<Long, String> remarkByUserIdMap = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserIds(year, week, userIds).stream().collect(Collectors.toMap(x -> x.getUserId(), y -> y.getRemark(), (x, y) -> y));

            String workdayPeriod = workdayStatus.getMonth() + "月" + workdayStatus.getDay() + "日-" + workdayStatus.getEndMonth() + "月" + workdayStatus.getEndDay() + "日，" + workdayStatus.getYear() + "年第" + workdayStatus.getWeek() + "周";

            fnPlatShareConfigService.exportPlatShareData(platId, configUserDTOS, year + "年" + week + "周人力统计.xls", "本周工作日:", remarkByUserIdMap, workdayStatus.getWorkday(), workdayPeriod);
        }
    }
    @Test
    // 导出技术中心下面平台的周分摊数据
    public void exportWeekly() throws Exception {
        fnPlatShareConfigService.exportWeekly(2018, 16, 2018, 17);
    }
}
