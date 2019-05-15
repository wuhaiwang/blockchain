package com.seasun.management.service;

import com.seasun.management.model.RUserWorkGroupPerm;

import java.util.List;

public interface RUserWorkGroupService {
    int insert(RUserWorkGroupPerm record);

    List<RUserWorkGroupPerm> selectByWorkGroupId(Long workGroupId);

    RUserWorkGroupPerm selectByUserIdAndWorkGroupId(Long userId, Long workGroupId);

    int deleteByUserIdAndWorkGroupId(Long userId, Long workGroupId);
}
