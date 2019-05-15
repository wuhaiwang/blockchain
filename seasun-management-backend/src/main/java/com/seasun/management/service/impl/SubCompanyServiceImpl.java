package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.mapper.CostCenterMapper;
import com.seasun.management.mapper.SubcompanyMapper;
import com.seasun.management.model.CostCenter;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Subcompany;
import com.seasun.management.service.SubCompanyService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.SubCompanySyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCompanyServiceImpl extends AbstractSyncService implements SubCompanyService {

    @Autowired
    SubcompanyMapper subcompanyMapper;

    @Autowired
    private CostCenterMapper costCenterMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof SubCompanySyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 SubCompanySyncVo 类");
        }
        SubCompanySyncVo subCompanySyncVo = (SubCompanySyncVo) baseSyncVo;
        if (null == subCompanySyncVo.getData().getId()) {
            throw new ParamException("id can not be null");
        }

        if (subCompanySyncVo.getType().equals(SyncType.add)) {
            subcompanyMapper.insertWithId(subCompanySyncVo.getData());
        } else if (subCompanySyncVo.getType().equals(SyncType.update)) {
            subcompanyMapper.updateByPrimaryKey(subCompanySyncVo.getData());
        }
    }

    @Override
    public List<Subcompany> getSubcompanys() {
        return subcompanyMapper.selectAll();
    }

    @Override
    public void updateSubcompany(Subcompany subcompany) {
        int i = subcompanyMapper.updateByPrimaryKeySelective(subcompany);
        if (i < 1) {
            throw new UserInvalidOperateException(ErrorMessage.OPERATION_FAILED);
        }
    }

    @Override
    public void deleteSubcompanyById(Long id) {
        subcompanyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void addSubcompany(Subcompany subcompany) {
        subcompanyMapper.insertSelective(subcompany);
    }

    @Override
    public List<IdNameBaseObject> getSimpleSubcompanys() {
        return subcompanyMapper.selectSimpleAll();
    }

    @Override
    public List<CostCenter> getCostCenters() {
        return costCenterMapper.selectAll();
    }

    @Override
    public void updateCostCenters(CostCenter costCenter) {
        int i = costCenterMapper.updateByPrimaryKeySelective(costCenter);
        if (i < 1) {
            throw new UserInvalidOperateException(ErrorMessage.OPERATION_FAILED);
        }
    }

    @Override
    public void addCostCenters(CostCenter costCenter) {
        costCenterMapper.insertSelective(costCenter);
    }
}
