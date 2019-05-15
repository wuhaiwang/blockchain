package com.seasun.management;

import com.alibaba.fastjson.JSON;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.model.FnTask;
import com.seasun.management.model.User;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyListUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UtilTest {

    private static final Logger logger = LoggerFactory.getLogger(UtilTest.class);

    @Test
    public void testAESencrypt() {
        String result = "";
        try {
            result = MyEncryptorUtils.decryptByAES("kSffdpNwSNgSiNVMe4sposJVNmiozX81hjCPZDbeBUY=");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("after encrypt:{}", result);
    }

    @Test
    public void testStringTrim() {
        String out = ExcelHelper.trimSpaceAndSpecialSymbol("斗战西游（原:GC项目）");
        logger.debug("out string is:" + out);
    }

    @Test
    public void testJsonDateFormat() {
        FnTask task = new FnTask();
        task.setResultMessage("hello");
        task.setCreateTime(new Date());
        String result = JSON.toJSONStringWithDateFormat(task, "yyyy-MM-dd kk:mm:ss");
        logger.debug(result);
    }

    @Test
    public void testCloneUser() {
        User user1 = new User();
        user1.setFirstName("x1");
        user1.setLastName("h1");
        User user2 = new User();
        user2.setFirstName("x2");
        user2.setLastName("h3");
        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);

        List<User> clonedAllUsers = new ArrayList<>(allUsers);

        try {
            clonedAllUsers = (List<User>) MyListUtils.deepClone(allUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clonedAllUsers.get(0).setFirstName("YYYY");

        logger.debug(allUsers.get(0).getFirstName());
        logger.debug(clonedAllUsers.get(0).getFirstName());
    }


    @Test
    public void testDecry() throws Exception {
        String result = MyEncryptorUtils.decryptByAES("BRreumTQ6PckSAruiULOQQ==");
        System.out.println(result);
    }

    @Test
    public void testPw() {
        try {
            String result = MyEncryptorUtils.encryptByAES("123456");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
