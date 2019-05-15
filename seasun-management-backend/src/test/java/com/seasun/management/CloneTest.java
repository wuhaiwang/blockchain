package com.seasun.management;

import com.seasun.management.dto.UserDto;
import com.seasun.management.dto.UserDtoMultObject;
import org.junit.Test;

import java.util.Date;
import java.util.Random;

public class CloneTest {

    // 普通对象深拷贝
    @Test
    public void testClone() {
        UserDto dto = new UserDto();
        dto.setFirstName("xht");
        dto.setLoginId("xionghaitao");
        dto.setCreateTime(new Date());
        dto.setLocation("长沙");
        dto.setId(123L);
        UserDto cloned = dto.clone();
        cloned.setLoginId("cloned");

        System.out.println("before cloned loginId is:" + dto.getLoginId());
        System.out.println("after cloned loginId is:" + cloned.getLoginId());
    }

    // 复杂对象深拷贝
    @Test
    public void testMultObjectClone() {
        UserDtoMultObject userDtoMultObject = new UserDtoMultObject();
        userDtoMultObject.setLoginId("熊海涛");
        userDtoMultObject.setCreateTime(new Date());
        userDtoMultObject.setLabel("VP");
        UserDto user = new UserDto();
        user.setLoginId("sublogin");
        user.setLabel("subLabel");
        userDtoMultObject.setUser(user);

        UserDtoMultObject cloned = userDtoMultObject.clone();
        cloned.setLoginId("cloned");
        System.out.println("before cloned loginId is:" + userDtoMultObject.getLoginId());
        System.out.println("after cloned loginId is:" + cloned.getLoginId());

        cloned.getUser().setLoginId("new login id");
        System.out.println("before cloned user.loginId is:" + userDtoMultObject.getUser().getLoginId());
        System.out.println("after cloned loginId is:" + cloned.getUser().getLoginId());
    }

    @Test
    public void aa() {
        Integer[] miliInt = new Integer[10000];
        System.out.println("begin init random" + System.currentTimeMillis());
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            miliInt[i] = random.nextInt(10000) * random.nextInt(50);
        }
        long bengin = System.currentTimeMillis();
        System.out.println("begin init sort" + System.currentTimeMillis());

        // 8秒
        int i1 = miliInt.length - 30;
        for (int i = 0; i < miliInt.length; i++) {
            for (int j = i + 1; j < miliInt.length ; j++) {
                if (miliInt[i] < miliInt[j]) {
                    int x = miliInt[i];

                    miliInt[i] = miliInt[j];
                    miliInt[j] = x;
                }
            }
        }


        //for (int i = 0; i < miliInt.length / 2; i++) {
//            int x = miliInt[i];
//            for (int j = 1; j < miliInt.length - i; j++) {
//                if (x > miliInt[j]) {
//                    miliInt[i] = miliInt[j];
//                    miliInt[j] = x;
//                }
//            }
//
        System.out.println("end sort" + (System.currentTimeMillis() - bengin));

    }
}
