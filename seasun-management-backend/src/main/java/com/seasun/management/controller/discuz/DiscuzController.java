package com.seasun.management.controller.discuz;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.discuz.client.Client;
import com.seasun.management.service.DiscuzService;
import com.seasun.management.util.MyTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/apis/auth/discuz")
public class DiscuzController {
	@Autowired
	private Client e;
	@Autowired
	private DiscuzService discuzService;
	@RequestMapping(value="/login",method= RequestMethod.POST)
	public ResponseEntity<?> userLogin(){
		String token = MyTokenUtils.getCurrentToken();
		Map<String,String> map = discuzService.userLogin(token);
		int code = Integer.parseInt(map.get("code"));
		return  ResponseEntity.ok(new CommonResponse(code,map.get("msg")));
	}
}