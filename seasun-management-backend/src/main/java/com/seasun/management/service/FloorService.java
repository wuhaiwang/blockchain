package com.seasun.management.service;

import com.seasun.management.dto.FloorDetailDto;
import com.seasun.management.dto.FloorDto;
import com.seasun.management.model.Floor;
import com.seasun.management.vo.FloorMemberRequestVo;

import java.util.List;

public interface FloorService {

    void insertTwo();

    List<FloorDto> selectAll();

    FloorDetailDto findOne(Long id);

    void update(Long id, Floor floor);

    Boolean checkMemberExistsInFloor(Long floorId, Long userId);

    void updateMemberToFloor(FloorMemberRequestVo vo);

    String exportFloorMember(Long floorId) throws IllegalAccessException;
}
