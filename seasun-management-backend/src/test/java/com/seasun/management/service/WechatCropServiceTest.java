package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.common.WechatCropHttpComponent;
import com.seasun.management.vo.RequestUserVo;
import com.seasun.management.vo.WechatCropAccessToken;
import com.seasun.management.vo.WechatCropBaseVo;
import com.seasun.management.vo.WechatCropOperationResultVo;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class WechatCropServiceTest extends TestCase {

    @Autowired
    WechatCropService wechatCropService;

    @Autowired
    WechatCropHttpComponent wechatCropHttpComponent;

    Logger logger = LoggerFactory.getLogger(WechatCropServiceTest.class);

    /**
     * 正常的流程 -> 单个用户并且存在于数据库, 类型为 login_id
     * */
    @Test
    public void testUpdateUserByNormal () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");
        requestUserVoList.add (vo);
        String type = "loginId";
        WechatCropOperationResultVo wechatCropOperationResultVo =  wechatCropService.updateUsers(requestUserVoList, type);
        logger.info ("list -> {}", wechatCropOperationResultVo.getResultSet());

    }

    /**
     * 正常的流程 -> 多个个用户并且存均在于数据库, 类型为 login_id
     * */
    @Test
    public void testUpdateUsersByNormal () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");

        RequestUserVo vo1 = new RequestUserVo();
        vo1.setLoginId("jianghongbin");

        requestUserVoList.add (vo);
        requestUserVoList.add (vo1);
        String type = "loginId";
        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        logger.info ("list -> {}", wechatCropOperationResultVo.getResultSet());

        assertEquals(2, wechatCropOperationResultVo.getResultSet().size());
        wechatCropOperationResultVo.getResultSet().stream().forEach(item-> {
            item.stream().forEach(it->{
                assertEquals (0L, it.getErrcode().longValue());
            });
        });

    }

    /**
     *
     * 测试多个用户并且均存在db中, 第一次读取token, 流程应该成功, 接下来我将token修改模拟过期行为, 程序是否会自动获取token并更新到缓存中, 期望结果是能成功完成操作
     * */
    @Test
    public void testUpdateUsersByTokenExpire () throws Exception {
        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");
        requestUserVoList.add (vo);
        String type = "loginId";
        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        wechatCropOperationResultVo.getResultSet().stream().forEach(item-> {
            item.stream().forEach(it->{
                assertEquals (0L, it.getErrcode().longValue());
            });
        });


        /**
         * 外部修改 token
         * */
        WechatCropAccessToken wechatCropAccessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();

        logger.debug ("old -> {}", wechatCropAccessToken.getAccessToken());

        wechatCropAccessToken.setAccessToken("123");

        wechatCropHttpComponent.putWechatCropAccessTokenIntoCache(wechatCropAccessToken);

        WechatCropAccessToken wechatCropAccessToken1 = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();

        logger.debug ("new -> {}", wechatCropAccessToken1.getAccessToken());


        assertEquals(wechatCropAccessToken1.getAccessToken(),wechatCropAccessToken.getAccessToken());


         wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        wechatCropOperationResultVo.getResultSet().stream().forEach(item-> {
            item.stream().forEach(it->{
                assertEquals (0L, it.getErrcode().longValue());
            });
        });



        wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        wechatCropOperationResultVo.getResultSet().stream().forEach(item-> {
            item.stream().forEach(it->{
                assertEquals (0L, it.getErrcode().longValue());
            });
        });
    }


    /**
     *
     * 这种情况能真正测出access_token过期
     * */
    @Test
    public void testOnceAHour () throws InterruptedException {

        Long OneHour = 60*60*1000L;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<RequestUserVo> requestUserVoList = new ArrayList<>();
                        RequestUserVo vo = new RequestUserVo();
                        vo.setLoginId("jianghongbin1");
                        requestUserVoList.add (vo);
                        String type = "loginId";
                        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

                        wechatCropOperationResultVo.getResultSet().stream().forEach(item-> {
                            item.stream().forEach(it->{
                                assertEquals (0L, it.getErrcode().longValue());
                            });
                        });

                        Thread.sleep(OneHour);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }).start();


        CountDownLatch countDownLatch = new CountDownLatch(1);

        countDownLatch.await();

        System.exit(0);

    }


    /**
     * 测试login_id没有找到的情况
     *
     * 输入： jianghongbin1， jianghongbin2
     * 期望输出:
     * */
    @Test
    public void testLoginIdNoFound () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");
        requestUserVoList.add (vo);

        RequestUserVo vo2 = new RequestUserVo();
        vo2.setLoginId("jianghongbin2");

        RequestUserVo vo3 = new RequestUserVo();
        vo3.setLoginId("");

        RequestUserVo vo4 = new RequestUserVo();

        requestUserVoList.add (vo2);
        requestUserVoList.add (vo3);
        requestUserVoList.add (vo4);


        String type = "loginId";
        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        logger.debug(" wechatCropOperationResultVo -> {}", wechatCropOperationResultVo);

        List<List<WechatCropBaseVo.WechatCropOperationVo>> res = wechatCropOperationResultVo.getResultSet();

        List<WechatCropBaseVo.WechatCropOperationVo> res1= wechatCropOperationResultVo.getUserNotFoundSet();

        res.get(0).stream().forEach(item-> {
            assertEquals(0l, item.getErrcode().longValue());
        });

        res1.stream().forEach(item->{
            assertEquals(-1L, item.getErrcode().longValue());
            assertEquals("loginId not found", item.getErrmsg());
        });


        assertEquals(3, res1.size());

        assertEquals(1, res.size());
    }

    /**
     *
     * 测试输入员工号
     * employee_no = 20160002
     * expected : [{transactionId='20160002', errcode=0, errmsg='deleted'}, WechatCropOperationVo{transactionId='20160002', errcode=0, errmsg='created'}]]
     * */
    @Test
    public void testEmployeeNo () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setEmployeeNo(20160002L);
        requestUserVoList.add (vo);
        String type = "employeeNo";
        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());
        assertEquals(0, wechatCropOperationResultVo.getUserNotFoundSet().size());

        wechatCropOperationResultVo.getResultSet().get(0).stream().forEach(item->{
            assertEquals(0L, item.getErrcode().longValue());
        });

    }

    /**
     *
     * 测试输入员工号
     * employee_no = [20160002, 20160003, null, -1]
     * expected : [{transactionId='20160002', errcode=0, errmsg='deleted'}, WechatCropOperationVo{transactionId='20160002', errcode=0, errmsg='created'}], {transactionId='29328', errcode=0, errmsg='deleted'}, WechatCropOperationVo{transactionId='29328', errcode=0, errmsg='created'}] ]
     *            userNotFoundSet size = 3
     * */
    @Test
    public void testEmployeeNosByNotFound () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setEmployeeNo(20160002L);

        RequestUserVo vo2 = new RequestUserVo();
        vo2.setEmployeeNo(20160003L);

        RequestUserVo vo3 = new RequestUserVo();
        vo3.setEmployeeNo(null);

        RequestUserVo vo4 = new RequestUserVo();
        vo4.setEmployeeNo(-1l);
        requestUserVoList.add (vo);
        //requestUserVoList.add (vo1);
        requestUserVoList.add (vo2);
        requestUserVoList.add (vo3);
        requestUserVoList.add (vo4);



        String type = "employeeNo";
        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());
        assertEquals(3, wechatCropOperationResultVo.getUserNotFoundSet().size());

        wechatCropOperationResultVo.getResultSet().get(0).stream().forEach(item->{
            assertEquals(0L, item.getErrcode().longValue());
        });

    }


    /**
     *
     * 测试输入员工号
     * employee_no = [20160002]
     * expected : [{transactionId='20160002', errcode=0, errmsg='deleted'}]
     *
     * */
    @Test
    public void testEmployeeNoAndPhone () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setPhone("13640374926");
        vo.setEmployeeNo(20160002L);
        requestUserVoList.add (vo);
        String type = "employeeNo";



        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());

        wechatCropOperationResultVo.getResultSet().get(0).stream().forEach(item->{
            assertEquals(0L, item.getErrcode().longValue());
        });

    }


    /**
     *
     * 测试输入存在的员工姓名和手机号码(唯一)
     * 输入：江宏斌 13417647832
     * 期望输出 [{transactionId='江宏斌13417647832', errcode=0, errmsg='deleted'}]
     * */
    @Test
    public void testPhoneAndName () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setPhone("13417647832");
        vo.setName("江宏斌");
        requestUserVoList.add (vo);
        String type = "phoneAndUserName";


        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());

        wechatCropOperationResultVo.getResultSet().get(0).stream().forEach(item->{
            assertEquals(0L, item.getErrcode().longValue());
        });

    }


    /**
     * 测试 输入正确姓名和错误的手机号
     * 输入: 江宏斌 13417647833
     * 期望: userNotFound.size = 1
     * */
    @Test
    public void testPhoneAndNameButPhoneWrong () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setPhone("13417647833");
        vo.setName("江宏斌");
        requestUserVoList.add (vo);
        String type = "phoneAndUserName";


        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(0, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());
        logger.info ("{}", wechatCropOperationResultVo);

    }

    /**
     * 测试 输入正确姓名和空的手机号
     * 输入: 江宏斌 null
     * 期望: exception
     * */
    @Test
    public void testPhoneAndNameButPhoneNull () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setName("江宏斌");
        requestUserVoList.add (vo);
        String type = "phoneAndUserName";


        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(0, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());
        logger.info ("{}", wechatCropOperationResultVo);

    }


    /**
     * 测试 输入正确姓名和空字符串的手机号
     * 输入: 江宏斌 ""
     * 期望: exception
     * */
    @Test
    public void testPhoneAndNameButPhoneEmpty () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setPhone("");
        vo.setName("江宏斌");
        requestUserVoList.add (vo);
        String type = "phoneAndUserName";


        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(0, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());
        logger.info ("{}", wechatCropOperationResultVo);

    }

    /**
     * 测试 输入正确姓名和非空字符串的手机号
     * 输入: 江宏斌 "    "
     * 期望: exception
     * */
    @Test
    public void testPhoneAndNameButPhoneTrimEmpty () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setPhone("    ");
        vo.setName("江宏斌");
        requestUserVoList.add (vo);
        String type = "phoneAndUserName";


        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);
        assertEquals(0, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());
        logger.info ("{}", wechatCropOperationResultVo);

    }


    /**
     * 测试 输入含有正确名字的手机,正确的名字和错误的手机
     * 输入: 江宏斌 "13417647832" 黄进海 "13417647833"
     * 期望: resultSet size 1 userNotFoundSet size 0
     * */
    @Test
    public void testPhoneAndNames () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setPhone("13417647832");
        vo.setName("江宏斌");

        RequestUserVo vo1 = new RequestUserVo();
        vo1.setPhone("13417647833");
        vo1.setName("黄进海");

        requestUserVoList.add (vo);
        requestUserVoList.add (vo1);
        String type = "phoneAndUserName";


        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        logger.info ("{}", wechatCropOperationResultVo);

        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());


    }


    /**
     * 错误的类型
     * 输入: login_id
     * 期望 exception
     * */
    @Test
    public void wrongType () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");
        requestUserVoList.add (vo);
        String type = "login_id";

        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        logger.info ("{}", wechatCropOperationResultVo);

        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());


    }

    /**
     * 错误的类型
     * 输入: null
     * 期望 exception
     * */
    @Test
    public void nullType () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");
        requestUserVoList.add (vo);
        String type = null;

        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        logger.info ("{}", wechatCropOperationResultVo);

        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());


    }


    /**
     * 错误的类型
     * 输入: ""
     * 期望 exception
     * */
    @Test
    public void emptyType () throws Exception {

        List<RequestUserVo> requestUserVoList = new ArrayList<>();
        RequestUserVo vo = new RequestUserVo();
        vo.setLoginId("jianghongbin1");
        requestUserVoList.add (vo);
        String type = "  ";

        WechatCropOperationResultVo wechatCropOperationResultVo = wechatCropService.updateUsers(requestUserVoList, type);

        logger.info ("{}", wechatCropOperationResultVo);

        assertEquals(1, wechatCropOperationResultVo.getResultSet().size());

        assertEquals(1, wechatCropOperationResultVo.getUserNotFoundSet().size());


    }

}
