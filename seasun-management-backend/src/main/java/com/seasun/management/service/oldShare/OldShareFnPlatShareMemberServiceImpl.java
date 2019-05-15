package com.seasun.management.service.oldShare;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.FnPlatShareMemberDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.mapper.FnPlatShareConfigMapper;
import com.seasun.management.mapper.FnPlatShareMemberMapper;
import com.seasun.management.mapper.FnShareInfoMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.model.FnPlatShareMember;
import com.seasun.management.model.FnShareInfo;
import com.seasun.management.model.RUserProjectPerm;
import com.seasun.management.service.FnPlatShareMemberService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.service.impl.FnPlatShareMemberServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service(value = "oldShareFnPlatShareMemberService")
public class OldShareFnPlatShareMemberServiceImpl implements FnPlatShareMemberService {
    public OldShareFnPlatShareMemberServiceImpl() {
        super();
    }

    private static final Logger logger = LoggerFactory.getLogger(OldShareFnPlatShareMemberServiceImpl.class);

    @Autowired
    FnPlatShareMemberMapper fnPlatShareMemberMapper;

    @Autowired
    FnPlatShareConfigMapper fnPlatShareConfigMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    FnShareInfoMapper fnShareInfoMapper;

    protected FnPlatShareConfigMapper getFnPlatShareConfigMapper() {
        return fnPlatShareConfigMapper;
    }

    @Override
    public List<FnPlatShareMemberVo> getPlatShareMembers(Long platId) {
        List<FnPlatShareMemberVo> fnPlatShareMemberVos = fnPlatShareMemberMapper.selectByPlatId(platId);
        return fnPlatShareMemberVos;
    }

    @Override
    public FnPlatManageAndMemberVo getPlatManageAndMemberVo(Long platId) {
        FnPlatManageAndMemberVo result = new FnPlatManageAndMemberVo();
        result.setProjectRoleId(ProjectRole.Role.plat_share_manager_id);
        result.setShareMembers(getPlatShareMembers(platId));
        result.setShareObservers(getPlatObservers(platId));
        result.setShareManagers(getPlatShareManagers(platId));
        return result;
    }

    private List<FnPlatShareMemberVo> getPlatMemberWithRole(Long projectId, Long roleId) {
        List<FnPlatShareMemberVo> fnPlatShareMembers = new ArrayList<>();
        List<UserSelectVo> userSelectVos = rUserProjectPermMapper.selectUserSelectVoByProjectIdAndRoleId(projectId, roleId);
        for (UserSelectVo userSelectVo : userSelectVos) {
            FnPlatShareMemberVo fnPlatShareMemberVo = new FnPlatShareMemberVo();
            fnPlatShareMemberVo.setId(userSelectVo.getId());
            fnPlatShareMemberVo.setProjectId(projectId);
            fnPlatShareMemberVo.setLoginId(userSelectVo.getLoginId());
            fnPlatShareMemberVo.setUserId(userSelectVo.getUserId());
            fnPlatShareMemberVo.setUserName(userSelectVo.getName());
            fnPlatShareMemberVo.setUserPhoto(userSelectVo.getUserPhoto());
            fnPlatShareMembers.add(fnPlatShareMemberVo);
        }
        return fnPlatShareMembers;
    }

    protected List<FnPlatShareMemberVo> getPlatObservers(Long projectId) {
        return getPlatMemberWithRole(projectId, ProjectRole.Role.plat_share_observer_id);
    }

    @Override
    public List<FnPlatShareMemberVo> getPlatShareManagers(Long projectId) {
        return getPlatMemberWithRole(projectId, ProjectRole.Role.plat_share_manager_id);
    }


    @Transactional
    @Override
    public JSONObject insert(FnPlatShareMember fnPlatShareMember) {
        if (null == fnPlatShareMember.getPlatId() || null == fnPlatShareMember.getUserId()) {
            logger.info("insertPlatShareMember failed...");
            throw new ParamException("platId and userId can not be null...");
        }
        if (fnPlatShareMember.getWeight().floatValue() <= 0) {
            logger.info("insertPlatShareMember failed...");
            throw new ParamException("weight can not less than zero...");
        }

        JSONObject jsonObject = new JSONObject();

        FnPlatShareMemberDto checkFnPlatShareMemberDto = fnPlatShareMemberMapper.selectWithPlatNameByUserId(fnPlatShareMember.getUserId());
        if (null != checkFnPlatShareMemberDto) {
            jsonObject.put("code", ErrorCode.PLAT_SHARE_MEMBER_EXISTS);
            jsonObject.put("message", "current user belong to other share plat");
            jsonObject.put("platName", checkFnPlatShareMemberDto.getPlatName());
        } else {
            fnPlatShareMemberMapper.insert(fnPlatShareMember);

            RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
            rUserProjectPerm.setUserId(fnPlatShareMember.getUserId());
            rUserProjectPerm.setProjectId(fnPlatShareMember.getPlatId());
            rUserProjectPerm.setProjectRoleId(PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_PEOPLE_ID);
            RUserProjectPerm data = rUserProjectPermMapper.selectSelective(rUserProjectPerm);
            if (null == data) {
                rUserProjectPermMapper.insert(rUserProjectPerm);
            }

            jsonObject.put("code", 0);
            jsonObject.put("message", "success");
        }

        return jsonObject;
    }

    @Override
    public void update(FnPlatShareMember fnPlatShareMember) {
        if (null == fnPlatShareMember.getId()) {
            logger.info("updatePlatShareMember failed...");
            throw new ParamException("id can not be null...");
        }
        if (fnPlatShareMember.getWeight().floatValue() <= 0) {
            logger.info("insertPlatShareMember failed...");
            throw new ParamException("weight can not less than zero...");
        }
        fnPlatShareMemberMapper.updateByPrimaryKeySelective(fnPlatShareMember);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        FnPlatShareMember fnPlatShareMember = fnPlatShareMemberMapper.selectByPrimaryKey(id);
        if (null != fnPlatShareMember) {
            fnPlatShareMemberMapper.deleteByPrimaryKey(id);

            // 删除当前正在进行的记录
            FnShareInfo latestRecord = fnShareInfoMapper.selectLatestProcessingRecord();
            if (latestRecord != null) {
                fnPlatShareConfigMapper.deleteByCreateBy(fnPlatShareMember.getUserId(), latestRecord.getYear(), latestRecord.getMonth());
            }

            RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
            rUserProjectPerm.setUserId(fnPlatShareMember.getUserId());
            rUserProjectPerm.setProjectId(fnPlatShareMember.getPlatId());
            rUserProjectPerm.setProjectRoleId(PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_PEOPLE_ID);
            rUserProjectPermMapper.deleteSelective(rUserProjectPerm);
        }
    }

    @Override
    //TP
    public List<UserLoginEmailVo> getShareMembersConfigInfo(Long platId, Integer year, Integer month) {
        List<UserLoginEmailVo> result = new ArrayList<>();

        List<FnPlatShareMemberVo> members = fnPlatShareMemberMapper.selectByPlatId(platId);
        List<Long> validMemberIds = new ArrayList<>();
        for (FnPlatShareMemberVo memberVo : members) {
            validMemberIds.add(memberVo.getUserId());
        }

        // 加入未填写的member
        List<UserLoginEmailVo> noConfigMemberList = fnPlatShareMemberMapper.getNoConfigShareMemberInfo(platId, year, month);
        result.addAll(noConfigMemberList);

        // 加入未完成的member
        List<UserLoginEmailVo> noCompleteConfigMemberList = fnPlatShareMemberMapper.getShareMemberConfigInfo(platId, year, month);
        for (UserLoginEmailVo userLoginEmailVo : noCompleteConfigMemberList) {
            if (validMemberIds.contains(userLoginEmailVo.getId())) {
                result.add(userLoginEmailVo);
            }
        }

        return result;
    }
}
