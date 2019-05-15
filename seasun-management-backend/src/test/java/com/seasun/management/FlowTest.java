package com.seasun.management;


import com.seasun.management.flow.ExampleSalaryChangeListener;
import com.seasun.management.flow.FlowService;
import com.seasun.management.mapper.FInstanceDetailMapper;
import com.seasun.management.mapper.FInstanceMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
//@Transactional
public class FlowTest {

    @Autowired
    FlowService flowService;

    @Autowired
    ExampleSalaryChangeListener exampleSalaryChangeListener;

    @Autowired
    FInstanceMapper fInstanceMapper;

    @Autowired
    FInstanceDetailMapper fInstanceDetailMapper;

    @Test
    public void testInit() {
        flowService.init("测试流程xht1", 123L, null, exampleSalaryChangeListener);
    }

    @Test
    public void testComplete() {
        Long instanceId = 16L;
        Long instanceDetailId = 22L;
        flowService.complete(fInstanceMapper.selectByPrimaryKey(instanceId),
                fInstanceDetailMapper.selectByPrimaryKey(instanceDetailId), "步骤2已完成", null, exampleSalaryChangeListener);
    }

    @Test
    public void testReject() {
        Long instanceId = 13L;
        Long instanceDetailId = 16L;
        flowService.reject(fInstanceMapper.selectByPrimaryKey(instanceId),
                fInstanceDetailMapper.selectByPrimaryKey(instanceDetailId),
                "强制结束流程", null, exampleSalaryChangeListener);
    }

    @Test
    public void testRejectAndRollback() {
        Long instanceId = 17L;
        Long instanceDetailId = 25L;
        flowService.rejectAndRollback(fInstanceMapper.selectByPrimaryKey(instanceId),
                fInstanceDetailMapper.selectByPrimaryKey(instanceDetailId),
                "强制结束流程并返回", null, exampleSalaryChangeListener);
    }
}
