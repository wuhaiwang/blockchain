package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.CostCenterMapper;
import com.seasun.management.mapper.OrderCenterMapper;
import com.seasun.management.mapper.UserTransferPostMapper;
import com.seasun.management.model.CostCenter;
import com.seasun.management.model.OrderCenter;
import com.seasun.management.service.UserTransferPosService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.UserTransferPosSyncVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserTransferPosServiceImpl extends AbstractSyncService implements UserTransferPosService {

    private static final Logger logger = LoggerFactory.getLogger(UserTransferPosServiceImpl.class);

    @Autowired
    UserTransferPostMapper userTransferPostMapper;

    @Autowired
    CostCenterMapper costCenterMapper;

    @Autowired
    OrderCenterMapper orderCenterMapper;

    @Override
    void sync(BaseSyncVo baseSyncVo) throws Exception {
        UserTransferPosSyncVo userTransferPosSyncVo = (UserTransferPosSyncVo) baseSyncVo;
        UserTransferPosSyncVo.UserTransferPosInfo data = userTransferPosSyncVo.getData();

        // 更新 costCenterId
        if (StringUtils.hasText(data.getPreCostCenterCode())) {
            CostCenter costCenter = costCenterMapper.selectByCode(data.getPreCostCenterCode());
            if (null == costCenter) {
                throw new ParamException("costCenterCode do not exitst...");
            }
            data.setPreCostCenterId(costCenter.getId());
        }
        // 更新 orderCenterId
        if (StringUtils.hasText(data.getPreOrderCenterCode())) {
            OrderCenter orderCenter = orderCenterMapper.selectByCode(data.getPreOrderCenterCode());
            if (null == orderCenter) {
                throw new ParamException("orderCenterCode do not exitst...");
            }
            data.setPreOrderCenterId(orderCenter.getId());
        }
        // 更新 costCenterId
        if (StringUtils.hasText(data.getNewCostCenterCode())) {
            CostCenter costCenter = costCenterMapper.selectByCode(data.getNewCostCenterCode());
            if (null == costCenter) {
                throw new ParamException("costCenterCode do not exitst...");
            }
            data.setNewCostCenterId(costCenter.getId());
        }
        // 更新 orderCenterId
        if (StringUtils.hasText(data.getNewOrderCenterCode())) {
            OrderCenter orderCenter = orderCenterMapper.selectByCode(data.getNewOrderCenterCode());
            if (null == orderCenter) {
                throw new ParamException("orderCenterCode do not exitst...");
            }
            data.setNewOrderCenterId(orderCenter.getId());
        }

        if (userTransferPosSyncVo.getType().equals(SyncType.add)) {
            userTransferPostMapper.insertSelective(data);
        } else if (userTransferPosSyncVo.getType().equals(SyncType.update)) {
            userTransferPostMapper.updateByUserIdAndTransferTime(data);
        }
    }
}
