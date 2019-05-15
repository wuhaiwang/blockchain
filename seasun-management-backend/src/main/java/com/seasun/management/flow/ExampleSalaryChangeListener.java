package com.seasun.management.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExampleSalaryChangeListener implements BusinessListener {

    private static final Logger logger = LoggerFactory.getLogger(ExampleSalaryChangeListener.class);

    @Override
    public NextStepInfo onInit(FlowInfo flowInfo) {

        logger.debug("enter example onInit ....");
        return new NextStepInfo();
    }

    @Override
    public NextStepInfo onComplete(FlowInfo flowInfo) {
        logger.debug("enter example onComplete ....");
        return new NextStepInfo();
    }

    @Override
    public void onReject(FlowInfo flowInfo) {
        logger.debug("enter example onReject ....");

    }

    @Override
    public NextStepInfo onRejectAndRollback(FlowInfo flowInfo) {
        logger.debug("enter example onRejectAndRollback ....");
        NextStepInfo nextStepInfo = new NextStepInfo();
        nextStepInfo.setFlowId(1008L);
        nextStepInfo.setNextStepName("步骤1");
        return nextStepInfo;
    }
}
