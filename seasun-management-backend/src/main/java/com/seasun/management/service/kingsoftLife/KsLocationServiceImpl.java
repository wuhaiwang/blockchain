package com.seasun.management.service.kingsoftLife;

import com.alibaba.fastjson.JSON;
import com.seasun.management.common.KsHttpComponent;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.KsBuildingCoordinateVo;
import com.seasun.management.vo.KsBuildingImageVo;
import com.seasun.management.vo.KsLifeCommonVo;
import com.seasun.management.vo.KsPositionDetailVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.seasun.management.util.URLUtil.encodeURIComponent;

@Service
public class KsLocationServiceImpl implements KsLocationService {


    @Value("${ks.life.address}")
    private String address;

    @Override
    public CommonResponse getAllBuildingImags() {

        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String buildingIndex = "/ebuilding/building/index";
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doGetHttpRequest(address + buildingIndex, null, loginId, null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (Boolean.parseBoolean(ksResult.getSuccess())) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            return new CommonResponse(0, "success", JSON.parseObject(decryptedData, KsBuildingImageVo.class));
        } else {
            return new CommonResponse(0, ErrorMessage.KOA_USER_ERROR, null);
        }
    }

    @Override
    public CommonResponse searchBuildingByCondition(String buildingCode, String floor, String keyword, String pageNo, String pageSize, String loginId) {
        if (loginId == null) {
            loginId = MyTokenUtils.getCurrentUser().getLoginId();
        }
        String searchURL = "/ebuilding/buildingFloorCoordinate/search/v2";
        KsHttpComponent component = new KsHttpComponent();
        Map<String, String> urlParamMap = new HashMap<>();
        urlParamMap.put("building", buildingCode);
        urlParamMap.put("floor", floor);
        urlParamMap.put("keyword", encodeURIComponent(keyword));
        urlParamMap.put("pageNo", pageNo);
        urlParamMap.put("pageSize", pageSize);
        String response = component.doGetHttpRequest(address + searchURL, urlParamMap, loginId, null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (Boolean.parseBoolean(ksResult.getSuccess())) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            return new CommonResponse(0, "success", JSON.parseObject(decryptedData, KsBuildingCoordinateVo.class));
        } else {
            return new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, ksResult.getMsg());
        }
    }


    @Override
    public CommonResponse getPositionDetail(Long id, String loginId) {
        if (loginId == null) {
            loginId = MyTokenUtils.getCurrentUser().getLoginId();
        }
        String floorDetail = "/ebuilding/buildingFloorCoordinate/detail";
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doGetHttpRequest(address + floorDetail, new HashMap<String, String>() {{
            put("id", String.valueOf(id));
        }}, loginId, null);
        KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);
        if (Boolean.parseBoolean(ksResult.getSuccess())) {
            String decryptedData = (String) component.ksDecryptData(ksResult.getData().toString());
            KsPositionDetailVo data = JSON.parseObject(decryptedData, KsPositionDetailVo.class);
            return new CommonResponse(0, "success", data);
        } else {
            return new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, ksResult.getMsg());
        }

    }


}
