package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.CostCenterMapper;
import com.seasun.management.mapper.DepartmentMapper;
import com.seasun.management.mapper.RUserDepartmentPermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.CostCenter;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.RUserDepartmentPerm;
import com.seasun.management.service.DepartmentService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.DepartmentSyncVo;
import com.seasun.management.vo.DepartmentVo;
import com.seasun.management.vo.UserSelectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentServiceImpl extends AbstractSyncService implements DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    CostCenterMapper costCenterMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RUserDepartmentPermMapper rUserDepartmentPermMapper;


    private void setDepartmentManager(DepartmentVo departmentVo, String userIdsString, List<UserSelectVo> allActiveUserList) {
        if (StringUtils.hasText(userIdsString)) {
            String[] userIds = userIdsString.split(",");

            for (String userId : userIds) {
                for (IdNameBaseObject user : allActiveUserList) {
                    if (user.getId().toString().equals(userId)) {
                        if (departmentVo.getManageUserList() == null) {
                            departmentVo.setManageUserList(new ArrayList<>());
                        }
                        departmentVo.getManageUserList().add(user);
                        break;
                    }
                }
            }
        }
    }

    private void addOrUpdateCostCenter(Long departmentId, String costCenterCodes) {
        if (StringUtils.hasText(costCenterCodes)) {
            String[] codes = costCenterCodes.toUpperCase().split(",");
            Map<String, Object> params = new HashMap<>();
            params.put("departmentId", departmentId);
            params.put("codes", codes);

            costCenterMapper.removeByDepartmentIdAndCodesNotIn(params);

            List<CostCenter> costCenterList = costCenterMapper.selectAll();
            List<CostCenter> costCenterListNeedInsert = new ArrayList<>();
            List<String> costCenterListNeedUpdate = new ArrayList<>();
            for (String code : codes) {
                Boolean aExists = false;
                for (CostCenter costCenter : costCenterList) {
                    if (code.equals(costCenter.getCode())) {
                        aExists = true;

                        //成本编号一样部门不一样的做更新处理
                        if (!departmentId.equals(costCenter.getDepartmentId())) {
                            costCenterListNeedUpdate.add(code);
                        }
                        break;
                    }
                }

                if (!aExists) {
                    CostCenter costCenter = new CostCenter();
                    costCenter.setCode(code);
                    costCenter.setDepartmentId(departmentId);

                    costCenterListNeedInsert.add(costCenter);
                }
            }

            if (costCenterListNeedInsert.size() > 0) {
                costCenterMapper.batchInsert(costCenterListNeedInsert);
            }
            if (costCenterListNeedUpdate.size() > 0) {
                Map<String, Object> paramsUpdate = new HashMap<>();
                paramsUpdate.put("departmentId", departmentId);
                paramsUpdate.put("codes", costCenterListNeedUpdate);

                costCenterMapper.updateDepartmentByCodesIn(paramsUpdate);
            }
        }else{
            costCenterMapper.removeByDepartmentId(departmentId);
        }
    }

    private void addOrUpdateManager(Long departmentId, String managerString) {
        if (StringUtils.hasText(managerString)) {
            String[] managers = managerString.split(",");
            Map<String, Object> params = new HashMap<>();
            params.put("departmentId", departmentId);
            params.put("userIds", managers);

            rUserDepartmentPermMapper.deleteByDepartmentIdAndUserIdsNotIn(params);

            List<RUserDepartmentPerm> rUserDepartmentPermList = rUserDepartmentPermMapper.selectByDepartmentId(departmentId);
            List<RUserDepartmentPerm> rUserDepartmentPermListNeedInsert = new ArrayList<>();
            for (String userId : managers) {
                Boolean aExists = false;
                for (RUserDepartmentPerm rUserDepartmentPerm : rUserDepartmentPermList) {
                    if (rUserDepartmentPerm.getUserId().toString().equals(userId)) {
                        aExists = true;
                        break;
                    }
                }

                if (!aExists) {
                    RUserDepartmentPerm rUserDepartmentPerm = new RUserDepartmentPerm();
                    rUserDepartmentPerm.setDepartmentId(departmentId);
                    rUserDepartmentPerm.setUserId(Long.parseLong(userId));

                    rUserDepartmentPermListNeedInsert.add((rUserDepartmentPerm));
                }
            }

            if (rUserDepartmentPermListNeedInsert.size() > 0) {
                rUserDepartmentPermMapper.batchInsert(rUserDepartmentPermListNeedInsert);
            }
        }else{
            rUserDepartmentPermMapper.deleteByDepartmentId(departmentId);
        }
    }

    @Override
    public List<DepartmentVo> getAllDepartment() {
        List<DepartmentVo> departmentVos = departmentMapper.selectAllWithCostCodesStr();

        List<UserSelectVo> allActiveUserList = userMapper.selectAllActiveUserSelectVo();

        for (DepartmentVo departmentVo : departmentVos) {
            setDepartmentManager(departmentVo, departmentVo.getManagerIds(), allActiveUserList);
        }
        return departmentVos;
    }

    @Override
    public DepartmentVo getDepartmentById(Long departmentId) {
        DepartmentVo departmentVo = departmentMapper.selectWithCostCodesStrByPrimaryKey(departmentId);

        List<UserSelectVo> allActiveUserList = userMapper.selectAllActiveUserSelectVo();

        setDepartmentManager(departmentVo, departmentVo.getManagerIds(), allActiveUserList);

        return departmentVo;
    }

    @Override
    public void addDepartment(DepartmentVo departmentVo) {
        departmentMapper.insertSelective(departmentVo);

        //处理成本中心编码
        addOrUpdateCostCenter(departmentVo.getId(), departmentVo.getCostCenterCodesStr());

        //处理负责人
        addOrUpdateManager(departmentVo.getId(), departmentVo.getManagerIds());
    }

    @Override
    public void updateDepartment(DepartmentVo departmentVo) {
        departmentMapper.updateByPrimaryKeySelective(departmentVo);

        //处理成本中心编码
        addOrUpdateCostCenter(departmentVo.getId(), departmentVo.getCostCenterCodesStr());

        //处理负责人
        addOrUpdateManager(departmentVo.getId(), departmentVo.getManagerIds());
    }

    @Override
    public void disableDepartment(Long departmentId) {
        departmentMapper.disableByPrimaryKey(departmentId);

        //处理成本中心编码
        //costCenterMapper.removeByDepartmentId(departmentId);

        //处理负责人
        //rUserDepartmentPermMapper.deleteByDepartmentId(departmentId);
    }

    @Override
    public void activeDepartment(Long departmentId) {
        departmentMapper.activeByPrimaryKey(departmentId);
    }

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof DepartmentSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 DepartmentSyncVo 类");
        }
        DepartmentSyncVo departmentSyncVo = (DepartmentSyncVo) baseSyncVo;
        if (null == departmentSyncVo.getData().getId()) {
            logger.info("syncDepartment failed...");
            throw new ParamException("departmentId can not be null...");
        }

        // 更新department
        if (departmentSyncVo.getType().equals(SyncType.add)) {
            departmentMapper.insertSelectiveWithId(departmentSyncVo.getData());

            //处理成本中心编码
            addOrUpdateCostCenter(departmentSyncVo.getData().getId(), departmentSyncVo.getData().getCostCenterCode());

            //处理负责人
            addOrUpdateManager(departmentSyncVo.getData().getId(), departmentSyncVo.getData().getAssistantId().toString());
        } else if (departmentSyncVo.getType().equals(SyncType.update)) {
            departmentMapper.updateByPrimaryKeySelective(departmentSyncVo.getData());

            //处理成本中心编码
            addOrUpdateCostCenter(departmentSyncVo.getData().getId(), departmentSyncVo.getData().getCostCenterCode());

            //处理负责人
            addOrUpdateManager(departmentSyncVo.getData().getId(), departmentSyncVo.getData().getAssistantId().toString());
        } else if (departmentSyncVo.getType().equals(SyncType.delete)) {
            departmentMapper.disableByPrimaryKey(departmentSyncVo.getData().getId());

            //处理成本中心编码
            costCenterMapper.removeByDepartmentId(departmentSyncVo.getData().getId());

            //处理负责人
            rUserDepartmentPermMapper.deleteByDepartmentId(departmentSyncVo.getData().getId());
        }
    }

}

