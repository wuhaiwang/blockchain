package com.seasun.management.service.kingsoftLife;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.UserDormitoryParamDto;
import com.seasun.management.model.DormPayment;
import com.seasun.management.vo.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.web.multipart.MultipartFile;


public interface KsDormitoryService {
    public ResponseEntity<?> getDormitory(KsDormitoryVo ksDormitoryVo);
    public ResponseEntity<?> addDormitoryReserve(KsDormitoryReserveVo ksDormitoryReserveVo) throws Exception;
    public ResponseEntity<?> getDormitoryReserveInfo(KsDormitoryReserveVo ksDormitoryReserveVo);
    public ResponseEntity<?> deleteDormitoryReserve(Long id,String liveNo);
    ResponseEntity<?> exportDormitoryReserveInfo(KsDormitoryReserveVo ksDormitoryReserveVo, LinkedHashSet<String> fields) throws IOException;
    UserMiniVo getDormitoryUserInfo(String loginId)throws Exception;

    BaseQueryResponseVo getDormitoryReserveFinanceInfo(Integer year, Integer month, Integer currentPage, Integer pageSize) throws Exception;

    void confirmUserDormPayInfo(DormPayment userDormPayInfo);

    List<String> getKsDormitoryReserveVoDeclareFieldsValue (List<String> filterFields, Object model) throws IOException;

    String exportExcel (LinkedHashSet<String> filterFields, List<KsDormitoryReserveVo> models) throws IOException;

    void updateNonCheckInOrNonCheckOutOrNonUserId ();

    BaseQueryResponseVo getDormitoryReserveInfoByLiveNos (List<String> liveNos, Integer pageSize);

    Future<BaseQueryResponseVo> getKsDormitoryReserveVoAsync(List<String> liveNos, Integer pageSize);

    public List<UserDormitoryParamDto> getUserDormitoryParam(Integer year,Integer month);
    public boolean saveOrUpdateUserDormitoryParam(List<UserDormitoryParamDto> list);
    public int deleteUserDormitoryParam(Long id);

    public String userDormitoryReport(Integer year,Integer month);
    CommonResponse importDormitoryReserve(MultipartFile multipartFile) throws Exception;
    public KsLifeCommonVo checkDormitoryStatus(KsDormitoryReserveVo ksDormitoryReserveVo);

    KsDormitoryChangeRepVo getDormitoryChangeInfo(Integer page, Integer pageSize, String name);
}
