package com.seasun.management.service.impl;

import com.seasun.management.mapper.FnTaskMapper;
import com.seasun.management.model.FnTask;
import com.seasun.management.service.FnTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskServiceImpl implements FnTaskService {


    @Autowired
    FnTaskMapper fnTaskMapper;

    private static final int MAX_TASK_RECORD = 200;

    @Override
    public List<FnTask> getTaskByType(String type) {
        if (FnTask.Type.all.equals(type)) {
            return fnTaskMapper.selectAllOrderByIdDescAndLimitBy(MAX_TASK_RECORD);
        }
        return fnTaskMapper.selectByType(type);
    }
}
