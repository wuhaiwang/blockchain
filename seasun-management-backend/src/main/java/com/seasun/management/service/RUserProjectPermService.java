package com.seasun.management.service;


import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.model.RUserProjectPerm;
import com.seasun.management.vo.RUserProjectPermListVo;

import java.util.List;

public interface RUserProjectPermService {
    RUserProjectPerm selectRUserProjectPerm(RUserProjectPerm rUserProjectPerm);

    RUserProjectPermListVo selectAllByUserId(Long userId);

    AddRecordResponse insert(RUserProjectPerm record);

    void delete(Long id);
}
