package com.seasun.management.flow;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.exception.FlowException;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seasun.management.util.MyTokenUtils;


import java.util.*;

@Transactional
@Service
public class FlowServiceImpl implements FlowService {

    private static final Logger logger = LoggerFactory.getLogger(FlowServiceImpl.class);

    @Autowired
    FDefineMapper fDefineMapper;

    @Autowired
    FInstanceMapper fInstanceMapper;

    @Autowired
    FStepDefineMapper fStepDefineMapper;

    @Autowired
    FInstanceDetailMapper fInstanceDetailMapper;

    @Autowired
    FlogMapper flogMapper;

    @Override
    public FInstance init(String flowName, Long businessKey, Map<Object, Object> ext, BusinessListener businessListener) {

        User user = MyTokenUtils.getCurrentUser();

        // 0. 执行业务
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setBusinessKey(businessKey);
        flowInfo.setExt(ext);
        NextStepInfo nextStepInfo = null;
        if (null != businessListener) {
            nextStepInfo = businessListener.onInit(flowInfo);
        }

        // 1.创建流程实例 instance
        FDefine fFlowDefine = fDefineMapper.selectByName(flowName);
        FInstance fInstance = new FInstance();
        fInstance.setFlowId(fFlowDefine.getId());
        fInstance.setBusinessKey(businessKey);
        fInstance.setProcessResult(FInstance.ProcessResult.processing);
        fInstance.setCreatedBy(user.getId());
        fInstance.setStartTime(new Date());
        fInstanceMapper.insert(fInstance);

        // 2.创建第一个流程节点
        FStepDefine firstStep = fStepDefineMapper.selectFirstStep(fFlowDefine.getId());
        FInstanceDetail firstInstanceDetail = new FInstanceDetail();
        firstInstanceDetail.setfStepDefineId(firstStep.getId());
        firstInstanceDetail.setProcessResult(FInstanceDetail.ProcessResult.success); // 第一步默认已完成
        firstInstanceDetail.setStartTime(new Date());
        firstInstanceDetail.setEndTime(new Date());
        firstInstanceDetail.setEndFlag(firstStep.getEndFlag());
        firstInstanceDetail.setInstanceId(fInstance.getId());
        firstInstanceDetail.setManagerId(firstStep.getManagerId());
        fInstanceDetailMapper.insert(firstInstanceDetail);

        // 3. 创建第二个流程节点
        processNextStep(fInstance, firstInstanceDetail, nextStepInfo);

        return fInstance;
    }

    @Override
    public void complete(FInstance fInstance, FInstanceDetail fInstanceDetail, String remark, Map<Object, Object> ext, BusinessListener businessListener) {

        // 0. 执行业务
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setInstanceId(fInstance.getId());
        flowInfo.setInstanceDetailId(fInstanceDetail.getId());
        flowInfo.setExt(ext);
        NextStepInfo nextStepInfo = null;
        if (null != businessListener) {
            nextStepInfo = businessListener.onComplete(flowInfo);
        }

        // 1.标记当前 f_instance_detail 节点为已完成
        FInstanceDetail cond = new FInstanceDetail();
        cond.setId(fInstanceDetail.getId());
        cond.setProcessResult(FInstanceDetail.ProcessResult.success);
        cond.setEndTime(new Date());
        cond.setRemark(remark);
        fInstanceDetailMapper.updateByPrimaryKeySelective(cond);

        // 2.处理后续节点
        processNextStep(fInstance, fInstanceDetail, nextStepInfo);


    }


    @Override
    public void reject(FInstance fInstance, FInstanceDetail fInstanceDetail, String remark, Map<Object, Object> ext, BusinessListener businessListener) {

        // 0. 执行业务
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setInstanceId(fInstance.getId());
        flowInfo.setInstanceDetailId(fInstanceDetail.getId());
        flowInfo.setExt(ext);
        if (null != businessListener) {
            businessListener.onReject(flowInfo);
        }

        // 1.更新当前 instanceDetail 为失败
        FInstanceDetail detailCond = new FInstanceDetail();
        detailCond.setId(fInstanceDetail.getId());
        detailCond.setEndTime(new Date());
        detailCond.setProcessResult(FInstanceDetail.ProcessResult.failed);
        detailCond.setRemark(remark);
        fInstanceDetailMapper.updateByPrimaryKeySelective(detailCond);

        // 2.更新instance
        FInstance instanceCond = new FInstance();
        instanceCond.setId(fInstance.getId());
        instanceCond.setProcessResult(FInstance.ProcessResult.failed);
        instanceCond.setEndTime(new Date());
        fInstanceMapper.updateByPrimaryKeySelective(instanceCond);

    }

    @Override
    public void rejectAndRollback(FInstance fInstance, FInstanceDetail fInstanceDetail, String remark, Map<Object, Object> ext, BusinessListener businessListener) {

        // 0. 执行业务
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setInstanceId(fInstance.getId());
        flowInfo.setInstanceDetailId(fInstanceDetail.getId());
        flowInfo.setExt(ext);
        NextStepInfo nextStepInfo = null;

        if (null == businessListener) {
            throw new FlowException(ErrorCode.FLOW_NO_BUSINESS_LISTENER_FOUND_EXCEPTION, "没有配置业务监听，无法获取到回滚的目标节点");
        } else {
            nextStepInfo = businessListener.onRejectAndRollback(flowInfo);
        }
        if (null == nextStepInfo || null == nextStepInfo.getFlowId() || null == nextStepInfo.getNextStepName()) {
            throw new FlowException(ErrorCode.FLOW_INVALID_NEXT_STEP_NAME_EXCEPTION, "步骤名称不合法，回滚操作无法执行");
        }

        // 1.获取目标节点 nextStepInfo.getNextStepName(),
        FStepDefine targetStep = fStepDefineMapper.selectStepByName(fInstance.getFlowId(), nextStepInfo.getNextStepName());
        if (null == targetStep) {
            throw new FlowException(ErrorCode.FLOW_INVALID_NEXT_STEP_NAME_EXCEPTION, "步骤名称不合法，回滚操作无法执行");
        }

        // 2.标记当前目标节点的状态为初始化
        fInstanceDetailMapper.updateProcessResultByInstanceIdAndStepDefineId(FInstanceDetail.ProcessResult.processing, fInstance.getId(), targetStep.getId());

        // 3.删除目标节点之后的所有子节点
        List<FInstanceDetail> allInstanceDetails = fInstanceDetailMapper.selectByInstanceId(fInstance.getId());
        List<Long> childInstanceIds = new ArrayList<>();
        FInstanceDetail targetInstanceDetail = fInstanceDetailMapper.selectByInstanceIdAndStepDefineId(fInstance.getId(), targetStep.getId());
        fillChildInstanceIds(childInstanceIds, targetInstanceDetail.getId(), allInstanceDetails);
        fInstanceDetailMapper.batchDelete(childInstanceIds);

        // 操作日志
        FStepDefine currentStepDefine = fStepDefineMapper.selectByPrimaryKey(fInstanceDetail.getfStepDefineId());
        String comment = StringUtils.isEmpty(remark) ? "" : ",并留言：" + remark;
        Flog flog = new Flog();
        flog.setInstanceId(fInstance.getId());
        flog.setInstanceDetailId(fInstanceDetail.getId());
        flog.setCreateTime(new Date());
        flog.setContent(MyTokenUtils.getCurrentUserNameWithDefaultValue("系统管理员")
                + "将流程从" + currentStepDefine.getName()
                + " 回滚到-> "
                + targetStep.getName()
                + comment);
        flogMapper.insert(flog);

    }


    private void processNextStep(FInstance fInstance, FInstanceDetail fInstanceDetail, NextStepInfo nextStepInfo) {

        // 1. 无后续节点,直接标记 f_instance 为已完成
        if (fInstanceDetail.getEndFlag()) {
            FInstance instanceCond = new FInstance();
            instanceCond.setId(fInstance.getId());
            instanceCond.setEndTime(new Date());
            instanceCond.setProcessResult(FInstance.ProcessResult.success);
            fInstanceMapper.updateByPrimaryKeySelective(instanceCond);
            return;
        }

        // 2. 有后续节点，但业务未重新指定，则根据系统预定义的步骤生成实例
        List<FStepDefine> nextSteps = fStepDefineMapper.selectNextStepsByPreviousStep(fInstance.getFlowId(), fInstanceDetail.getfStepDefineId());
        if (null == nextStepInfo || nextStepInfo.getNextStepName() == null) {
            generateDetailsFromFlowDefine(nextStepInfo, fInstance.getId(), fInstanceDetail.getId(), nextSteps);
            return;
        }

        // 3. 有后续节点，且被业务重新指定，则根据业务指定的步骤生成实例
        generateDetailsFromNextStepInfo(nextStepInfo, fInstance.getFlowId(), fInstance.getId(), fInstanceDetail.getId());
    }


    private void fillChildInstanceIds(List<Long> childInstanceIds, Long targetDetailId, List<FInstanceDetail> allInstanceDetails) {
        for (FInstanceDetail child : allInstanceDetails) {
            if (null != child.getPreviousDetail() && child.getPreviousDetail().equals(targetDetailId)) {
                childInstanceIds.add(child.getId());
                fillChildInstanceIds(childInstanceIds, child.getId(), allInstanceDetails);
            }
        }
    }

    private void generateDetailsFromNextStepInfo(NextStepInfo nextStepInfo, Long flowId, Long fInstanceId, Long previousInstanceId) {
        if (nextStepInfo != null) {
            FStepDefine nextStepDefine = fStepDefineMapper.selectStepByName(flowId, nextStepInfo.getNextStepName());
            if (null == nextStepDefine) {
                throw new FlowException(ErrorCode.FLOW_INVALID_NEXT_STEP_NAME_EXCEPTION, "步骤名称不合法，找不到该步骤");
            }
            FInstanceDetail nextInstanceDetail = new FInstanceDetail();
            nextInstanceDetail.setfStepDefineId(nextStepDefine.getId());
            nextInstanceDetail.setProcessResult(FInstanceDetail.ProcessResult.processing); // 第二步默认初始化
            nextInstanceDetail.setStartTime(new Date());
            nextInstanceDetail.setPreviousDetail(previousInstanceId);
            nextInstanceDetail.setEndFlag(nextStepDefine.getEndFlag());
            nextInstanceDetail.setInstanceId(fInstanceId);

            if (null != nextStepInfo.getManagerId()) {
                nextInstanceDetail.setManagerId(nextStepInfo.getManagerId());
            } else {
                nextInstanceDetail.setManagerId(nextStepDefine.getManagerId());
            }
            fInstanceDetailMapper.insert(nextInstanceDetail);
        }
    }

    private void generateDetailsFromFlowDefine(NextStepInfo nextStepInfo, Long fInstanceId, Long previousInstanceId, List<FStepDefine> nextSteps) {
        if (null == nextSteps || nextSteps.isEmpty()) {
            logger.info("no next steps,will return...");
            return;
        }

        List<FInstanceDetail> nextInstanceDetails = new ArrayList<>();
        for (FStepDefine fStepDefine : nextSteps) {
            FInstanceDetail tmp = new FInstanceDetail();
            tmp.setfStepDefineId(fStepDefine.getId());
            tmp.setPreviousDetail(previousInstanceId);
            tmp.setProcessResult(FInstanceDetail.ProcessResult.processing); // 第二步默认初始化
            tmp.setStartTime(new Date());
            tmp.setEndFlag(fStepDefine.getEndFlag());
            tmp.setInstanceId(fInstanceId);
            if (null != nextStepInfo && null != nextStepInfo.getManagerId()) {
                tmp.setManagerId(nextStepInfo.getManagerId());
            } else {
                tmp.setManagerId(fStepDefine.getManagerId());
            }
            nextInstanceDetails.add(tmp);
        }
        fInstanceDetailMapper.batchInsert(nextInstanceDetails);
    }


}
