package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.FmUserRoleDto;
import com.seasun.management.dto.PerformanceUserDto;
import com.seasun.management.dto.PerformanceWorkGroupDto;
import com.seasun.management.dto.UserPerformanceDto;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.UserMessageHelper;
import com.seasun.management.mapper.UserSalaryMapper;
import com.seasun.management.model.*;
import com.seasun.management.service.UserSalaryService;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyTokenUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserSalaryServiceImpl implements UserSalaryService {

    @Autowired
    UserSalaryMapper userSalaryMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserSalaryServiceImpl.class);

    @Override
    public void importUserSalary(MultipartFile file) {

        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(file.getInputStream(), "876222");
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }

        // 读取薪资信息. (模板列信息, 0:userId, 4:salary, 5:last-change-date,6:last-change-amount)
        Sheet sheet = wb.getSheetAt(0);
        List<UserSalary> userSalaryList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            UserSalary usersalary = new UserSalary();
            usersalary.setUserId(Long.valueOf((long) row.getCell(0).getNumericCellValue()));

            // salary encrypt by AES
            try {
                String salaryEnStr = MyEncryptorUtils.encryptByAES(String.valueOf((long) row.getCell(0).getNumericCellValue()));
                usersalary.setSalary(salaryEnStr);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            usersalary.setLastSalaryChangeDate(MyCellUtils.getCellValue(row.getCell(5)));
            usersalary.setLastSalaryChangeAmount(String.valueOf((int) row.getCell(6).getNumericCellValue()));
            usersalary.setCreateTime(new Date());
            userSalaryList.add(usersalary);
        }

        // 不存历史,全表清空,并刷新
        if (userSalaryList.size() > 0) {
            userSalaryMapper.deleteAll();
            userSalaryMapper.batchInsert(userSalaryList);
        } else {
            logger.info("no valid salary data found,will return...");
        }

    }

    @Override
    public UserSalary getUserSalaryByUserId(Long userId) {
        UserSalary result = null;

        // here, result is cached, do not care about db performance.
        List<UserSalary> salaryList = userSalaryMapper.selectAll();

        for (UserSalary userSalary : salaryList) {
            if (userId.equals(userSalary.getUserId())) {
                result = userSalary;
                break;
            }
        }
        return result;
    }
}
