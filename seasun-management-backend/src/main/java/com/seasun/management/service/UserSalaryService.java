package com.seasun.management.service;

import com.seasun.management.model.UserSalary;
import org.springframework.web.multipart.MultipartFile;


public interface UserSalaryService {

    void importUserSalary(MultipartFile file);

    UserSalary getUserSalaryByUserId(Long userId);
}
