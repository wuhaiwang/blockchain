package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.dto.UserMiniDto;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserDetail;
import com.seasun.management.service.app.ProjectReportService;
import com.seasun.management.util.MyEncryptorUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(FloorServiceTest.class);

    @Autowired
    UserService userService;

    @Autowired
    ProjectReportService projectReportService;

    @Autowired
    UserMapper userMapper;

    @Test
    public void testUpdateUserPhoto() {
        userService.updateUserPhoto();
    }

    @Test
    public void testProjectSer() {

        List<User> users = userMapper.selectAll();
        List<UserMiniDto> userMiniDtos = new ArrayList<>(users.size());

        for (User user : users) {
            if (user.getLoginId() != null && user.getPhone() != null) {
                if (user.getLoginId().equals("shiyijie")){
                    int a = 1;
                }
                UserMiniDto userPhotoDto = new UserMiniDto();
                userPhotoDto.setLoginId(user.getLoginId());
                try {
                    userPhotoDto.setPhone(MyEncryptorUtils.decryptByAES(user.getPhone()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                userPhotoDto.setPhoto(userPhotoDto.getPhoto());
                userMiniDtos.add(userPhotoDto);

            }
        }
        userMapper.batchUpdateUserPhone(userMiniDtos);
    }
}
