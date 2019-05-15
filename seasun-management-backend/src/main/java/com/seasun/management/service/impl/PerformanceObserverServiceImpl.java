package com.seasun.management.service.impl;

import com.seasun.management.mapper.PerformanceObserverMapper;
import com.seasun.management.mapper.RUserPerformancePermMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.model.User;
import com.seasun.management.service.PerformanceObserverService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.PerformanceObserverVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceObserverServiceImpl implements PerformanceObserverService {

    @Autowired
    PerformanceObserverMapper performanceObserverMapper;

    @Autowired
    RUserPerformancePermMapper rUserPerformancePermMapper;

    @Override
    public List<PerformanceObserverVo> getObserverList() {
        User user = MyTokenUtils.getCurrentUser();
        List<PerformanceObserverVo> performanceObservers = new ArrayList<>();
        List<PerformanceObserverVo> observerUserList = performanceObserverMapper.selectAllByObserverId(user.getId());
        List<PerformanceObserverVo> observerGroupList = rUserPerformancePermMapper.selectAllByUserIdAndObserver(user.getId());
        if (!observerUserList.isEmpty()) {
            performanceObservers.addAll(observerUserList);
        }
        if (!observerGroupList.isEmpty()) {
            performanceObservers.addAll(observerGroupList);
        }
        return performanceObservers;
    }
}
