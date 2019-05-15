package com.seasun.management.service;

import com.seasun.management.model.Department;
import com.seasun.management.vo.DepartmentVo;

import java.util.List;


public interface DepartmentService {

    List<DepartmentVo> getAllDepartment();

    DepartmentVo getDepartmentById(Long departmentId);

    void addDepartment(DepartmentVo departmentVo);

    void updateDepartment(DepartmentVo departmentVo);

    void disableDepartment(Long departmentId);

    void activeDepartment(Long id);
}
