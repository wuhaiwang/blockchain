package com.seasun.management;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.common.WechatCropHttpComponent;
import com.seasun.management.constant.WechatCropConstant;
import com.seasun.management.dto.WechatCropConcatUserDto;
import com.seasun.management.util.MyHttpAsyncClientUtils;
import com.seasun.management.vo.WechatCropAccessToken;
import com.seasun.management.vo.WechatCropBaseVo;
import com.seasun.management.vo.WechatCropConcatUserVo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class WechatCropHttpComponentTest extends TestCase {

    Logger logger = LoggerFactory.getLogger(WechatCropHttpComponentTest.class);

    @Autowired
    WechatCropHttpComponent wechatCropHttpComponent;


    @Test
    public void testGetAccessToken () throws Exception {

        WechatCropAccessToken accessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();
        logger.info ("获取access_token -> {}", accessToken);

        WechatCropConcatUserVo wechatCropConcatUserVo1 = wechatCropHttpComponent.httpRequestFindWechatCropConcatUserByLoginId("jianghongbin", accessToken);
        logger.info ("获取企业联系人信息", wechatCropConcatUserVo1);

        WechatCropAccessToken accessToken1 = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();
        logger.info ("从缓存中获取accesstoken -> {}", accessToken1);

        WechatCropConcatUserVo wechatCropConcatUserVo2 = wechatCropHttpComponent.httpRequestFindWechatCropConcatUserByLoginId("huangjinhai", accessToken1);
        logger.info ("获取企业联系人信息", wechatCropConcatUserVo2);


    }

    @Test
    public void testFindConcatUser () throws Exception {
        WechatCropAccessToken accessToken = new WechatCropAccessToken();
        accessToken.setAccessToken("5jF9CVuKPzw7iJXRfk6mEylRsFCrJN_h8s7qdheGUqIyrI_EZ31Ys6nxKZ9MZtGK6yQNYkEVF0GM2p4d_2PiVl-jHgQvjnvjgzGUJijZX8TBuVp4mlJQdky8G1wYPfVHOf-HBPOQtD1J-NEVD8Fku3y-PPcPePT7URNtS_P15apXpY4gmYF7gwco1IEqkDakZo7nzae-4KYXOA44i6oMBg");
        WechatCropConcatUserVo vo = wechatCropHttpComponent.httpRequestFindWechatCropConcatUserByLoginId("jianghongbin1", accessToken);
        logger.info ("WechatCropConcatUserVo -> {}", vo);
    }

    @Test
    public void testAddConcatUser () throws Exception {
        WechatCropAccessToken accessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();
        WechatCropConcatUserDto userDto = new WechatCropConcatUserDto();
        userDto.setGender("1");
        userDto.setMobile("13417647832");
        userDto.setUserid("jianghongbin1");
        userDto.setName("江宏斌");
        userDto.setEmail("583920059@qq.com");
        List<Long> departments = new ArrayList<>();
        departments.add (1L);
        userDto.setDepartment(departments);
        userDto.setIsleader(0);
        userDto.setStatus(1);
        WechatCropBaseVo baseVo = wechatCropHttpComponent.httpRequestCreateWechatCropConcatUser(userDto, accessToken);
        logger.info("baseVo -> {}", baseVo);
        assert baseVo.getErrcode() == 0;
    }


    @Test
    public void testDeleteConcatUser () throws Exception{
        WechatCropAccessToken accessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();
        WechatCropBaseVo baseVo = wechatCropHttpComponent.httpRequestDeleteWechatCropConcatUser("jianghongbin1", accessToken);
        logger.info("baseVo -> {}", baseVo);
        assert baseVo.getErrcode() == 0;
    }


    /**
     * 测试 @CachePut 会覆盖 @Cachable , 前提是都在同一个value中
     * 先@Cachable, 进行 @CachePut， 再@Cachable, 期望结果是 @Cachable == @CachePut的值
     * */
    @Test
    public void testPutWillCoverGetCache () throws Exception {
        WechatCropAccessToken wechatCropAccessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();
        logger.debug ("from cache -> {}", wechatCropAccessToken);

        String newAccessToken = wechatCropAccessToken.getAccessToken()+"123";
        wechatCropAccessToken.setAccessToken(newAccessToken);
        wechatCropHttpComponent.putWechatCropAccessTokenIntoCache(wechatCropAccessToken);


        WechatCropAccessToken newCropAccessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();

        logger.debug ("after update from cache -> {}", newCropAccessToken);

        assertEquals( newCropAccessToken.getAccessToken(),newAccessToken);

    }



}
