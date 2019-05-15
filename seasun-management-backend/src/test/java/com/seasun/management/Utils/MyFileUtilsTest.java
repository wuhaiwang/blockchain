package com.seasun.management.Utils;

import com.seasun.management.Application;
import com.seasun.management.mapper.UserSalaryChangeMapper;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.util.MyFileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class MyFileUtilsTest {

    @Autowired
    private UserSalaryChangeMapper userSalaryChangeMapper;

    @Test
    public void testCopyFile() {
        String oldPath = "E:/file/tmp/file/test.txt";
        String newPath = "E:/file/tmp/file/test1.txt";
        MyFileUtils.copyFile(oldPath, newPath);
    }


    @Test
    public void batchInsert() {
        List<UserSalaryChange> list = new ArrayList<>();
        long x= 1200;
        long times = 300;
        for (int j = 0; j < 100; j++) {
            for (long i = x; i < x + times; i++) {
                UserSalaryChange userSalaryChange = new UserSalaryChange();
                long id = i;
                long workGroupId = (((long) (new Random().nextDouble() * 10)));
                long subGroup = (((long) (new Random().nextDouble() * 10)));
                long userId = (((long) (new Random().nextDouble() * 10)));
                int year = (((int) (new Random().nextDouble() * 10)));
                int quarter = (((int) (new Random().nextDouble() * 10))) % 4;
                int oldSalary = (((int) (new Random().nextDouble() * 10)));
                int increaseSalary = (((int) (new Random().nextDouble() * 10)));
                int score = (int) i / 4;
                Date lastSalaryChangeDate = new Date();
                Date lastGradeChangeDate = new Date();
                Date createTime = new Date();
                Date updateTime = new Date();
                userSalaryChange.setId(id);
                userSalaryChange.setWorkGroupId(workGroupId);
                userSalaryChange.setSubGroup(subGroup + "");
                userSalaryChange.setUserId(userId);
                userSalaryChange.setYear(year);
                userSalaryChange.setQuarter(quarter);
                userSalaryChange.setOldSalary(oldSalary);
                userSalaryChange.setIncreaseSalary(increaseSalary);
                userSalaryChange.setScore(score);
                userSalaryChange.setLastSalaryChangeDate(lastSalaryChangeDate);
                userSalaryChange.setLastGradeChangeDate(lastGradeChangeDate);
                userSalaryChange.setCreateTime(createTime);
                userSalaryChange.setUpdateTime(updateTime);
                list.add(userSalaryChange);
            }
            userSalaryChangeMapper.batchInsertSelective(list);
            x = x + times;
        }
    }


}
