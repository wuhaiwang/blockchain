package com.seasun.management.service.kingsoftLife;

import com.seasun.management.controller.response.CommonResponse;

public interface KsLocationService {

    CommonResponse getAllBuildingImags();

    CommonResponse searchBuildingByCondition(String buildingCode, String floor, String keyWord, String pageNo, String pageSize, String loginId);

    CommonResponse getPositionDetail(Long id, String loginId);
}
