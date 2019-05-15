package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.model.FnPlatShareMember;
import com.seasun.management.vo.*;

import java.util.List;

public interface FnPlatShareMemberService {

    List<FnPlatShareMemberVo> getPlatShareMembers(Long platId);

    //管理人员
    List<FnPlatShareMemberVo> getPlatShareManagers(Long projectId);

    FnPlatManageAndMemberVo getPlatManageAndMemberVo(Long platId);

    JSONObject insert(FnPlatShareMember fnPlatShareMember);

    void update(FnPlatShareMember fnPlatShareMember);

    void delete(Long id);

    List<UserLoginEmailVo> getShareMembersConfigInfo(Long platId, Integer year, Integer month);

}
