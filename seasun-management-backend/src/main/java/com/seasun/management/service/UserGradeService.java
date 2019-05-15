package com.seasun.management.service;

import com.seasun.management.dto.UserGradeDetailDto;
import com.seasun.management.dto.UserGradeDto;
import com.seasun.management.vo.UserGradeVo;

import java.util.List;

public interface UserGradeService {
    UserGradeVo getSubGradeChange(Long workGroupId);

    List<UserGradeDto> getMemberGradeChange(Long workGroupId);

    UserGradeDetailDto getGradeChangeDetail(Long userId);

    Boolean changeUserGradeInfo(Long userId, Integer year, Integer month, String grade, String evaluateType);
}
