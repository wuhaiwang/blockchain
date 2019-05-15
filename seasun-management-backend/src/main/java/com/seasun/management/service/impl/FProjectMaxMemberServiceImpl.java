package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.exception.ParamException;
import com.seasun.management.flow.BusinessListener;
import com.seasun.management.flow.FlowService;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.UserMessageService;
import com.seasun.management.util.MyEmojiUtil;
import com.seasun.management.vo.ApprovalFlowVo;
import com.seasun.management.vo.FProjectMaxMemberVo;
import com.seasun.management.service.FProjectMaxMemberService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.ProjectMaxMemberVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FProjectMaxMemberServiceImpl implements FProjectMaxMemberService {
    private static final Logger logger = LoggerFactory.getLogger(FProjectMaxMemberServiceImpl.class);

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FProjectMaxMemberMapper fProjectMaxMemberMapper;

    @Autowired
    FlowService flowService;

    @Resource(name = "project-max-member-change")
    BusinessListener businessListener;

    @Autowired
    FInstanceMapper fInstanceMapper;

    @Autowired
    FInstanceDetailMapper fInstanceDetailMapper;

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    private String getIdentity(User user) {
        String identity;
        if (MyTokenUtils.isBoss(user)) {
            identity = "boss";
        } else if (MyTokenUtils.isHrManager(user)) {
            identity = "hr";
        } else if (MyTokenUtils.isAppAdmin(user.getId())) {
            identity = "admin";
        } else {
            identity = "project-manager";
        }
        return identity;
    }

    @Override
    public JSONObject getManageProjects() {
        JSONObject jsonObject = new JSONObject();
        User user = MyTokenUtils.getCurrentUser();
        String identity = getIdentity(user);
        jsonObject.put("identity", identity);

        List<ProjectMaxMemberVo> projectMaxMemberVos;
        if ("boss".equals(identity) || "hr".equals(identity) || "admin".equals(identity)) {
            projectMaxMemberVos = projectMapper.selectAllWithMaxMemberAndShowApp();
            List<ProjectMaxMemberVo> projects = projectMaxMemberVos.stream().filter(p -> !Project.ServiceLine.plat.equals(p.getServiceLine())).collect(Collectors.toList());
            List<ProjectMaxMemberVo> platforms = projectMaxMemberVos.stream().filter(p -> Project.ServiceLine.plat.equals(p.getServiceLine())).collect(Collectors.toList());
            jsonObject.put("projects", projects);
            jsonObject.put("platforms", platforms);
        } else {
            projectMaxMemberVos = projectMapper.selectAllWithMaxMemberByManagerAndShowApp(user.getId());
            jsonObject.put("projects", projectMaxMemberVos);
        }

        return jsonObject;
    }

    @Override
    public JSONObject getFlowListByProject(Long projectId) {
        JSONObject jsonObject = new JSONObject();
        FProjectMaxMemberVo currentFlow = fProjectMaxMemberMapper.selectByProjectIdAndProcessing(projectId);
        List<FProjectMaxMemberVo> historyFlowList = fProjectMaxMemberMapper.selectAllByProjectIdAndComplete(projectId);
        if (null == currentFlow) {
            ProjectMaxMemberVo projectMaxMemberVo = projectMapper.selectWithMaxMemberByProjectId(projectId);
            if (null != projectMaxMemberVo) {
                currentFlow = new FProjectMaxMemberVo();
                currentFlow.setProjectId(projectMaxMemberVo.getId());
                currentFlow.setProjectName(projectMaxMemberVo.getName());
                currentFlow.setCurrentMember(projectMaxMemberVo.getCurrentMember());
                currentFlow.setMaxMember(projectMaxMemberVo.getMaxMember());
                currentFlow.setProcessResult(FInstance.ProcessResult.processing);
            }
        }
        jsonObject.put("current", currentFlow);
        jsonObject.put("history", historyFlowList);
        return jsonObject;
    }

    @Transactional
    @Override
    public void createApprovalFlow(FProjectMaxMember fProjectMaxMember) {
        if (null == fProjectMaxMember.getProjectId()) {
            logger.info("projectId is null");
            throw new ParamException("项目ID为空");
        }

        FProjectMaxMemberVo FProjectMaxMemberVo = fProjectMaxMemberMapper.selectByProjectIdAndProcessing(fProjectMaxMember.getProjectId());
        if (null != FProjectMaxMemberVo) {
            logger.info("this project has processing process, can not create new process.");
            throw new ParamException("还有待审批的申请，不能再发起申请");
        }

        User user = MyTokenUtils.getCurrentUser();

        String flowName = FDefine.Name.projectMaxMemberProcessApproval;
        if (null != fProjectMaxMember.getReason()) {
            fProjectMaxMember.setReason(MyEmojiUtil.resolveToByteFromEmoji(fProjectMaxMember.getReason()));
        }
        fProjectMaxMember.setDeployFlag(false);
        fProjectMaxMemberMapper.insertSelective(fProjectMaxMember);
        FInstance fInstance = flowService.init(flowName, fProjectMaxMember.getId(), null, businessListener);

        // 消息通知
        FProjectMaxMemberVo newProcessingFlow = fProjectMaxMemberMapper.selectByInstanceId(fInstance.getId());
        if (null != newProcessingFlow && null != newProcessingFlow.getManagerId()) {
            User manager = userMapper.selectByPrimaryKey(newProcessingFlow.getManagerId());
            String content = user.getName() + "申请提高" + newProcessingFlow.getProjectName() + "的上限人数为" + fProjectMaxMember.getApplyMaxMember();
            UserMessage userMessage = getFlowUserMessage(manager, fInstance.getId(), fInstance.getProcessResult(), content);
            userMessageService.add(userMessage);
        }
    }

    @Override
    public List<FProjectMaxMemberVo> getFlowListByApproval() {
        User user = MyTokenUtils.getCurrentUser();
        List<FProjectMaxMemberVo> FProjectMaxMemberVos;
        if (MyTokenUtils.isHrManager(user) || MyTokenUtils.isAppAdmin(user.getId())) {
            FProjectMaxMemberVos = fProjectMaxMemberMapper.selectAllByProcessing();
        } else {
            FProjectMaxMemberVos = fProjectMaxMemberMapper.selectAllByManagerIdAndProcessing(user.getId());
        }
        return FProjectMaxMemberVos;
    }

    @Override
    public FProjectMaxMemberVo getFlowByInstanceId(Long instanceId) {
        FProjectMaxMemberVo fProjectMaxMemberVo = fProjectMaxMemberMapper.selectByInstanceId(instanceId);
        if (null != fProjectMaxMemberVo.getReason()) {
            fProjectMaxMemberVo.setReason(MyEmojiUtil.resolveToEmojiFromByte(fProjectMaxMemberVo.getReason()));
        }
        if (null != fProjectMaxMemberVo.getApprovalComment()) {
            fProjectMaxMemberVo.setApprovalComment(MyEmojiUtil.resolveToEmojiFromByte(fProjectMaxMemberVo.getApprovalComment()));
        }
        return fProjectMaxMemberVo;
    }

    @Transactional
    @Override
    public void batchApprovalFlow() {
        User user = MyTokenUtils.getCurrentUser();
        List<FProjectMaxMemberVo> FProjectMaxMemberVos = fProjectMaxMemberMapper.selectAllByManagerIdAndProcessing(user.getId());
        for (FProjectMaxMemberVo fProjectMaxMemberVo : FProjectMaxMemberVos) {
            ApprovalFlowVo approvalFlowVo = new ApprovalFlowVo();
            approvalFlowVo.setInstanceId(fProjectMaxMemberVo.getInstanceId());
            approvalFlowVo.setPass(true);
            approvalFlow(approvalFlowVo);
        }
    }

    @Transactional
    @Override
    public void approvalFlow(ApprovalFlowVo approvalFlowVo) {
        User user = MyTokenUtils.getCurrentUser();
        approvalFlow(approvalFlowVo, user);
    }

    private void approvalFlow(ApprovalFlowVo approvalFlowVo, User user) {
        if (null == approvalFlowVo.getInstanceId()) {
            logger.info("approval process params instanceId is null");
            throw new ParamException("instanceId is null");
        }

        FInstance fInstance = fInstanceMapper.selectByPrimaryKey(approvalFlowVo.getInstanceId());
        if (FInstance.ProcessResult.processing != fInstance.getProcessResult()) {
            logger.info("this process is end");
            throw new ParamException("当前流程已结束");
        }

        FInstanceDetail fInstanceDetail = fInstanceDetailMapper.selectByInstanceIdAndProcessing(approvalFlowVo.getInstanceId());
        if (null == fInstanceDetail) {
            logger.info("processing instance detail is null");
            throw new ParamException("当前流程没有待处理步骤");
        }
        if (!user.getId().equals(fInstanceDetail.getManagerId())) {
            logger.info("processing instance's managerId is not current user");
            throw new ParamException("当前流程的负责人不是你，不能执行该操作");
        }

        FProjectMaxMemberVo fProjectMaxMember = fProjectMaxMemberMapper.selectWithProjectNameByPrimaryKey(fInstance.getBusinessKey());
        if (null == fProjectMaxMember) {
            logger.info("fProjectMaxMember is null");
            throw new ParamException("流程数据异常");
        }

        if (null != approvalFlowVo.getApprovalComment()) {
            fProjectMaxMember.setApprovalComment(MyEmojiUtil.resolveToByteFromEmoji(approvalFlowVo.getApprovalComment()));
            fProjectMaxMemberMapper.updateByPrimaryKeySelective(fProjectMaxMember);
        }

        List<UserMessage> userMessages = new ArrayList<>();

        if (approvalFlowVo.getPass()) {
            flowService.complete(fInstance, fInstanceDetail, "同意" + fProjectMaxMember.getProjectName() + "上限人数申请", null, businessListener);
            if (fInstanceDetail.getEndFlag()) {
                Project project = new Project();
                project.setId(fProjectMaxMember.getProjectId());
                project.setMaxMember(fProjectMaxMember.getApplyMaxMember());
                projectMapper.updateByPrimaryKeySelective(project);
            }
            // HR负责人消息
            List<String> hrManagers = MyTokenUtils.getHrManagers();
            if (null != hrManagers) {
                String content = user.getName() + "同意了" + fProjectMaxMember.getProjectName() + "的上限人数申请";
                List<UserMessage> userMessage = getFlowUserMessageForHrManagers(user, hrManagers, fInstance.getId(), FInstance.ProcessResult.success, content);
                userMessages.addAll(userMessage);
            }
        } else {
            flowService.reject(fInstance, fInstanceDetail, "拒绝" + fProjectMaxMember.getProjectName() + "上限人数申请", null, businessListener);
        }

        // 消息通知
        if (null != fInstance.getCreatedBy()) {
            User createdBy = userMapper.selectByPrimaryKey(fInstance.getCreatedBy());
            String content;
            if (approvalFlowVo.getPass()) {
                content = user.getName() + "同意您提交的" + fProjectMaxMember.getProjectName() + "上限人数申请";
            } else {
                content = user.getName() + "拒绝您提交的" + fProjectMaxMember.getProjectName() + "上限人数申请";
            }
            UserMessage userMessage = getFlowUserMessage(createdBy, fInstance.getId(), FInstance.ProcessResult.failed, content);
            userMessages.add(userMessage);
        }
        if (!userMessages.isEmpty()) {
            userMessageService.batchAdd(userMessages);
        }
    }

    @Transactional
    @Override
    public void createDeployFlow(FProjectMaxMember fProjectMaxMember) {
        User user = MyTokenUtils.getCurrentUser();

        FProjectMaxMemberVo FProjectMaxMemberVo = fProjectMaxMemberMapper.selectByProjectIdAndProcessing(fProjectMaxMember.getProjectId());
        if (null != FProjectMaxMemberVo) {
            throw new ParamException("还有待审批的申请，不能直接调配上限人数");
        }

        String flowName = FDefine.Name.projectMaxMemberProcessDeploy;
        fProjectMaxMember.setDeployFlag(true);
        fProjectMaxMemberMapper.insertSelective(fProjectMaxMember);
        FInstance fInstance = flowService.init(flowName, fProjectMaxMember.getId(), null, businessListener);
        Project projectCond = new Project();
        projectCond.setId(fProjectMaxMember.getProjectId());
        projectCond.setMaxMember(fProjectMaxMember.getApplyMaxMember());
        projectMapper.updateByPrimaryKeySelective(projectCond);

        // 消息通知
        List<UserMessage> userMessages = new ArrayList<>();
        Project project = projectMapper.selectByPrimaryKey(fProjectMaxMember.getProjectId());
        String content = user.getName() + "直接调配了" + project.getName() + "的上限人数";
        RUserProjectPerm rUserProjectPerm = rUserProjectPermMapper.selectAllSystemProjectRoleByProjectIdAndProjectRoleId(fProjectMaxMember.getProjectId(), ProjectRole.Role.project_manager_id);
        User manager = userMapper.selectByPrimaryKey(rUserProjectPerm.getUserId());
        // 发给项目负责人
        if (null != manager && !MyTokenUtils.isBoss(manager)) {
            UserMessage userMessage = getFlowUserMessage(manager, fInstance.getId(), FInstance.ProcessResult.success, content);
            userMessages.add(userMessage);
        }
        // 发给BOSS
        if (!MyTokenUtils.isBoss(user)) {
            User boss = MyTokenUtils.getBoss();
            if (null != boss) {
                UserMessage userMessage = getFlowUserMessage(boss, fInstance.getId(), FInstance.ProcessResult.success, content);
                userMessages.add(userMessage);
            }
        }
        // 发给HR负责人
        List<String> hrManagers = MyTokenUtils.getHrManagers();
        if (null != hrManagers) {
            List<UserMessage> userMessage = getFlowUserMessageForHrManagers(user, hrManagers, fInstance.getId(), FInstance.ProcessResult.success, content);
            userMessages.addAll(userMessage);
        }
        if (!userMessages.isEmpty()) {
            userMessageService.batchAdd(userMessages);
        }
    }


    private List<UserMessage> getFlowUserMessageForHrManagers(User currentUser, List<String> hrManagers, Long fInstanceId, Integer processResult, String content) {

        List<UserMessage> result = new ArrayList<>();
        List<User> hrUsers = userMapper.selectBYUserLoginIds(hrManagers);

        for (User hr : hrUsers) {
            if (hr.getId().equals(currentUser.getId())) {
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("instanceId", fInstanceId);
            jsonObject.put("identity", "hr");
            jsonObject.put("processResult", processResult);

            UserMessage userMessage = new UserMessage();
            userMessage.setType(UserMessage.Type.system);
            userMessage.setLocation(UserMessage.Location.projectMaxMemberFlow);
            userMessage.setReceiver(hr.getId());
            userMessage.setContent(content);
            userMessage.setParams(jsonObject.toJSONString());
            result.add(userMessage);
        }

        return result;
    }

    private UserMessage getFlowUserMessage(User receiver, Long fInstanceId, Integer processResult, String content) {
        String identity = getIdentity(receiver);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("instanceId", fInstanceId);
        jsonObject.put("identity", identity);
        jsonObject.put("processResult", processResult);

        UserMessage userMessage = new UserMessage();
        userMessage.setType(UserMessage.Type.system);
        userMessage.setLocation(UserMessage.Location.projectMaxMemberFlow);
        userMessage.setReceiver(receiver.getId());
        userMessage.setContent(content);
        userMessage.setParams(jsonObject.toJSONString());
        return userMessage;
    }
}
