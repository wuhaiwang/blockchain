package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.constant.SyncType;
import com.seasun.management.mapper.CostCenterMapper;
import com.seasun.management.mapper.DepartmentMapper;
import com.seasun.management.helper.SyncHelper;
import com.seasun.management.model.Department;
import com.seasun.management.vo.DepartmentSyncVo;
import com.seasun.management.vo.DepartmentSyncVo.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class DepartmentServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceTest.class);

    @Autowired
    DepartmentService departmentService;

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    CostCenterMapper costCenterMapper;

    private Long newDepartmentId = 3L;
    private String departmentCostCenters = "223,225,228";

    @Test
    public void testSync() {
        SyncService syncService = SyncHelper.getSyncServiceByTargetName("department");
        DepartmentSyncVo vo = new DepartmentSyncVo();
        DepartmentInfo info = new DepartmentInfo();
        info.setId(newDepartmentId);
        info.setName("xht测试部门");
        info.setCostCenterCode(departmentCostCenters);
        vo.setData(info);
        vo.setType(SyncType.add.toString());
        syncService.run(vo);

        Department departmentCond = new Department();
        departmentCond.setName("xht测试部门");
        String name = departmentMapper.selectByCondition(departmentCond).get(0).getName();
        logger.debug("--- curernt department name :{}",name);
        String [] codes = departmentCostCenters.split(",");

        //int newSize = costCenterMapper.selectByCodesIn(codes).size();
        //logger.debug("--- new costcenter size:{}",newSize);
    }

    @After
    public void after() {
        String [] codes = departmentCostCenters.split(",");
        //costCenterMapper.deleteByCodesNotIn(codes);
        departmentMapper.disableByPrimaryKey(newDepartmentId);
    }

}
