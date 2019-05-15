package com.seasun.management.service;

import com.seasun.management.model.FnTask;

import java.util.List;

public interface FnTaskService {

    List<FnTask> getTaskByType(String type);
}
