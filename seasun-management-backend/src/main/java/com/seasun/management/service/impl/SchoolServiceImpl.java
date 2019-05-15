package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.SchoolMapper;
import com.seasun.management.model.School;
import com.seasun.management.service.SchoolService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.SchoolSyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolServiceImpl extends AbstractSyncService implements SchoolService {
    @Autowired
    private SchoolMapper schoolMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof SchoolSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 SchoolSyncVo 类");
        }
        SchoolSyncVo schoolSyncVo = (SchoolSyncVo) baseSyncVo;
        if (null == schoolSyncVo.getData().getId()) {
            throw new ParamException("id can not be empty");
        }

        if (schoolSyncVo.getType().equals(SyncType.add)) {
            schoolMapper.insertWithId(schoolSyncVo.getData());
        } else if (schoolSyncVo.getType().equals(SyncType.update)) {
            schoolMapper.updateByPrimaryKey(schoolSyncVo.getData());
        } else if (schoolSyncVo.getType().equals(SyncType.delete)) {
            schoolMapper.deleteByPrimaryKey(schoolSyncVo.getData().getId());
        }
    }


    @Cacheable(value = "schoolService")
    @Override
    public List<School> selectAll() {
        return schoolMapper.selectAll();
    }

    @Override
    public School selectFixed1000() {
        return schoolMapper.selectByPrimaryKey(1000L);
    }
}
