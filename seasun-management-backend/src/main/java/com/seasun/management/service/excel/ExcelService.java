package com.seasun.management.service.excel;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

    void importExcel(MultipartFile file, String date, String type);
}
