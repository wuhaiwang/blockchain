package com.seasun.management.service;

import com.seasun.management.vo.FnSumShareConfigVo;

import java.util.List;

public interface FnSumShareConfigService {

    List<FnSumShareConfigVo.ProjectSumPro> saveSumShareConfig(List<FnSumShareConfigVo> fnSumShareConfigVos);

}
