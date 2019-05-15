package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.dto.PerformanceWorkGroupDto;
import com.seasun.management.dto.WorkGroupMemberDto;
import com.seasun.management.model.PerfUserCheckResult;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.model.UserPerfWorkGroupVo;
import com.seasun.management.vo.*;

import java.util.List;
import java.util.Map;

public interface PerformanceWorkGroupService {

    PerformanceWorkGroupNodeVo getPerformanceTree();

    PerformanceWorkGroupVo getPerformanceWorkGroupInfoById(Long id);

    Long addSubPerformanceWorkGroup(Long performanceWorkGroupId, Long subGroupId, Long managerId, Integer strictType, String newName);

    void deleteSubPerformanceGroup(Long id);

    void updateByCond(PerformanceWorkGroup performanceWorkGroup);

    void forceAddPerformanceMember(Long memberId, Long performanceWorkGroupId);

    JSONObject checkPerformanceMember(Long memberId);

    void syncHrWorkGroupToPerformanceWorkGroup();

    void syncHrWorkGroupToPerformanceWorkGroupById(Long performanceGroupId);

    void deleteDirectMember(String memberLoginId);

    CheckResultVo checkGroup();

    void changePerformanceWorkGroupParent(Long sourceId, Long parentId);

    List<PerformanceWorkGroupDto> getPerformanceWorkGroupsByWorkGroup(Long workGroupId);

    List<PerformanceWorkGroupDto> getPerformanceWorkGroupsByManager(Long managerId);

    List<PerformanceFixMemberSimpleInfoVo> getPerformanceGroupMember(Long platId);

    List<UserPerfWorkGroupVo> getUsers(String keyword);

    WorkGroupCompVo comparePerfHr(Long perfWGId, Long HrWGId);

    Long getWorkGroupTotalMemberById(Long id, Map<Long, List<WorkGroupMemberDto>> totalMemberMap);

    String getPerfWorkGroupName(Long perWorkGroupId, Map<Long, PerformanceWorkGroup> idPWGMap);

    Long insertPerfUserCheckResult(PerfUserCheckResult perfUserCheckResult);

    void batchUpdatePerfUserCheckResult(List<PerfUserCheckResult> perfUserCheckResult);

    int updateUserCheckResult(Long id, PerfUserCheckResult perfUserCheckResult);
}
