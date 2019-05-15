package com.seasun.management.controller.org;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.mapper.DepartmentMapper;
import com.seasun.management.service.DepartmentService;
import com.seasun.management.vo.DepartmentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    DepartmentService departmentService;

    @RequestMapping(value = "/apis/auth/departments", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDepartment() {
        logger.info("begin getAllDepartment...");
        List<DepartmentVo> departments = departmentService.getAllDepartment();
        logger.info("end getAllDepartment...");
        return ResponseEntity.ok(departments);
    }

    @RequestMapping(value = "/apis/auth/department/{departmentId}", method = RequestMethod.GET)
    public ResponseEntity<?> getDepartmentById(@PathVariable Long departmentId) {
        logger.info("begin getDepartmentById...");
        DepartmentVo department = departmentService.getDepartmentById(departmentId);
        logger.info("end getDepartmentById...");
        return ResponseEntity.ok(department);
    }

    @RequestMapping(value = "/apis/auth/department", method = RequestMethod.POST)
    public ResponseEntity<?> addDepartment(@RequestBody DepartmentVo departmentVo) {
        logger.info("begin updateDepartment...");
        departmentService.addDepartment(departmentVo);
        logger.info("end updateDepartment...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/department", method = RequestMethod.PUT)
    public ResponseEntity<?> updateDepartment(@RequestBody DepartmentVo departmentVo) {
        logger.info("begin updateDepartment...");
        departmentService.updateDepartment(departmentVo);
        logger.info("end updateDepartment...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/department/{departmentId}", method = RequestMethod.POST)
    public ResponseEntity<?> activeDepartment(@PathVariable Long departmentId) {
        logger.info("begin activeDepartment...");
        departmentService.activeDepartment(departmentId);
        logger.info("end activeDepartment...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    @RequestMapping(value = "/apis/auth/department/{departmentId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDepartment(@PathVariable Long departmentId) {
        logger.info("begin deleteDepartment...");
        departmentService.disableDepartment(departmentId);
        logger.info("end deleteDepartment...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
