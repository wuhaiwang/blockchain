package com.seasun.management.service.impl;

import com.seasun.management.mapper.RUserDepartmentPermMapper;
import com.seasun.management.model.RUserDepartmentPerm;
import com.seasun.management.service.RUserDepartmentPermService;
import com.seasun.management.vo.RUserDepartmentPermVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RUserDepartmentPermServiceImpl implements RUserDepartmentPermService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private RUserDepartmentPermMapper rUserDepartmentPermMapper;

    @Override
    public void insert(RUserDepartmentPerm record) {
        List<RUserDepartmentPermVo> rUserDepartmentPermVos = selectByUserId(record.getUserId());
        Boolean exists = false;
        for (RUserDepartmentPerm data : rUserDepartmentPermVos) {
            if (data.getDepartmentId().equals(record.getDepartmentId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            rUserDepartmentPermMapper.insert(record);
        }
    }

    @Override
    public void delete(Long id) {
        rUserDepartmentPermMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<RUserDepartmentPermVo> selectByUserId(Long userId) {
        return rUserDepartmentPermMapper.selectByUserId(userId);
    }
}
