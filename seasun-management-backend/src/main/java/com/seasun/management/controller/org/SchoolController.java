package com.seasun.management.controller.org;


import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.mapper.SchoolMapper;
import com.seasun.management.model.School;
import com.seasun.management.service.ProjectRoleService;
import com.seasun.management.service.SchoolService;
import com.seasun.management.vo.ProjectRoleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchoolController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    SchoolService schoolService;

    @Autowired
    SchoolMapper schoolMapper;

    @RequestMapping(value = "/apis/auth/schools", method = RequestMethod.GET)
    public ResponseEntity<?> getAllSchools() {
        logger.info("begin getAllSchools...");
        School school = schoolService.selectFixed1000();
        logger.info("end getAllSchools...");
        return ResponseEntity.ok(school);
    }


    @RequestMapping(value = "/apis/auth/school", method = RequestMethod.POST)
    public ResponseEntity<?> reloadSchool() {
        logger.info("begin reloadSchool...");
        schoolMapper.reloadCache();
        logger.info("end reloadSchool...");
        return ResponseEntity.ok(new CommonResponse(0, "reload success"));
    }
}
