package com.seasun.management.service;

import com.seasun.management.model.School;

import java.util.List;

public interface SchoolService {

    List<School> selectAll();

    School selectFixed1000();
}
