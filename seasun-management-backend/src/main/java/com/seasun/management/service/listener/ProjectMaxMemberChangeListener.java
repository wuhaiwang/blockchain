package com.seasun.management.service.listener;

import com.seasun.management.flow.BusinessListener;
import com.seasun.management.flow.FlowInfo;
import com.seasun.management.flow.NextStepInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "project-max-member-change")
public class ProjectMaxMemberChangeListener implements BusinessListener {
    private static final Logger logger = LoggerFactory.getLogger(ProjectMaxMemberChangeListener.class);

    @Override
    public NextStepInfo onInit(FlowInfo flowInfo) {

        return new NextStepInfo();
    }

    @Override
    public NextStepInfo onComplete(FlowInfo flowInfo) {

        return new NextStepInfo();
    }

    @Override
    public void onReject(FlowInfo flowInfo) {

    }

    @Override
    public NextStepInfo onRejectAndRollback(FlowInfo flowInfo) {

        return new NextStepInfo();
    }
}
