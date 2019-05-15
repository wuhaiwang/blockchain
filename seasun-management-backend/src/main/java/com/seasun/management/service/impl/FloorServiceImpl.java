package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.dto.FloorDetailDto;
import com.seasun.management.dto.FloorDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.FloorMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.Floor;
import com.seasun.management.model.User;
import com.seasun.management.service.FloorService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.FloorMemberRequestVo;
import com.seasun.management.vo.FloorSyncVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FloorServiceImpl extends AbstractSyncService implements FloorService {

    @Autowired
    FloorMapper floorMapper;

    @Autowired
    UserMapper userMapper;

    @Value("${file.sys.prefix}")
    private String fileSystemDiskPath;

    @Value("${export.excel.path}")
    private String fileExportDiskPath;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof FloorSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 FloorSyncVo 类");
        }
        FloorSyncVo floorSyncVo = (FloorSyncVo) baseSyncVo;
        if (null == floorSyncVo.getData().getId()) {
            throw new ParamException("id can not be empty");
        }

        if (floorSyncVo.getType().equals(SyncType.add)) {
            floorMapper.insertWithId(floorSyncVo.getData());
        } else if (floorSyncVo.getType().equals(SyncType.update)) {
            floorMapper.updateByPrimaryKey(floorSyncVo.getData());
        } else if (floorSyncVo.getType().equals(SyncType.delete)) {
            floorMapper.deleteByPrimaryKey(floorSyncVo.getData().getId());
        }
    }

    @Transactional
    @Override
    public void insertTwo() {
        insert();
    }

    @Override
    public List<FloorDto> selectAll() {
        return floorMapper.selectAll();
    }

    @Override
    public FloorDetailDto findOne(Long id) {
        List<FloorDetailDto.Member> members = floorMapper.selectMemberById(id);
        FloorDetailDto floor = floorMapper.selectByFloorId(id);
        List<FloorDetailDto.Member> memberList  = new ArrayList<>();
        memberList.addAll(members);
        floor.setMembers(memberList);
        return floor;
    }

    @Override
    public void update(Long id, Floor floor) {
        floor.setId(id);
        floorMapper.updateByPrimaryKeySelective(floor);
    }

    @Override
    public Boolean checkMemberExistsInFloor(Long floorId, Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user==null) throw new ParamException("该用户信息不存在");
        Floor floor = floorMapper.selectByPrimaryKey(floorId);
        if (floor==null) throw new ParamException("该楼层信息不存在");

        if (user.getFloorId() != null) {
            throw new ParamException("该用户已经在"+ floorMapper.selectByPrimaryKey(user.getFloorId()).getName() + "中, 确定修改吗?");
        }

        return Boolean.FALSE;
    }

    @Override
    public void updateMemberToFloor(FloorMemberRequestVo vo) {
        User user = new User();
        user.setId(vo.getUserId());
        user.setFloorId(vo.getFloorId());
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public String exportFloorMember(Long floorId) throws IllegalAccessException {
        Floor floor = floorMapper.selectByPrimaryKey(floorId);
        if (floor==null) throw new ParamException("该楼层信息不存在");
        String city = StringUtils.isBlank(floor.getCity()) ? "": floor.getCity() + "-";
        String path = city + floor.getName() + "人员名单.xls";
        ExcelHelper.ExcelBuilder excelBuilder = new ExcelHelper.ExcelBuilder(fileSystemDiskPath + File.separator +fileExportDiskPath + File.separator + path);
        List<FloorDetailDto.Member> members =  floorMapper.selectMemberById(floorId);
        List<String> titles = Arrays.asList("用户名","姓名");
        List<String> exports = Arrays.asList("loginId", "fullName");
        excelBuilder.buildSheetWidth(titles)
                .buildHead(titles)
                .buildBody(exports,members)
                .export();
        return fileExportDiskPath + File.separator + path;
    }


    private void insert() {
        Floor floor1 = new Floor();
        floor1.setName("test1");
        floorMapper.insert(floor1);
        Floor floor2 = new Floor();
        floor2.setName("test2");
        floorMapper.insert(floor2);
    }
}
