package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface SqlFileGenerateService {

    JSONObject exportProjectOutsourcingSqlForOldFinanceSystem(MultipartFile file,Integer year, Integer month);

    JSONObject exportProjectOutsourcingSqlForNewITSystem(MultipartFile file,Integer year, Integer month);
}
