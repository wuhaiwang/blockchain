package com.seasun.management.service.impl;

import com.seasun.management.dto.BaseTotalDto;
import com.seasun.management.dto.UserPsychologicalConsultationDto;
import com.seasun.management.mapper.UserPsychologicalConsultationMapper;
import com.seasun.management.model.UserPsychologicalConsultation;
import com.seasun.management.service.PsychologicalConsultantService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.UserPsychologicalConsultationVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Service
public class PsychologicalConsultantServiceImpl implements PsychologicalConsultantService {

    @Autowired
    private UserPsychologicalConsultationMapper userPsychologicalConsultationMapper;

    private Integer currentPage = 1;

    private Integer pageSize = 20;

    @Value("${file.sys.prefix}")
    private String fileSystemDiskPath;

    @Value("${export.excel.path}")
    private String fileExportDiskPath;

    @Override
    public UserPsychologicalConsultation getUserPsychologicalConsultationPassword(Integer year, Integer month, Long userId) {
        UserPsychologicalConsultation result = new UserPsychologicalConsultation();
        if (userId == null) {
            userId = MyTokenUtils.getCurrentUserId();
        }
        if (year == null && month == null) {
            Calendar nowDate = Calendar.getInstance();
            year = nowDate.get(Calendar.YEAR);
            month = nowDate.get(Calendar.MONTH) + 1;
        }
        Integer password = userPsychologicalConsultationMapper.selectPasswordByConditon(userId, year, month);
        result.setPassword(password);
        return result;
    }

    @Override
    public String exportPsychologicalConsultantPassword(Integer year, Integer month) {
        if (year == null && month == null) {
            Calendar nowDate = Calendar.getInstance();
            year = nowDate.get(Calendar.YEAR);
            month = nowDate.get(Calendar.MONTH) + 1;
        }

        String fileExprotPath = fileExportDiskPath + File.separator + year + "年" + month + "月西山居心理咨询密码.xls";

        File file = new File(fileSystemDiskPath + fileExprotPath);
        if (file.exists()) {
            return fileExprotPath;
        }

        List<UserPsychologicalConsultation> userInfos = userPsychologicalConsultationMapper.selectExportConditonByYearAndMonth(year, month);
        FileOutputStream fos = null;
        Workbook wb = null;
        if (userInfos.size() > 0) {
            try {
                wb = new HSSFWorkbook();
                fos = new FileOutputStream(fileSystemDiskPath + fileExprotPath);
                Sheet sheet = wb.createSheet();
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("时间");
                row.createCell(1).setCellValue("工号");
                row.createCell(2).setCellValue("密码");
                int currentRowNumber = 1;
                for (UserPsychologicalConsultation user : userInfos) {
                    Row currentRow = sheet.createRow(currentRowNumber);
                    currentRow.createCell(0).setCellValue(user.getYear() + "年" + user.getMonth() + "月");
                    currentRow.createCell(1).setCellValue(user.getEmployeeNo());
                    currentRow.createCell(2).setCellValue(user.getPassword());
                    currentRowNumber++;
                }
                //设置列自动调整宽度
                sheet.autoSizeColumn(0, true);
                sheet.autoSizeColumn(1, true);
                sheet.autoSizeColumn(2, true);
                wb.write(fos);
                return fileExprotPath;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (wb != null) {
                        wb.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public UserPsychologicalConsultationVo getUserPsychologicalConsultationPasswordPage(Integer pageSize, Integer currentPage, String searchKey) {
        UserPsychologicalConsultationVo result = new UserPsychologicalConsultationVo();
        BaseTotalDto yearMonthDto;
        if (pageSize == null) {
            pageSize = this.pageSize;
        }

        if (currentPage == null) {
            currentPage = this.currentPage;
        }

        if (searchKey == null) {
            searchKey = "";
        }

        Integer beginNumber = (currentPage - 1) * pageSize;

        if (searchKey == "") {
            yearMonthDto = userPsychologicalConsultationMapper.selectLastCreateYearAndMonthAndCount();
        } else {
            yearMonthDto = userPsychologicalConsultationMapper.selectLastCreateYearAndMonthAndCountBySearchKey(searchKey);
        }

        if (yearMonthDto != null) {
            List<UserPsychologicalConsultationDto> users = userPsychologicalConsultationMapper.selectPageBySearchCondition(yearMonthDto.getYear(), yearMonthDto.getMonth(), beginNumber, pageSize, searchKey);
            result.setTotalCount(yearMonthDto.getTotalCount());
            result.setUsers(users);
            result.setLastModifyTime(yearMonthDto.getYear() + "-" + yearMonthDto.getMonth());
        } else {
            return null;
        }
        return result;
    }
}
