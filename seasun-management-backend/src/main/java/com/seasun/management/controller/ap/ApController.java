package com.seasun.management.controller.ap;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.ApDanmaku;
import com.seasun.management.model.ApDraw;
import com.seasun.management.service.ApService;
import com.seasun.management.util.MyTokenUtils;

@RestController
@RequestMapping
public class ApController {

    private Logger LOGGER = LoggerFactory.getLogger(ApController.class);
    
    private static final String HMAC_SHA256_KEY = "seasun-ap-2018";
    private static Mac sha256_HMAC = null;
    
	static {
		try {
			sha256_HMAC = Mac.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
    
    @Autowired
    private ApService apService;

    //年会弹幕
    @RequestMapping(value = {"/apis/auth/app/ap/danmaku"}, method = RequestMethod.POST)
    public ResponseEntity<?> handleDanmaku(@RequestBody JSONObject jsonObject) {
        String content = jsonObject.getString("content");
        Long userId = MyTokenUtils.getCurrentUserId();
        LOGGER.info("enter ApDanmakuController.handleDanmaku userId: {},content :{}", userId, content);
        apService.handleApDanmaku(userId, content);
        LOGGER.info("leave ApDanmakuController.handleDanmaku userId: {},content :{}", userId, content);
        return ResponseEntity.ok(new CommonResponse(0, "ok"));
    }

    @CrossOrigin("*")
    @RequestMapping(value = {"/pub/ap/danmaku"}, method = RequestMethod.GET)
    public ResponseEntity<?> getDanmaku() {
        List<ApDanmaku> danmaluList = apService.getApDanmaku();
        return ResponseEntity.ok(danmaluList);
    }

    //年会抽奖
	@RequestMapping(value = { "/apis/auth/app/ap/draw" }, method = RequestMethod.GET)
	public ResponseEntity<?> getApDrawResult() {
		Long userId = MyTokenUtils.getCurrentUserId();
		LOGGER.info("enter LuckyDrawController.getApDrawResult(userId {}", userId);
		List<ApDraw> apDrawList = apService.getApDrawListByUserId(userId);
		LOGGER.info("leave LuckyDrawController.getApDrawResult(userId {}", userId);
		return ResponseEntity.ok(new CommonAppResponse(0, apDrawList));
	}
	
	public static String encode(String key, String data) throws Exception {
	  SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
	  sha256_HMAC.init(secret_key);

	  return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
	}

	@CrossOrigin("*")
	@RequestMapping(value = { "/pub/ap/draw" }, method = RequestMethod.POST)
	public ResponseEntity<?> createApDrawResult(@RequestBody ApDraw apDraw, @RequestParam String ts, @RequestParam String sign) throws Exception {
		LOGGER.info("enter LuckyDrawController.createApDrawResult(apDraw {}", apDraw.toString());

		String source = apDraw.getEmployeeNo()+"-"+apDraw.getUserName()+"-"+apDraw.getAwardName()+"-"+ts;
		String output = encode(HMAC_SHA256_KEY, source);
		if(!output.equals(sign)) {
			LOGGER.error("sign not match", apDraw.toString());
			return ResponseEntity.ok(StringUtils.EMPTY);
		}

		apService.addApDraw(apDraw);
		LOGGER.info("leave LuckyDrawController.createApDrawResult(apDraw {}", apDraw.toString());
		return ResponseEntity.ok(StringUtils.EMPTY);
	}

	@CrossOrigin("*")
    @RequestMapping(value = { "/pub/ap/draw" }, method = RequestMethod.GET)
	public ResponseEntity<?> getApDrawResult(@RequestParam String code) {
    		LOGGER.info("enter PublicApiController.getApDrawResult(code {}", code);
		ApDraw apDraw = apService.getApDrawByCode(code);
		LOGGER.info("end PublicApiController.getApDrawResult(code {}", code);
		return ResponseEntity.ok(new CommonAppResponse(0, apDraw));
	}

    //年会开关
    @RequestMapping(value = "/apis/auth/app/ap/startFlag", method = RequestMethod.GET)
    public ResponseEntity<?> getAnnualPartyStartFlag() {
        LOGGER.info("begin getAnnualPartyStartFlag...");
        Boolean result = apService.getAnnualPartyStartFlag();
        LOGGER.info("end getAnnualPartyStartFlag...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }
}
