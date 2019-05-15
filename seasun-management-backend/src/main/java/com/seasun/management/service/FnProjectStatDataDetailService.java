package com.seasun.management.service;

import com.seasun.management.model.FnProjectStatDataDetail;
import com.seasun.management.vo.FnProjectStatDataDetailVo;
import com.seasun.management.vo.FnProjectStatDataDetaildataVo;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface FnProjectStatDataDetailService {

    String importFile(MultipartFile file, int year, int month);

    List<FnProjectStatDataDetailVo> getDetailById(Long projectStatDataId);

    List<FnProjectStatDataDetaildataVo> getDetailByCondition(Long projectId,  Long statId,  Integer year,  Integer month);
}
