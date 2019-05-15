package com.seasun.management.controller.org;

import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.service.ProjectRoleService;
import com.seasun.management.vo.ProjectRoleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class ProjectRoleController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    ProjectRoleService projectRoleService;

    @RequestMapping(value = "/apis/auth/project-roles", method = RequestMethod.GET)
    public ResponseEntity<?> getAllActiveProjectRoles() {
        logger.info("begin getAllActiveProjectRoles...");
        ProjectRoleVo projectRoles = projectRoleService.getAllActiveProjectRoles();
        logger.info("end getAllActiveProjectRoles...");
        return ResponseEntity.ok(projectRoles);
    }
}
