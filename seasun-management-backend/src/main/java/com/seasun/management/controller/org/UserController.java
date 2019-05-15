package com.seasun.management.controller.org;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.MenuDto;
import com.seasun.management.helper.CfgHelper;
import com.seasun.management.mapper.UserPerformanceMapper;
import com.seasun.management.model.*;
import com.seasun.management.service.*;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    UserService userService;

    @Autowired
    RUserDepartmentPermService rUserDepartmentPermService;

    @Autowired
    RUserProjectPermService rUserProjectPermService;

    @Autowired
    RUserWorkGroupService rUserWorkGroupService;

    @Autowired
    PerformanceFMService performanceFMService;

    @Autowired
    RMenuProjectRolePermService rMenuProjectRolePermService;

    @Autowired
    MenuService menuService;

    @Autowired
    private PsychologicalConsultantService psychologicalConsultantService;

    @Autowired
    private OperateLogService operateLogService;

    // 分摊平台列表(根据用户project_role返回,包含用户与平台之间的关系)
    @RequestMapping(value = "/apis/auth/user-share-project-perm", method = RequestMethod.GET)
    public ResponseEntity<?> getUserShareProjectPerms() {
        logger.info("begin getUserShareProjectPerms...");
        List<UserProjectPermVo> userProjectPerms = userService.getUserShareProjectPerms();
        logger.info("end getUserShareProjectPerms...");
        return ResponseEntity.ok(userProjectPerms);
    }

    @RequestMapping(value = "/apis/auth/users-base-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) Boolean virtualFlag) {
        logger.info("begin getAllUsers...");
        List<UserMiniVo> users = userService.getAllPerformanceUsers();
        logger.info("end getAllUsers...");
        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/apis/auth/user-info-export", method = RequestMethod.GET)
    public ResponseEntity<?> exportAllUsers() {
        logger.info("begin exportAllUsers...");
        String downloadUrl = userService.exportAllUserInfo();
        logger.info("end exportAllUsers...");
        JSONObject result = new JSONObject();
        if (StringUtils.isEmpty(downloadUrl)) {
            result.put("code", ErrorCode.SYSTEM_ERROR);
        } else {
            result.put("code", 0);
            result.put("url", downloadUrl);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/user-psychological-consultation", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPsychologicalConsultationPassword(@RequestParam(required = false) Integer pageSize, @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) String searchKey) {
        logger.info("begin getUserPsychologicalConsultationPassword...");
        UserPsychologicalConsultationVo result = psychologicalConsultantService.getUserPsychologicalConsultationPasswordPage(pageSize, currentPage, searchKey);
        logger.info("end getUserPsychologicalConsultationPassword...");
        if (result == null) {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, "心理咨询密码暂未生成，请联系管理员..."));
        }
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/apis/auth/user", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentUserInfo() {
        logger.info("begin getCurrentUserInfo...");
        User user = userService.getCurrentUserInfo();
        logger.info("end getCurrentUserInfo...");
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value = "/apis/auth/user-photo", method = RequestMethod.GET)
    public ResponseEntity<?> updateUserPhoto() {
        logger.info("begin updateUserPhoto...");
        String result = userService.updateUserPhoto();
        logger.info("end updateUserPhoto...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/r-user-department-perms", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUserDepartmentPerms(@RequestParam(required = false) Long userId) {
        logger.info("begin getAllUserDepartmentPerms...");
        if (null == userId) {
            userId = MyTokenUtils.getCurrentUserId();
        }
        List<RUserDepartmentPermVo> datas = rUserDepartmentPermService.selectByUserId(userId);
        logger.info("end getAllUserDepartmentPerms...");
        return ResponseEntity.ok(datas);
    }

    @RequestMapping(value = "/apis/auth/r-user-department-perm", method = RequestMethod.POST)
    public ResponseEntity<?> addUserDepartmentPerm(@RequestBody RUserDepartmentPerm record) {
        logger.info("begin addUserDepartmentPerm...");
        rUserDepartmentPermService.insert(record);
        logger.info("end addUserDepartmentPerm...");
        return ResponseEntity.ok(new AddRecordResponse(0, "success", record.getId()));
    }

    @RequestMapping(value = "/apis/auth/r-user-department-perm/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserDepartmentPerm(@PathVariable Long id) {
        logger.info("begin deleteUserDepartmentPerm...");
        rUserDepartmentPermService.delete(id);
        logger.info("end deleteUserDepartmentPerm...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/r-user-project-perms", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUserProjectPerms(@RequestParam(required = false) Long userId) {
        logger.info("begin getAllUserProjectPerms...");
        if (null == userId) {
            userId = MyTokenUtils.getCurrentUserId();
        }
        RUserProjectPermListVo data = rUserProjectPermService.selectAllByUserId(userId);
        logger.info("end getAllUserProjectPerms...");
        return ResponseEntity.ok(data);
    }

    @RequestMapping(value = "/apis/auth/r-user-project-perm", method = RequestMethod.POST)
    public ResponseEntity<?> addUserProjectPerm(@RequestBody RUserProjectPerm record) {
        logger.info("begin addUserProjectPerm...");
        AddRecordResponse addRecordResponse = rUserProjectPermService.insert(record);
        logger.info("end addUserProjectPerm...");
        return ResponseEntity.ok(addRecordResponse);
    }

    @RequestMapping(value = "/apis/auth/r-user-project-perm/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserProjectPerm(@PathVariable Long id) {
        logger.info("begin deleteUserProjectPerm...");
        rUserProjectPermService.delete(id);
        logger.info("end deleteUserProjectPerm...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/r-user-workgroup", method = RequestMethod.GET)
    public ResponseEntity<?> selectByUserIdAndWorkGroupId(@RequestParam(required = false) Long userId, @RequestParam Long workGroupId) {
        if (null == userId) {
            logger.info("begin selectByUserIdAndWorkGroupId...");
            List<RUserWorkGroupPerm> rUserWorkGroupPerms = rUserWorkGroupService.selectByWorkGroupId(workGroupId);
            logger.info("end selectByUserIdAndWorkGroupId...");
            return ResponseEntity.ok(rUserWorkGroupPerms);
        } else {
            logger.info("begin selectByUserIdAndWorkGroupId...");
            RUserWorkGroupPerm rUserWorkGroupPerms = rUserWorkGroupService.selectByUserIdAndWorkGroupId(userId, workGroupId);
            logger.info("end selectByUserIdAndWorkGroupId...");
            return ResponseEntity.ok(rUserWorkGroupPerms);
        }
    }


    @RequestMapping(value = "/apis/auth/user-state-permission", method = RequestMethod.GET)
    public ResponseEntity<?> checkUserPermission(@RequestParam String state) {
        logger.info("begin checkUserPermission...");
        if (!userService.hasPermission(state)) {
            return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_NO_PERMISSION_ERROR, "state is not permit"));
        }

        operateLogService.addStateAccessLog(state, MyTokenUtils.getCurrentUserId());
        logger.info("end checkUserPermission...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/user-state-permissions", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPermissionList() {
        logger.info("begin getUserPermissionList...");
        UserPermissionsVo perms = userService.getPermissionList();
        logger.info("end getUserPermissionList...");
        return ResponseEntity.ok(perms);
    }

    @RequestMapping(value = "/apis/auth/r-fix-project-perm", method = RequestMethod.POST)
    public ResponseEntity<?> addFixProjectPerm(@RequestBody FixMemberAddInfoVo vo) {
        logger.info("begin addFixProjectPerm...");
        CommonResponse commonResponse = performanceFMService.addFixProjectPerm(vo.getGroupId(), vo.getUserId(), vo.getType());
        logger.info("end addFixProjectPerm...");
        return ResponseEntity.ok(commonResponse);
    }

    @RequestMapping(value = "/apis/auth/r-fix-project-perm", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFixProjectPerm(@RequestParam Long id, @RequestParam String type) {
        logger.info("begin addFixProjectPerm...");
        performanceFMService.deleteFixProjectPerm(id, type);
        logger.info("end addFixProjectPerm...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/user-couples", method = RequestMethod.GET)
    public ResponseEntity<?> getUserCouples() {
        logger.info("begin getUserCouples...");
        List<List<SimUserCoupleInfoVo>> userCouples = userService.getUserCouples();
        logger.info("end getUserCouples...");
        return ResponseEntity.ok(new CommonAppResponse(0, userCouples));
    }

    @RequestMapping(value = "/apis/auth/user-couple", method = RequestMethod.POST)
    public ResponseEntity<?> setUserCouple(@RequestBody JSONObject jsonObject) {
        logger.info("begin setUserCouple...");
        userService.setUserCouple(jsonObject.getLong("maleId"), jsonObject.getLong("femaleId"));
        logger.info("end setUserCouple...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/user-couple", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserCouple(@RequestParam Long maleId, @RequestParam Long femaleId) {
        logger.info("begin deleteUserCouple...");
        userService.deleteUserCouple(maleId, femaleId);
        logger.info("end deleteUserCouple...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/user-couple-data-import", method = RequestMethod.POST)
    public ResponseEntity<?> importUserCoupleData(MultipartFile file) {
        logger.info("begin importUserCoupleData...");
        List<String> result = userService.importUserCoupleData(file);
        logger.info("end importUserCoupleData...");
        if (result.isEmpty()) {
            return ResponseEntity.ok(new CommonAppResponse(0, "success"));
        } else {
            return ResponseEntity.ok(new CommonAppResponse(ErrorCode.PARAM_ERROR, result));
        }
    }
    @RequestMapping(value = "/apis/auth/r-menu-project-perm/{projectRoleId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProjectRoleMenuList (@PathVariable Long projectRoleId) {
        return ResponseEntity.ok(rMenuProjectRolePermService.getListByProjectRoleId(projectRoleId));
    }

    @RequestMapping(value = "/apis/auth/r-menu-project-perm/{projectRoleId}/menu", method = RequestMethod.POST)
    public ResponseEntity<?> addProjectRoleMenu (@PathVariable ("projectRoleId") Long projectRoleId, @RequestBody RequestRoleProjectPermVo requestRoleProjectPermVo) {
        return ResponseEntity.ok(rMenuProjectRolePermService.insertProjectRoleMenu(projectRoleId, requestRoleProjectPermVo.getMenuIds()));
    }

    @RequestMapping(value = "/apis/auth/r-menu-project-perm/{projectRoleId}/menu", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProjectRoleMenu (@PathVariable ("projectRoleId") Long projectRoleId, @RequestParam ("menuId") Long menuId) {
        return ResponseEntity.ok(CommonResponse.buildOperationResponse(rMenuProjectRolePermService.deleteProjectRoleMenu(projectRoleId, menuId), ErrorCode.PARAM_ERROR,  "删除失败"));
    }

    @RequestMapping(value = "/apis/auth/menus", method = RequestMethod.GET)
    public ResponseEntity<?> getMenus () {
        return ResponseEntity.ok(menuService.findAll());
    }

    @RequestMapping(value = "/apis/auth/menus", method = RequestMethod.POST)
    public ResponseEntity<?> insertMenus (@RequestBody MenuDto menuDto) {
        return ResponseEntity.ok(menuService.insertOne(menuDto));
    }

    @RequestMapping(value = "/apis/auth/employees/base-info", method = RequestMethod.GET)
    public ResponseEntity<?> getBaseInfo () {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getBaseInfoVo()));
    }

    @RequestMapping(value = "/apis/auth/employees", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfoVoByCondition (UserBaseInfoRequestVo baseInfoRequestVo) {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserInfoVoByCondition(baseInfoRequestVo)));
    }

    @RequestMapping(value = "/apis/auth/employees-base-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserDetailBaseInfo (@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserDetailInfo(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-work-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserWorkInfo (@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserWorkInfo(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-child-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserChildInfo (@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserChildrenInfo(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-nda-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserNdaInfo (@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserNdaInfo(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-work-experience-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserWorkExperienceInfo (@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserWorkExperienceInfo(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-edu-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserEduExperienceInfo (@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserEduExperienceInfo(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-certificate-info/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserCertification (@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.getUserCertification(userId)));
    }

    @RequestMapping(value = "/apis/auth/employees-export", method = RequestMethod.POST)
    public ResponseEntity<?> exportUserBaseInfo (@RequestBody UserBaseInfoRequestVo userBaseInfoRequestVo) throws NoSuchFieldException, IllegalAccessException {
        return ResponseEntity.ok(new CommonAppResponse(0, userService.exportUserInfo(userBaseInfoRequestVo)));
    }



}
