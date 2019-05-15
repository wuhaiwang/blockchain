package com.seasun.management.service;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seasun.management.Application;
import com.seasun.management.constant.SyncType;
import com.seasun.management.helper.SyncHelper;
import com.seasun.management.vo.BatchSyncVo;
import com.seasun.management.vo.DepartmentSyncVo;
import com.seasun.management.vo.DepartmentSyncVo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")

public class BatchSyncServiceTest {


    @Test
    public void testSync() {
        SyncService syncService = SyncHelper.getSyncServiceByTargetName("batch-sync");

        // 构建request
        BatchSyncVo batchSyncVo = new BatchSyncVo();
        batchSyncVo.setTargetName("batch-sync");
        BatchSyncVo.BatchRequest requst = new BatchSyncVo.BatchRequest();
        requst.setTargetName("department");

        DepartmentSyncVo departmentSyncVo = new DepartmentSyncVo();
        departmentSyncVo.setTargetName("department");
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo.setId(12345L);
        departmentInfo.setName("新部门xht");
        departmentSyncVo.setData(departmentInfo);
        departmentSyncVo.setType(SyncType.update.toString());
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(departmentSyncVo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        requst.setData(JSONObject.parseObject(json));

        List<BatchSyncVo.BatchRequest> requstList = new ArrayList<>();
        requstList.add(requst);
        batchSyncVo.setRequests(requstList);

        // 测试
        syncService.run(batchSyncVo);
    }
}
