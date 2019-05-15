package com.seasun.management.service.app;

import com.seasun.management.vo.HomeInfoAppVo;

import java.util.List;

public interface PermissionService {

    HomeInfoAppVo getAppHomeInfo();

    List<String> getAppPermissionList(String module);

}
