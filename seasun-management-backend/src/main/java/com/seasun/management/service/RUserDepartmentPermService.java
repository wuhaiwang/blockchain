package com.seasun.management.service;

import com.seasun.management.model.RUserDepartmentPerm;
import com.seasun.management.vo.RUserDepartmentPermVo;

import java.util.List;

public interface RUserDepartmentPermService {
    List<RUserDepartmentPermVo> selectByUserId(Long userId);

    void insert(RUserDepartmentPerm record);

    void delete(Long id);
}
