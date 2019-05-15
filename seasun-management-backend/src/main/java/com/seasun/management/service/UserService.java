package com.seasun.management.service;

import com.seasun.management.dto.*;
import com.seasun.management.model.*;
import com.seasun.management.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserProjectPermVo> getUserShareProjectPerms();

    List<UserWeekProjectPermVo> getUserShareWeekProjectPerms();

    List<UserMiniVo> getAllPerformanceUsers();

    User getCurrentUserInfo();

    String exportAllUserInfo();

    boolean hasPermission(String state);

    UserPermissionsVo getPermissionList();

    UserInfoAppVo getAppUserInfo(Long userId, Boolean getSeatFlag);

    String updateUserPhoto();

    SimpleUserInfoVo getSimpleUserInfo();

    List<List<SimUserCoupleInfoVo>> getUserCouples();

    void setUserCouple(Long maleId, Long femaleId);

    void deleteUserCouple(Long maleId, Long femaleId);

    List<String> importUserCoupleData(MultipartFile file);

    List<UserGroupManagerVo> findUserGroupManagers(List<Long> userIds, Boolean isNeedFindParent);

    UserInfoVo.BaseInfoVo getBaseInfoVo ();

    BaseQueryResponseVo getUserInfoVoByCondition (UserBaseInfoRequestVo requestVo);

    UserDetailBaseInfoDto getUserDetailInfo(Long userId) throws Exception;

    UserWorkInfoDto getUserWorkInfo(Long userId) throws Exception;

    List<UserChildrenInfoDto> getUserChildrenInfo (Long userId);

    List<UserNdaDto> getUserNdaInfo (Long userId);

    List<UserWorkExperienceDto> getUserWorkExperienceInfo (Long userId);

    List<UserEduExperienceDto> getUserEduExperienceInfo (Long userId);

    List<UserCertificationDto> getUserCertification (Long userId);

    String exportUserInfo (UserBaseInfoRequestVo userBaseInfoRequestVo) throws IllegalAccessException;

}
