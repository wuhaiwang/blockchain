package com.seasun.management.controller.org;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.vo.UserPhotoWallVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/apis/auth/org")
public class UserPhotoController {
    private static final Logger logger = LoggerFactory.getLogger(UserPhotoController.class);

    @Autowired
    UserPerformanceService userPerformanceService;

    @RequestMapping(value = "/photo-wall", method = RequestMethod.GET)
    public ResponseEntity<?> getPhotoWall(@RequestParam(required = false)Integer year, @RequestParam(required = false)Integer month, @RequestParam(required = false)String floorId){
        UserPhotoWallVo vo = userPerformanceService.selectUserPerformance(year, month, floorId);
        return ResponseEntity.ok(new CommonAppResponse(0, vo));
    }

    @RequestMapping(value = "/photo-wall-upload", method = RequestMethod.POST)
    public ResponseEntity<?> uploadUserPhoto(MultipartFile file , String loginId){
        boolean flag = userPerformanceService.uploadUserPhoto(file,loginId);
        if(flag){
            return ResponseEntity.ok(new CommonResponse(0,"success"));
        }else{
            return ResponseEntity.ok(new CommonResponse(999,"fail"));
        }
    }

    @RequestMapping(value = "/photo-wall-download", method = RequestMethod.GET)
    public ResponseEntity<?> downloadUserPhoto(@RequestParam(required = false)Integer year, @RequestParam(required = false)Integer month, @RequestParam(required = false)String floorName){
        String url = userPerformanceService.downloadUserPhoto(year,month,floorName);
        return ResponseEntity.ok(new CommonResponse(0, url));
    }

}
