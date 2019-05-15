package com.seasun.management.service;

import com.seasun.management.model.PmAttachment;
import com.seasun.management.model.PmFinanceReport;
import org.springframework.web.multipart.MultipartFile;

public interface PmFinanceReportService {

    Long insertSelective(PmFinanceReport pmFinanceReport);

    int updateSelective(Long id, PmFinanceReport pmFinanceReport);

    PmAttachment insertAttachment(Long projectId, Integer year, Integer month, MultipartFile file, String size);

    int deletePmAttachmentByPmAttachmentId(Long id);
}
