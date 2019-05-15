package com.seasun.management.service.impl;

import com.seasun.management.mapper.FnSumShareConfigMapper;
import com.seasun.management.model.FnSumShareConfig;
import com.seasun.management.service.FnSumShareConfigService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.FnSumShareConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FnSumShareConfigServiceImpl implements FnSumShareConfigService {

    @Autowired
    FnSumShareConfigMapper fnSumShareConfigMapper;

    @Override
    public List<FnSumShareConfigVo.ProjectSumPro> saveSumShareConfig(List<FnSumShareConfigVo> fnSumShareConfigVo) {

        // 先删除旧数据
        fnSumShareConfigMapper.deleteByPlatIdAndYearAndMonth(fnSumShareConfigVo.get(0).getPlatId(),
                fnSumShareConfigVo.get(0).getYear(),
                fnSumShareConfigVo.get(0).getMonth());

        Long userId = MyTokenUtils.getCurrentUserId();
        List<FnSumShareConfig> fnSumShareConfigs = new ArrayList<>();
        for (FnSumShareConfigVo sumPro : fnSumShareConfigVo) {
            FnSumShareConfig tempSumConfig = new FnSumShareConfig();
            BeanUtils.copyProperties(sumPro, tempSumConfig);
            tempSumConfig.setCreateTime(new Date());
            tempSumConfig.setUpdateBy(userId);
            tempSumConfig.setSharePro(sumPro.getSharePro());
            fnSumShareConfigs.add(tempSumConfig);
        }
        fnSumShareConfigMapper.batchInsert(fnSumShareConfigs);

        // 返回批量插入后的id.
        List<FnSumShareConfigVo.ProjectSumPro> result = fnSumShareConfigMapper.selectSumShareConfigIdsByPlatIdAndYearAndMonth(
                fnSumShareConfigs.get(0).getPlatId(),
                fnSumShareConfigs.get(0).getYear(),
                fnSumShareConfigs.get(0).getMonth());
        return result;
    }
}
