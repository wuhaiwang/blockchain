package com.seasun.management.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.dto.*;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.MenuService;
import com.seasun.management.service.cache.CacheService;
import com.seasun.management.service.kingsoftLife.KsUserService;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.service.UserService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceTreeHelper;
import com.seasun.management.service.permission.ConstantPermCollector;
import com.seasun.management.service.permission.FixMemberPermCollector;
import com.seasun.management.service.permission.PerformanceDataPermCollector;
import com.seasun.management.service.permission.PerformanceManagerPermCollector;
import com.seasun.management.service.permission.RUserProjectPermCollector;
import com.seasun.management.vo.UserDetailSyncVo.UserDetailInfo;
import com.seasun.management.vo.UserSyncVo.DataVo;
import com.seasun.management.vo.UserSyncVo.UserInfo;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl extends AbstractSyncService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final Map<String, String> titleValues = new LinkedHashMap<>();

    static {
        titleValues.put("loginId","用户名");
        titleValues.put( "fullName","姓名");
        titleValues.put( "gender","性别");
        titleValues.put( "certificateType","证件类型");
        titleValues.put( "certificationNo","证件号码");
        titleValues.put( "birthday","出生日期");
        titleValues.put( "age","年龄");
        titleValues.put( "politicalStatus","政治面貌");
        titleValues.put( "nationName","国籍");
        titleValues.put( "nativePlace","籍贯");
        titleValues.put( "householdType","户籍性质");
        titleValues.put("householdPlace","户口所在地");
        titleValues.put( "nationality","民族");
        titleValues.put( "graduateFrom","毕业院校");
        titleValues.put( "graduateDate","毕业时间");
        titleValues.put( "major","所学专业");
        titleValues.put( "topEducation","最高学历");
        titleValues.put( "employeeNo","员工编号");
        titleValues.put( "workStatus","员工状态");
        titleValues.put( "post","员工岗位");
        titleValues.put( "firstJoinDate","首次加入金山日期");
        titleValues.put( "inDate","入职日期");
        titleValues.put( "topDegree","最高学位");
        titleValues.put( "englishLevel","英语水平");
        titleValues.put( "otherLanguageLevel","其他外语及水平");
        titleValues.put( "department","部门");
        titleValues.put( "workGroup","工作组");
        titleValues.put( "joinPostDate","到岗日期");
        titleValues.put( "becomeValidDate","转正日期");
        titleValues.put( "leaveDate","离职日期");
        titleValues.put( "workAge","工作年限");
        titleValues.put( "workAgeInKs","司龄");
        titleValues.put( "email","公司邮箱");
        titleValues.put( "encouragement","获得奖励");
        titleValues.put( "phone","手机号码");
        titleValues.put( "extraPhone","分机号码");
        titleValues.put( "qq","QQ号码");
        titleValues.put( "msn","MSN");
        titleValues.put( "fax","传真");
        titleValues.put( "personalEmail","私人邮箱");
        titleValues.put( "marryFlag","婚姻状况");
        titleValues.put( "homeAddress","家庭住址");
        titleValues.put( "currentAddress","现住址");
        titleValues.put( "postCode","现住址邮");
        titleValues.put( "emgContactPerson","紧急联系人");
        titleValues.put( "emgContactPersonTel","紧急联系人电话");
        titleValues.put( "hobbies","个人爱好");
        titleValues.put( "specialSkill","个人特长");
        titleValues.put( "postAddress","邮寄地址");
        titleValues.put( "subcompany","所属公司");
        titleValues.put( "belongToCenter","所属中心");
        titleValues.put( "costCenterCode","成本中心");
        titleValues.put( "project","项目");
        titleValues.put( "orderCenterCode","订单");

    }


    @Value("${file.sys.prefix}")
    private String fileSystemDiskPath;

    @Value("${export.excel.path}")
    private String fileExportDiskPath;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CfgPlatAttrMapper cfgPlatAttrMapper;

    @Autowired
    UserDetailMapper userDetailMapper;

    @Autowired
    CostCenterMapper costCenterMapper;

    @Autowired
    UserChildrenInfoMapper userChildrenInfoMapper;

    @Autowired
    UserEduExperienceMapper userEduExperienceMapper;

    @Autowired
    UserNdaMapper userNdaMapper;

    @Autowired
    UserBankCardMapper userBankCardMapper;

    @Autowired
    UserCertificationMapper userCertificationMapper;

    @Autowired
    UserWorkExperienceMapper userWorkExperienceMapper;

    @Autowired
    UserFavoriteMapper userFavoriteMapper;

    @Autowired
    OrderCenterMapper orderCenterMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    FnPlatShareMemberMapper fnPlatShareMemberMapper;

    @Autowired
    UserHideThePhoneMapper userHideThePhoneMapper;

    @Autowired
    private WorkGroupService workGroupService;

    @Autowired
    RUserProjectPermCollector rUserProjectPermCollector;

    @Autowired
    PerformanceManagerPermCollector performanceManagerPermCollector;

    @Autowired
    PerformanceDataPermCollector performanceDataPermCollector;

    @Autowired
    ConstantPermCollector normalUserPermCollector;

    @Autowired
    FixMemberPermCollector fixMemberPermCollector;

    @Autowired
    WorkGroupMapper workGroupMapper;


    @Autowired
    private UserContactGroupMapper userContactGroupMapper;

    @Value("${user.icon.url}")
    private String userPhotoUrl;

    @Autowired
    private PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    private KsUserService ksUserService;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CfgSystemParamMapper cfgSystemParamMapper;

    @Autowired
    private UserDormitoryReservationMapper userDormitoryReservationMapper;

    @Autowired
    SubcompanyMapper subcompanyMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    MenuService menuService;

    @Override
    public void sync(BaseSyncVo baseSyncVo) throws Exception {
        if (!(baseSyncVo instanceof UserSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 UserSyncVo 类");
        }
        UserSyncVo userSyncVo = (UserSyncVo) baseSyncVo;
        DataVo data = userSyncVo.getData();
        UserInfo userInfo = data.getBaseInfo();
        UserDetailInfo userDetailInfo = data.getUserDetail();

        if (null == userInfo.getId()) {
            throw new ParamException("id can not be null...");
        }
        if (null != userDetailInfo && null == userDetailInfo.getUserId()) {
            throw new ParamException("user_id can not be null...");
        }

        // 如果用户已经非激活，则删除token数据
        if (null != userInfo.getActiveFlag() && userInfo.getActiveFlag()) {
            logger.info("target user is active...");
        } else {
            logger.info("target user is not active, will delete token...");
            userTokenMapper.deleteByUserId(userInfo.getId());
        }

        // 更新 costCenterId
        if (null != userInfo.getCostCenterCode() && !userInfo.getCostCenterCode().isEmpty()) {
            CostCenter costCenter = costCenterMapper.selectByCode(userInfo.getCostCenterCode());
            if (null == costCenter) {
                throw new ParamException("costCenterCode do not exist...");
            }
            userInfo.setCostCenterId(costCenter.getId());
        }
        // 更新 orderCenterId
        if (null != userInfo.getOrderCenterCode() && !userInfo.getOrderCenterCode().isEmpty()) {
            OrderCenter orderCenter = orderCenterMapper.selectByCode(userInfo.getOrderCenterCode());
            if (null == orderCenter) {
                throw new ParamException("orderCenterCode do not exist...");
            }
            userInfo.setOrderCenterId(orderCenter.getId());
        }

        //User字段加密
        userInfo.setEmail(MyEncryptorUtils.encryptByAES(userInfo.getEmail()));
        userInfo.setPhone(MyEncryptorUtils.encryptByAES(userInfo.getPhone()));
        //UserDetail字段加密
        if (null != userDetailInfo) {
            userDetailInfo.setMsn(MyEncryptorUtils.encryptByAES(userDetailInfo.getMsn()));
            userDetailInfo.setCertificationNo(MyEncryptorUtils.encryptByAES(userDetailInfo.getCertificationNo()));
            userDetailInfo.setQq(MyEncryptorUtils.encryptByAES(userDetailInfo.getQq()));
            userDetailInfo.setEmgContactPerson(MyEncryptorUtils.encryptByAES(userDetailInfo.getEmgContactPerson()));
            userDetailInfo.setEmgContactPersonRel(MyEncryptorUtils.encryptByAES(userDetailInfo.getEmgContactPersonRel()));
            userDetailInfo.setEmgContactPersonTel(MyEncryptorUtils.encryptByAES(userDetailInfo.getEmgContactPersonTel()));
            userDetailInfo.setPersonalEmail(MyEncryptorUtils.encryptByAES(userDetailInfo.getPersonalEmail()));
            userDetailInfo.setPostAddress(MyEncryptorUtils.encryptByAES(userDetailInfo.getPostAddress()));
            userDetailInfo.setFamilyContactPerson(MyEncryptorUtils.encryptByAES(userDetailInfo.getFamilyContactPerson()));
            userDetailInfo.setFamilyContactPersonPlane(MyEncryptorUtils.encryptByAES(userDetailInfo.getFamilyContactPersonPlane()));
            userDetailInfo.setFamilyContactPersonRel(MyEncryptorUtils.encryptByAES(userDetailInfo.getFamilyContactPersonRel()));
            userDetailInfo.setFamilyContactPersonTel(MyEncryptorUtils.encryptByAES(userDetailInfo.getFamilyContactPersonTel()));
            userDetailInfo.setHomeAddress(MyEncryptorUtils.encryptByAES(userDetailInfo.getHomeAddress()));
            userDetailInfo.setCurrentAddress(MyEncryptorUtils.encryptByAES(userDetailInfo.getCurrentAddress()));
        }

        userInfo.setPhoto(null);//照片字段已经不从DSP同步了
        if (userSyncVo.getType().equals(SyncType.add)) {
            userMapper.insertWithId(userInfo);//因为导入的时候要把user_id带过来，所以用这个方法 2016-12-7 何大海
            if (null != userDetailInfo) {
                userDetailMapper.insertSelective(userDetailInfo);
                // 员工入职后，需要更新其宿舍预定表的信息,回填userId
                if (userDetailInfo.getCertificateType() != null) {
                    userDormitoryReservationMapper.updateUserIdByCertNO(userDetailInfo.getCertificationNo(), userDetailInfo.getUserId());
                }
            }
        } else if (userSyncVo.getType().equals(SyncType.update)) {
            userMapper.updateByPrimaryKeySelective(userInfo);
            if (null != userDetailInfo) {
                UserDetail currentDetail = userDetailMapper.selectByUserId(userDetailInfo.getUserId()); // 判断user_detail是否存在
                if (null == currentDetail) {
                    userDetailMapper.insertSelective(userDetailInfo);
                } else {
                    userDetailMapper.updateByUserIdSelective(userDetailInfo);
                }
            }
        }

    }

    @Override
    public String updateUserPhoto() {
        int nCount = 0, nTotalCount = 0;
        List<UserMiniVo> userAllList = userMapper.selectAllActiveUser(false);

        List<UserMiniDto> needUpdateUserPhotoList = new ArrayList<>();

        String photoPath = fileSystemDiskPath + userPhotoUrl;
        File path = new File(photoPath);
        if (path.exists()) {
            File fileList[] = path.listFiles();
            if (fileList != null) {
                nTotalCount = fileList.length;
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileFullName = file.getName();
                        String userLoginId = fileFullName.split("_")[0];//从文件名中取出用户LoginID
                        UserMiniVo userInfo = userAllList.stream().filter(user -> user.getLoginId().equals(userLoginId)).findFirst().orElse(null);
                        if (null != userInfo) {
                            if (!fileFullName.equals(userInfo.getPhoto())) {
                                UserMiniDto userPhoto = new UserMiniDto();
                                userPhoto.setLoginId(userLoginId);
                                userPhoto.setPhoto(fileFullName);
                                needUpdateUserPhotoList.add(userPhoto);

                                nCount++;
                            }
                        }
                    }
                }
            }
        }

        if (needUpdateUserPhotoList.size() > 0) {
            userMapper.batchUpdateUserPhoto(needUpdateUserPhotoList);
        }

        return nCount + " photo updated of " + nTotalCount;
    }

    @Override
    public SimpleUserInfoVo getSimpleUserInfo() {
        User currentUser = MyTokenUtils.getCurrentUser();
        SimpleUserInfoVo result = new SimpleUserInfoVo();

        BeanUtils.copyProperties(currentUser, result);
        return result;
    }

    @Override
    public List<List<SimUserCoupleInfoVo>> getUserCouples() {
        List<List<SimUserCoupleInfoVo>> result = new ArrayList<>();

        //权限校验
        boolean adminFlag = false;
        List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByUserId(MyTokenUtils.getCurrentUserId());
        List<String> citys = getUserCoupleManagementCity(rUserProjectPerms);
        if (citys.isEmpty()) {
            throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
        }
        if (citys.contains(Project.City.SEASUN)) {
            adminFlag = true;
        }

        List<SimUserCoupleInfoVo> marriedUsers = userMapper.selectAllMarriedUser();

        if (marriedUsers.isEmpty()) {
            return result;
        }

        Map<Long, SimUserCoupleInfoVo> marriedUserByUserIdMap = marriedUsers.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x));
        for (SimUserCoupleInfoVo maleUser : marriedUsers) {

            if (!marriedUserByUserIdMap.containsKey(maleUser.getCoupleUserId())) {
                logger.info("用户：" + maleUser.getUserId() + maleUser.getName() + "的配偶信息异常。");
                continue;
            }

            //女性跳过
            if (UserDetail.Gender.female.equals(maleUser.getGender())) {
                continue;
            }

            SimUserCoupleInfoVo femaleUser = marriedUserByUserIdMap.get(maleUser.getCoupleUserId());
            if (!maleUser.getUserId().equals(femaleUser.getCoupleUserId()) || maleUser.getGender().equals(femaleUser.getGender())) {
                logger.info("用户：" + maleUser.getUserId() + maleUser.getName() + " " + femaleUser.getUserId() + femaleUser.getName() + "的配偶信息异常。");
                continue;
            }

            //过滤非管辖地区人员
            if (!adminFlag) {
                if (!citys.contains(maleUser.getCity()) && !citys.contains(femaleUser.getCity())) {
                    continue;
                }
            }
            //前端需求，男性在前
            List<SimUserCoupleInfoVo> userCouples = new ArrayList<>(2);
            userCouples.add(maleUser);
            userCouples.add(femaleUser);
            result.add(userCouples);
        }

        return result;
    }

    @Override
    public void setUserCouple(Long maleId, Long femaleId) {
        if (maleId == null || femaleId == null) {
            throw new UserInvalidOperateException(ErrorMessage.PARAM_ERROR_MESSAGE);
        }

        //权限校验
        boolean managerFlag = false;
        List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByUserId(MyTokenUtils.getCurrentUserId());
        List<String> citys = getUserCoupleManagementCity(rUserProjectPerms);
        if (citys.isEmpty()) {
            throw new UserInvalidOperateException(ErrorMessage.USER_STATE_PERMISSIONS_ERROR);
        }

        List<Long> userIds = new ArrayList<>(2);
        userIds.add(maleId);
        userIds.add(femaleId);
        List<SimUserCoupleInfoVo> users = userMapper.selectSimUserCoupleInfoVoBYUserIds(userIds);
        if (userIds.size() != 2) {
            throw new UserInvalidOperateException(ErrorMessage.PARAM_ERROR_MESSAGE);
        }

        if (users.get(0).getGender().equals(users.get(1).getGender())) {
            throw new UserInvalidOperateException(ErrorMessage.PARAM_ERROR_MESSAGE);
        }

        for (SimUserCoupleInfoVo user : users) {
            if (user.getCoupleUserId() != null) {
                throw new UserInvalidOperateException("用户：" + user.getName() + " 已有配偶，请重新选择。");
            }
            if (citys.contains(user.getCity()) || citys.contains(Project.City.SEASUN)) {
                managerFlag = true;
            }
            if (user.getUserId().equals(maleId)) {
                user.setCoupleUserId(femaleId);
            } else {
                user.setCoupleUserId(maleId);
            }
        }
        if (!managerFlag) {
            throw new UserInvalidOperateException("添加的用户不在您所管辖的地区，请重新选择。");
        }
        userMapper.batchUpdateUserCouple(users);
    }

    private List<String> getUserCoupleManagementCity(List<RUserProjectPerm> rUserProjectPerms) {
        List<String> citys = new ArrayList<>();

        for (RUserProjectPerm rUserProjectPerm : rUserProjectPerms) {
            if (ProjectRole.Role.admin_id.equals(rUserProjectPerm.getProjectRoleId())) {
                citys.add(Project.City.SEASUN);
                break;
            }
            if (ProjectRole.SEASUN_COUPLE_MANAGER_IDS.contains(rUserProjectPerm.getProjectRoleId())) {
                citys.add(ProjectRole.SEASUN_COUPLE_CITY_BY_PROJECT_ROLE_ID_MAP.get(rUserProjectPerm.getProjectRoleId()));
            }
        }
        return citys;
    }

    @Override
    public void deleteUserCouple(Long maleId, Long femaleId) {
        if (maleId == null || femaleId == null) {
            throw new UserInvalidOperateException(ErrorMessage.PARAM_ERROR_MESSAGE);
        }
        List<Long> userIds = new ArrayList<>(2);
        userIds.add(maleId);
        userIds.add(femaleId);
        List<SimUserCoupleInfoVo> users = userMapper.selectSimUserCoupleInfoVoBYUserIds(userIds);

        if (users.size() != 2) {
            throw new UserInvalidOperateException(ErrorMessage.PARAM_ERROR_MESSAGE);
        }

        //权限校验
        boolean managerFlag = false;
        List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByUserId(MyTokenUtils.getCurrentUserId());
        List<String> citys = getUserCoupleManagementCity(rUserProjectPerms);
        for (SimUserCoupleInfoVo user : users) {
            if (citys.contains(Project.City.SEASUN) || citys.contains(user.getCity())) {
                managerFlag = true;
            }
            user.setCoupleUserId(null);
        }
        if (!managerFlag) {
            throw new UserInvalidOperateException("解除关系的用户不在您所管辖的地区，请重新选择。");
        }
        userMapper.batchUpdateUserCouple(users);
    }

    @Override
    public List<String> importUserCoupleData(MultipartFile file) {
        Workbook wb = null;
        List<Long> employeeNos = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try {
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
        }

        if (null == wb) {
            throw new ParamException("文件解析失败");
        }

        Sheet sheetAt = wb.getSheetAt(0);
        Row firstRow = sheetAt.getRow(0);
        Integer employeeNoCellIndex = null;
        Integer nameCellIndex = null;
        for (int i = 0; i < firstRow.getLastCellNum(); i++) {
            Cell cell = firstRow.getCell(i);
            if (cell == null) {
                continue;
            }
            if ("员工编号".equals(cell.getStringCellValue())) {
                employeeNoCellIndex = i;
            }
            if ("姓名".equals(cell.getStringCellValue())) {
                nameCellIndex = i;
            }
        }

        if (employeeNoCellIndex == null) {
            throw new UserInvalidOperateException("文件中员工编号列未找到，请重新整理。");
        }

        Map<Long, Long> userCoupleMap = new HashMap<>();
        int count;
        if (sheetAt.getLastRowNum() % 2 == 0) {
            count = sheetAt.getLastRowNum();
        } else {
            count = sheetAt.getLastRowNum() - 1;
        }

        for (int i = 1; i <= count; i = i + 2) {
            Row maleRow = sheetAt.getRow(i);
            Row femaleRow = sheetAt.getRow(i + 1);
            if (maleRow == null || femaleRow == null) {
                errorMessages.add("第" + i + "、" + (i + 1) + "行数据异常，已跳过。");
                continue;
            }

            Cell maleEmployeeNoCell = maleRow.getCell(employeeNoCellIndex);
            Cell femaleEmployeeNoCell = femaleRow.getCell(employeeNoCellIndex);
            if (maleEmployeeNoCell == null || femaleEmployeeNoCell == null || MyCellUtils.getCellValue(maleEmployeeNoCell).isEmpty() || MyCellUtils.getCellValue(femaleEmployeeNoCell).isEmpty()) {
                errorMessages.add("第" + i + "、" + (i + 1) + "行，" + maleRow.getCell(nameCellIndex).getStringCellValue() + "," + femaleRow.getCell(nameCellIndex).getStringCellValue() + "的工号数据异常，已跳过。");
                continue;
            }

            employeeNos.add((long) maleEmployeeNoCell.getNumericCellValue());
            employeeNos.add((long) femaleEmployeeNoCell.getNumericCellValue());

            userCoupleMap.put((long) maleEmployeeNoCell.getNumericCellValue(), (long) femaleEmployeeNoCell.getNumericCellValue());
            userCoupleMap.put((long) femaleEmployeeNoCell.getNumericCellValue(), (long) maleEmployeeNoCell.getNumericCellValue());
        }

        try {
            wb.close();
        } catch (IOException e) {
        }

        if (!employeeNos.isEmpty()) {
            List<SimUserCoupleInfoVo> users = userMapper.selectUserIdsByEmployeeNos(employeeNos);
            Map<Long, Long> userIdByEmployeeNoMap = users.stream().collect(Collectors.toMap(x -> x.getEmployeeNo(), x -> x.getUserId()));
            if (!users.isEmpty()) {
                for (SimUserCoupleInfoVo user : users) {
                    user.setCoupleUserId(userIdByEmployeeNoMap.get(userCoupleMap.get(user.getEmployeeNo())));
                }
                userMapper.batchUpdateUserCouple(users);
            }
        }
        return errorMessages;
    }

    private List<User> findUserWorkGroupManagers (Long groupId, Boolean isNeedFindParent) {

//        Long groupId = userMapper.selectByPrimaryKey(userId).getWorkGroupId();
        /**
         * select u.* from r_user_work_group_perm perm join user u on u.id = perm.user_id and perm.work_group_id = #{groupId};
         * */

        List<User> users = userMapper.selectUsersByWorkGroupIdAndRole(groupId, 3l);

        logger.debug ("users -> {}", users);

        if (isNeedFindParent == Boolean.FALSE || !users.isEmpty()) {
            return users;
        }

        /*
        * 已经达到树的根节点了,不需要遍历了
        * */
        if (groupId == 0l) return null;
        /**
         * select * from work_group where id = '#{id}';
         * */
        WorkGroup workGroup = workGroupMapper.selectById(groupId);
        Long parent = workGroup.getParent();
        logger.debug("group_id: {}, parent;{}", groupId, parent);
        return findUserWorkGroupManagers(parent, isNeedFindParent);

    }

    private List<User> findUserWorkGroupManagersInMap (Long groupId, List<WorkGroup> workGroups,Boolean isNeedFindParent) {
        if (groupId == null) return null;
        List<WorkGroup> workGroupList = this.findUserAllWorkGroups(groupId, workGroups, isNeedFindParent);
        if (workGroupList==null || workGroupList.isEmpty()) return null;
        List<Long> workGroupIds = new ArrayList<>();

        workGroupList.stream().forEach(item->{
            workGroupIds.add (item.getId().longValue());
        });
        List<User> users = userMapper.selectUsersInWorkGroupIdAndRole(workGroupIds, PermissionHelper.SystemRole.BACKEND_HR_ORG_ID);
        List<Long> userWorkGroupIds = users.stream().map(user-> user.getWorkGroupId().longValue()).collect(Collectors.toList());
        Long firstGroupId = -1L;

        for (long id: workGroupIds) {
           if (userWorkGroupIds.contains(id)) {
               firstGroupId = id;
               break;
           }
        }

        if (firstGroupId == -1L) return new ArrayList<>();
        final Long firstGroupIdCopy = firstGroupId;
        logger.info ("firstGroupId -> {}", firstGroupId);
        return users.stream().filter(item->
            item.getWorkGroupId().longValue() == firstGroupIdCopy.longValue()
        ).collect(Collectors.toList());

    }

    private List<WorkGroup> findUserAllWorkGroups (Long groupId, List<WorkGroup> workGroupList,Boolean isNeedFindParent) {

        List<WorkGroup> workGroups = new ArrayList<>();

        if (isNeedFindParent == Boolean.FALSE) {
            WorkGroup workGroup = workGroupMapper.selectById(groupId);
            workGroups.add(workGroup);
            return workGroups;
        }

        //List<WorkGroup> workGroupList = workGroupMapper.selectAll();
        Map<Long, WorkGroup> map = new HashMap<>();
        workGroupList.stream().forEach(workGroup -> {
            map.put(workGroup.getId(), workGroup);
        });
        WorkGroup workGroup = map.get(groupId);

        while (workGroup.getParent() != 0L){
            workGroup = map.get (workGroup.getParent());
            workGroups.add (workGroup);
            logger.debug ("parent -> {}", workGroup.getId());
        }
        map.clear();
        return workGroups;
    }

    @Override
    public List<UserGroupManagerVo> findUserGroupManagers(List<Long> userIds, Boolean isNeedFindParent) {
        logger.debug("查询用户所在组, userId => {}", userIds);
        if (userIds==null || userIds.isEmpty()) return new ArrayList<>();
        List<UserGroupManagerDto> userGroupManagerDtoList = userMapper.findUserGroupManagers(userIds);
        if (userGroupManagerDtoList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> userIdList = new ArrayList<>();
        List<UserGroupManagerDto> filterUserGroupManagerDtoList = userGroupManagerDtoList.stream().filter(userGroupManagerDto ->{
            if (userIdList.contains(userGroupManagerDto.getId())) return Boolean.FALSE;
            userIdList.add (userGroupManagerDto.getId());
            return Boolean.TRUE;
        }).collect(Collectors.toList());

        logger.info ("after filter ,userIdList -> {}", userIdList);

        List<UserGroupManagerVo> userGroupManagerVoList = new ArrayList<>();
        List<WorkGroup> workGroupList = workGroupMapper.selectAll();
        filterUserGroupManagerDtoList.stream().forEach(userGroupManagerDto -> {
            UserGroupManagerVo userGroupManagerVo = new UserGroupManagerVo();
            userGroupManagerVo.setId(userGroupManagerDto.getId());
            userGroupManagerVo.setLoginId(userGroupManagerDto.getLoginId());
            userGroupManagerVo.setName(userGroupManagerDto.getName());
            List<UserGroupManagerVo.GroupManagerVo> groupManagerVoList = new ArrayList<>();

            userGroupManagerDtoList.stream().forEach(item -> {

                if (item.getId().longValue()!=userGroupManagerDto.getId().longValue()) {
                    return;
                }

                logger.debug ("item -> {}", item.getId());
                logger.debug ("userGroupManagerDto -> {}", userGroupManagerDto.getId());
                UserGroupManagerVo.GroupManagerVo groupManagerVo = new UserGroupManagerVo.GroupManagerVo();
                if (null == item.getGroupId() || null == item.getGroupManagerId()) {
                    logger.debug("该用户没有所在组");
                    return;
                }
                try {
                    groupManagerVo.setEmail(MyEncryptorUtils.decryptByAES(item.getGroupManagerEmail()));
                    groupManagerVo.setPhone(MyEncryptorUtils.decryptByAES(item.getGroupManagerPhone()));
                } catch (Exception e) {
                    logger.error("解析用户的邮件和手机,err => {}", e.getMessage());
                    groupManagerVo.setEmail("");
                    groupManagerVo.setPhone("");
                }
                groupManagerVo.setUserId(item.getGroupManagerId());
                groupManagerVo.setLoginId(item.getGroupManagerLoginId());
                groupManagerVo.setRoleId(PermissionHelper.SystemRole.BACKEND_HR_ORG_ID);
                groupManagerVo.setRoleName("人力资源部");
                groupManagerVo.setName(item.getGroupManagerName());
                groupManagerVoList.add(groupManagerVo);
            });
            try {
                userGroupManagerVo.setEmail(MyEncryptorUtils.decryptByAES(userGroupManagerDto.getEmail()));
                userGroupManagerVo.setPhone(MyEncryptorUtils.decryptByAES(userGroupManagerDto.getPhone()));
            } catch (Exception e) {
                logger.error("解析用户的邮件和手机,err => {}", e.getMessage());
                userGroupManagerVo.setEmail("");
                userGroupManagerVo.setPhone("");
            }

            if (groupManagerVoList.isEmpty() && isNeedFindParent == Boolean.TRUE) {

                Long groupId = userMapper.selectByPrimaryKey(userGroupManagerDto.getId()).getWorkGroupId();
                List<User> managers = this.findUserWorkGroupManagersInMap(groupId,workGroupList, isNeedFindParent);

                logger.debug ("managers -> {}", managers);

                if (null!=managers && !managers.isEmpty()) {
                    managers.stream().forEach(user->{
                        UserGroupManagerVo.GroupManagerVo groupManagerVo = new UserGroupManagerVo.GroupManagerVo();
                        groupManagerVo.setUserId(user.getId());
                        groupManagerVo.setLoginId(user.getLoginId());
                        groupManagerVo.setRoleId(PermissionHelper.SystemRole.BACKEND_HR_ORG_ID);

                        groupManagerVo.setRoleName("人力资源部");
                        groupManagerVo.setName(user.getName());
                        try {
                            groupManagerVo.setEmail(MyEncryptorUtils.decryptByAES(user.getEmail()));
                            groupManagerVo.setPhone(MyEncryptorUtils.decryptByAES(user.getPhone()));
                        } catch (Exception e) {
                            logger.error("解析用户的邮件和手机,err => {}", e.getMessage());
                            groupManagerVo.setEmail("");
                            groupManagerVo.setPhone("");
                        }
                        groupManagerVoList.add(groupManagerVo);
                    });
                }
            }
            userGroupManagerVo.setGroupManagers(groupManagerVoList);
            userGroupManagerVoList.add (userGroupManagerVo);

        });
        workGroupList.clear();
        return userGroupManagerVoList;
    }


    @Override
    public List<UserProjectPermVo> getUserShareProjectPerms() {
        Long userId = MyTokenUtils.getCurrentUserId();

        UserDetail userDetail = userDetailMapper.selectByUserId(userId);
        if (userDetail != null) {

            if (!("正式".equals(userDetail.getWorkStatus()) || "试用".equals(userDetail.getWorkStatus()))) {
                throw new ParamException("非正式员工（如：实习生）没有分摊填写权限!");
            }

        }

        FnPlatShareMember fnPlatShareMember = fnPlatShareMemberMapper.selectByUserId(userId);
        List<CfgPlatAttrVo> cfgPlatAttrVos = cfgPlatAttrMapper.selectAllWithPlatName(); // 当前所有在填写分摊的平台

        List<UserProjectPermVo> userProjectPermVos = new ArrayList<>();
        List<RUserProjectPermVo> rUserProjectPermVos = rUserProjectPermMapper.selectByUserIdAndOrderByProjectRoleIdAsc(userId);

        CfgPlatAttrVo userPlat = isUserHasShareDetailEditPerm(userId);// 判断该用户是否在人力上有分摊权限
        for (CfgPlatAttrVo sharePlat : cfgPlatAttrVos) {
            // 根据角色表获取分摊权限
            if (null != rUserProjectPermVos && rUserProjectPermVos.size() > 0) {
                for (RUserProjectPermVo rUserProjectPermVo : rUserProjectPermVos) {
                    if (!PermissionHelper.SystemRole.BACKEND_MANAGEMENT_ID.equals(rUserProjectPermVo.getProjectRoleId())
                            && !PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_MANAGER_ID.equals(rUserProjectPermVo.getProjectRoleId())
                            && !PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_PEOPLE_ID.equals(rUserProjectPermVo.getProjectRoleId())
                            && !PermissionHelper.SystemRole.BACKEND_FINANCE_DATA_ID.equals(rUserProjectPermVo.getProjectRoleId())) {
                        continue;
                    }
                    if (null != rUserProjectPermVo.getProjectId() && !sharePlat.getPlatId().equals(rUserProjectPermVo.getProjectId())) {
                        continue;
                    }

                    UserProjectPermVo userProjectPermVo = new UserProjectPermVo();
                    userProjectPermVo.setProjectName(sharePlat.getPlatName());
                    userProjectPermVo.setUserId(userId);
                    userProjectPermVo.setProjectId(sharePlat.getPlatId());
                    userProjectPermVo.setProjectRoleId(rUserProjectPermVo.getProjectRoleId());

                    // 检查该平台是否开始填写分摊明细
                    userProjectPermVo.setShareDetailFlag(sharePlat.getShareDetailFlag());
                    if (sharePlat.getShareDetailFlag() && sharePlat.getPlatId().equals(rUserProjectPermVo.getProjectId())
                            && userPlat != null && userPlat.getPlatId().equals(sharePlat.getPlatId())) {
                        userProjectPermVo.setWeight(1F); // 若该平台开启了分摊明细，且该用户属于该平台，则需要填写分摊
                    }
                    // 没有开启分摊明细填写，但是开启了分摊填写，则走老流程
                    else if (null != fnPlatShareMember && fnPlatShareMember.getPlatId().equals(sharePlat.getPlatId())) {
                        userProjectPermVo.setWeight(fnPlatShareMember.getWeight().floatValue());
                    }

                    // 后台管理员，后台财务数据维护，项目分摊负责人，都有manage权限
                    if (PermissionHelper.SystemRole.BACKEND_MANAGEMENT_ID.equals(rUserProjectPermVo.getProjectRoleId())
                            || PermissionHelper.SystemRole.BACKEND_FINANCE_DATA_ID.equals(rUserProjectPermVo.getProjectRoleId())
                            || PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_MANAGER_ID.equals(rUserProjectPermVo.getProjectRoleId())) {
                        userProjectPermVo.setManagerFlag(true);
                    }
                    // 已处理：rUserProjectPermVos 结果集已经按 project_role_id 升序排列，所以若存在BACKEND_FINANCE_SHARE_MANAGER_ID权限，则先匹配BACKEND_FINANCE_SHARE_MANAGER_ID，
                    // 然后直接返回，而不会匹配到 BACKEND_FINANCE_SHARE_PEOPLE_ID，导致逻辑错误。
                    else if (PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_PEOPLE_ID.equals(rUserProjectPermVo.getProjectRoleId())) {
                        userProjectPermVo.setManagerFlag(false);
                    }

                    // 前台展示，是否开启周分摊填写
                    if (sharePlat.getShareWeekWriteFlag() != null && sharePlat.getShareWeekWriteFlag()) {
                        userProjectPermVo.setShareWeekWriteFlag(true);
                    }

                    userProjectPermVos.add(userProjectPermVo);
                    break;
                }
            }

        }

        // 如果用户存在平台分摊明细填写权限，但是现有逻辑未产生该权限，则补充。
        if (userPlat != null) {
            logger.debug("该用户需要分配分摊明细填写权限...");
            boolean patchedFlag = false;

            // 若当前权限已经包含分摊项目，直接从现有权限中增加weight即可
            for (UserProjectPermVo permVo : userProjectPermVos) {
                if (permVo.getProjectId().equals(userPlat.getPlatId())) {
                    permVo.setWeight(1F);
                    patchedFlag = true;
                }
            }

            // 否则，强制增加该权限
            if (!patchedFlag) {
                UserProjectPermVo userProjectPermVo = new UserProjectPermVo();
                userProjectPermVo.setProjectName(userPlat.getPlatName());
                userProjectPermVo.setUserId(userId);
                userProjectPermVo.setProjectId(userPlat.getPlatId());
                userProjectPermVo.setProjectRoleId(PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_PEOPLE_ID);
                userProjectPermVo.setShareDetailFlag(true);
                boolean isManage = rUserProjectPermVos.stream().filter(p -> null != p)
                        .anyMatch(p -> userPlat.getPlatId().equals(p.getProjectId()) &&
                                p.getProjectRoleId().equals(PermissionHelper.SystemRole.BACKEND_FINANCE_SHARE_MANAGER_ID));
                userProjectPermVo.setManagerFlag(isManage);
                userProjectPermVo.setWeight(1F);
                userProjectPermVo.setShareWeekWriteFlag(userPlat.getShareWeekWriteFlag() == null ? false : userPlat.getShareWeekWriteFlag());
                userProjectPermVos.add(userProjectPermVo);
            }
        }

        if (userProjectPermVos.size() == 0 ) {
            throw new ParamException("该用户没有任何平台的分摊填写权限!");
        }

        return userProjectPermVos;
    }

    @Override
    public List<UserWeekProjectPermVo> getUserShareWeekProjectPerms() {
        List<UserWeekProjectPermVo> result = new ArrayList<>();

        User user = MyTokenUtils.getCurrentUser();
        Set<Long> projectIdSet = new HashSet<>();
        Map<Long, Object> managerPlatIdMap = new HashMap<>();
        Set<Long> readPlatIdList = new HashSet<Long>();
        Long inProjectId = userMapper.selectUserProjectId(user.getId());
        boolean projectManagerFlag = false;
        boolean manageAllPlatFlag = false;
        boolean readAllPlatFlag = false;
        List<CfgPlatAttrVo> allPlatVo = cfgPlatAttrMapper.selectAllWeekShareWithPlatName();
        Map<Long, CfgPlatAttrVo> cfgPlatAttrVoByPlatIdMap = allPlatVo.stream().collect(Collectors.toMap(x -> x.getPlatId(), x -> x));
        List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByUserId(user.getId());
        // 所管辖的平台
        for (RUserProjectPerm rUserProjectPerm : rUserProjectPerms) {
            if (ProjectRole.Role.pm_manager_id.equals(rUserProjectPerm.getProjectRoleId())) {
                projectManagerFlag = true;
                break;
            }
            if (ProjectRole.Role.plat_share_manager_id.equals(rUserProjectPerm.getProjectRoleId())) {
                if (rUserProjectPerm.getProjectId() != null) {
                    projectIdSet.add(rUserProjectPerm.getProjectId());
                    managerPlatIdMap.put(rUserProjectPerm.getProjectId(), null);
                } else {
                    manageAllPlatFlag = true;
                    break;
                }
            }
            if (!readAllPlatFlag && ProjectRole.Role.plat_share_observer_id.equals(rUserProjectPerm.getProjectRoleId())) {
                if (rUserProjectPerm.getProjectId() != null) {
                    projectIdSet.add(rUserProjectPerm.getProjectId());
                    readPlatIdList.add(rUserProjectPerm.getProjectId());
                } else {
                    readAllPlatFlag = true;
                }
            }
        }

        if (manageAllPlatFlag || projectManagerFlag || readAllPlatFlag) {
            for (CfgPlatAttrVo cfgPlatAttrVo : allPlatVo) {
                projectIdSet.add(cfgPlatAttrVo.getPlatId());
                if (manageAllPlatFlag || projectManagerFlag) {
                    managerPlatIdMap.put(cfgPlatAttrVo.getPlatId(), null);
                } else {
                    readPlatIdList.add(cfgPlatAttrVo.getPlatId());
                }
            }
        }
        // 补充人力组组长所对应的平台
        Map<Long, List<BaseParentDto>> projectIdsByWorkGroupIdMap = workGroupMapper.selectActiveWorkGroupIdWithProjectId().stream().collect(Collectors.groupingBy(x -> x.getId()));
        List<WorkGroupHrDto> workGroups = workGroupMapper.selectActiveWorkGroupHrDtoByHrId(user.getId());
        for (WorkGroupHrDto workGroup : workGroups) {
            List<BaseParentDto> baseParentDtos = projectIdsByWorkGroupIdMap.get(workGroup.getId());
            if (baseParentDtos != null) {
                for (BaseParentDto baseParentDto : baseParentDtos) {
                    projectIdSet.add(baseParentDto.getParent());
                }
            }
        }

        // 回填
        boolean inSetFlag = projectIdSet.contains(inProjectId);
        projectIdSet.add(inProjectId);
        for (Long projectId : projectIdSet) {
            CfgPlatAttrVo cfgPlatAttrVo = cfgPlatAttrVoByPlatIdMap.get(projectId);
            if (cfgPlatAttrVo != null) {
                if (cfgPlatAttrVo.getShareWeekWriteFlag() != null && cfgPlatAttrVo.getShareWeekWriteFlag()) {
                    UserWeekProjectPermVo userProjectPermVo = new UserWeekProjectPermVo();
                    userProjectPermVo.setManagerFlag(true);
                    userProjectPermVo.setProjectId(projectId);
                    userProjectPermVo.setProjectName(cfgPlatAttrVo.getPlatName());
                    userProjectPermVo.setProjectManagerFlag(projectManagerFlag);
                    if (managerPlatIdMap.containsKey(projectId)) {
                        userProjectPermVo.setPlatManagerFlag(true);
                    }
                    if (readPlatIdList.contains(projectId)) {
                        userProjectPermVo.setPlatObserverFlag(true);
                    }
                    if (projectId.equals(inProjectId)) {
                        userProjectPermVo.setWeight(1f);
                        if (!inSetFlag) {
                            userProjectPermVo.setManagerFlag(false);
                        }
                    }
                    result.add(userProjectPermVo);
                }
            }
        }

        return result;
    }

    @Override
    public List<UserMiniVo> getAllPerformanceUsers() {
        List<UserMiniVo> resultList = userMapper.selectAllPerformanceUser();
        resultList.sort((x, y) -> x.getLoginId().compareTo(y.getLoginId()));
        return resultList;
    }

    @Override
    public User getCurrentUserInfo() {
        User user = MyTokenUtils.getCurrentUser();
        user.setEmail(getDecryptEmail(user.getEmail()));
        return user;
    }

    @Override
    public String exportAllUserInfo() {
        List<OrgWorkGroupMemberAppVo> allUsers = userMapper.selectAllEntityWithOrgWorkGroupSimpleWithIntern();
        List<WorkGroup> fullNameWorkGroups = workGroupService.getAllActiveWorkGroup();
        List<OrgWorkGroupExportInfo> exportUsers = new ArrayList<>();
        for (OrgWorkGroupMemberAppVo temp : allUsers) {
            OrgWorkGroupExportInfo item = new OrgWorkGroupExportInfo();
            item.setUserId(temp.getUserId());
            item.setName(temp.getName());
            item.setLoginId(temp.getLoginId());
            item.setLeaderFlag(temp.getLeaderFlag() != null && temp.getLeaderFlag());
            item.setWorkStatus(temp.getWorkStatus());
            List<WorkGroup> matchGroups = fullNameWorkGroups.stream().filter(w -> w.getId().equals(temp.getWorkGroupId())).collect(Collectors.toList());
            if (matchGroups.size() > 0) {
                item.setFullWorkGroupName(matchGroups.get(0).getFullPathName());
            }
            exportUsers.add(item);
        }
        return genExcelFromAllUser(exportUsers);
    }

    private String genExcelFromAllUser(List<OrgWorkGroupExportInfo> exportUsers) {
        String fileExportPath = fileSystemDiskPath + fileExportDiskPath + File.separator + "西山居全员组织信息.xls";
        FileOutputStream fos = null;
        Workbook wb = null;
        if (exportUsers.size() > 0) {
            try {
                wb = new HSSFWorkbook();
                fos = new FileOutputStream(fileExportPath);
                Sheet sheet = wb.createSheet();
                Row row = sheet.createRow(0);
                row.createCell(0).setCellValue("工作组");
                row.createCell(1).setCellValue("员工姓名");
                row.createCell(2).setCellValue("状态");
                int currentRowNumber = 1;
                for (OrgWorkGroupExportInfo user : exportUsers) {
                    Row currentRow = sheet.createRow(currentRowNumber);
                    currentRow.createCell(0).setCellValue(user.getFullWorkGroupName());
                    currentRow.createCell(1).setCellValue(user.getName() + "(" + user.getLoginId() + ")");
                    currentRow.createCell(2).setCellValue(user.getWorkStatus());
                    currentRowNumber++;
                }
                //设置列自动调整宽度
                sheet.autoSizeColumn(0, true);
                sheet.autoSizeColumn(1, true);
                sheet.autoSizeColumn(2, true);
                wb.write(fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (wb != null) {
                        wb.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return fileExportPath;
    }

    private String getDecryptEmail(String email) {
        try {
            if (email != null) {
                email = MyEncryptorUtils.decryptByAES(email);
            }
        } catch (Exception e) {
            logger.error("user email is invalid...");
            e.printStackTrace();
        }
        return email;
    }

    private String getDecryptPhone(String phone) {
        try {
            if (phone != null) {
                phone = MyEncryptorUtils.decryptByAES(phone);
            }
        } catch (Exception e) {
            logger.error("user phone is invalid...");
            e.printStackTrace();
        }
        return phone;
    }

    private CfgPlatAttrVo isUserHasShareDetailEditPerm(Long userId) {
        Long userProjectId = userMapper.selectUserProjectId(userId); // 用户所在平台
        List<CfgPlatAttrVo> cfgPlatAttrVos = cfgPlatAttrMapper.selectAllWithPlatName(); // 当前所有在填写分摊的平台
        List<CfgPlatAttrVo> matchedPlats = cfgPlatAttrVos.stream().filter(c -> c != null
                && c.getShareDetailFlag()
                && c.getPlatId().equals(userProjectId)).collect(Collectors.toList());
        return matchedPlats.size() > 0 ? matchedPlats.get(0) : null;
    }

    @Override
    public boolean hasPermission(String state) {

        Long userId = MyTokenUtils.getCurrentUserId();
        // 1. 普通成员都需要的权限（目前有：我的绩效，分摊填写），只需个别特殊处理。
        return normalUserPermCollector.hasPermission(userId, state)
                // 2. 绩效组对应的其他权限（在performance_work_group实体上衍生出来的权限，目前有：绩效组观察者，绩效组数据专员，绩效组配置专员）
                || performanceDataPermCollector.hasPermission(userId, state)
                // 3. 绩效组对应的负责人权限（因早期直接将该负责人权限定义在 performance_work_group的performance_manager_id字段中，故继续沿用）
                || performanceManagerPermCollector.hasPermission(userId, state)
                // 4. 通用角色处理
                || rUserProjectPermCollector.hasPermission(userId, state)
                // 5. 固化菜单(项目负责人的权限没有在数据库中存储，代码判断)
                || fixMemberPermCollector.hasPermission(userId, state);
    }

    @Override
    public UserPermissionsVo getPermissionList() {

        User currentUser = MyTokenUtils.getCurrentUser();
        // 具体逻辑，同上
        return normalUserPermCollector.collectPermission(currentUser)
                .add(performanceDataPermCollector.collectPermission(currentUser))
                .add(performanceManagerPermCollector.collectPermission(currentUser))
                .add(rUserProjectPermCollector.collectPermission(currentUser))
                .add(fixMemberPermCollector.collectPermission(currentUser));
    }

    @Override
    public UserInfoAppVo getAppUserInfo(Long userId, Boolean getSeatFlag) {
        boolean myself = false;
        boolean subMemberFlag = false;

        if (null == userId) {
            userId = MyTokenUtils.getCurrentUserId();
            myself = true;
        }

        UserInfoAppVo user = userMapper.selectByUserId(userId);
        user.setUserContactGroups(new ArrayList<>());
        //获取个人卡位工号
        if (null == getSeatFlag || getSeatFlag) {
            try {
                Object userSeatNo = ksUserService.getUserSeatNo(user.getLoginId());
                if (userSeatNo != null) {
                    user.setSeatNo(userSeatNo.toString());
                }
            } catch (Exception e) {
                logger.error("获取个人卡位信息失败");
            }
        }

        List<WorkGroup> allGroups = workGroupMapper.selectAllByActive();
        Map<Long, WorkGroup> allGroupMap = allGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));

        if (myself) {
            user.setPhone(getDecryptPhone(user.getPhone()));
            CfgSystemParam cfgSystemParam = cfgSystemParamMapper.selectByName(MySystemParamUtils.Key.UserPsyStartFlag);
            if (cfgSystemParam != null) {
                user.setPsyStartFlag(Boolean.parseBoolean(cfgSystemParam.getValue()));
            } else {
                user.setPsyStartFlag(false);
            }
        } else {
            List<PerformanceWorkGroupDto> perfWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
            Long logonUserId = MyTokenUtils.getCurrentUserId();
            List<UserHideThePhone> userHideThePhones = userHideThePhoneMapper.selectAll();
            Map<Long, UserHideThePhone> userHideThePhoneMap = userHideThePhones.stream().collect(Collectors.toMap(u -> u.getUserId(), u -> u));
            if (userHideThePhoneMap.containsKey(logonUserId)) {
                user.setPhone(getDecryptPhone(user.getPhone()));
            } else if (!userHideThePhoneMap.containsKey(userId)) {
                user.setPhone(getDecryptPhone(user.getPhone()));
            } else {
                user.setPhone(null);
            }

            //联系人群组
            List<UserContactGroup> contactGroups = userContactGroupMapper.selectInContactGroupsByUserId(userId);
            for (UserContactGroup contactGroup : contactGroups) {
                if (logonUserId.equals(contactGroup.getUserId())) {
                    user.getUserContactGroups().add(contactGroup);
                }
            }

            //是否下属绩效组成员
            for (PerformanceWorkGroupDto perfWorkGroup : perfWorkGroups) {
                if (logonUserId.equals(perfWorkGroup.getPerformanceManagerId())) {
                    List<Long> subperfWorkGroupId = new ArrayList<>();
                    WorkGroupDto workGroupTree = PerformanceTreeHelper.getNoMemberWorkGroupTree(perfWorkGroup.getId(), perfWorkGroups, false);
                    PerformanceTreeHelper.getAllSubPerfWorkGroupIdByRootTree(subperfWorkGroupId, workGroupTree);
                    if (subperfWorkGroupId.contains(user.getPerfWorkGroupId()) || perfWorkGroup.getId().equals(user.getPerfWorkGroupId())) {
                        subMemberFlag = true;
                        break;
                    }
                }
            }
        }

        user.setEmail(getDecryptEmail(user.getEmail()));
        //所在人力组
        if (allGroupMap.containsKey(user.getWorkGroupId())) {
            WorkGroup workGroup = allGroupMap.get(user.getWorkGroupId());
            user.setWorkGroup(ReportHelper.getWorkGroupFullPathName(workGroup, allGroupMap));
        }

        user.setSubMemberFlag(subMemberFlag);
        return user;
    }

    @Override
    public UserInfoVo.BaseInfoVo getBaseInfoVo() {

       List<IdNameBaseObject> projects = projectMapper.selectAllBaseInfo();

        Map<Long, String> maps = workGroupService.getWorkGroupsIdNamePairFromCache();
        List<IdNameBaseObject> groups = new ArrayList<>();


        for (Map.Entry<Long, String> entry : maps.entrySet()) {
            IdNameBaseObject idNameBaseObject = new IdNameBaseObject();
            idNameBaseObject.setId(entry.getKey());
            idNameBaseObject.setName(entry.getValue());
            groups.add (idNameBaseObject);
        }

       List<IdNameBaseObject> subcompanys = subcompanyMapper.selectAllBaseInfo();

       UserInfoVo.BaseInfoVo baseInfoVo = new UserInfoVo.BaseInfoVo();

       baseInfoVo.setProjects(projects);

       baseInfoVo.setWorkGroups(groups);

       Map<String, List<Long>> subcompanyIds = new LinkedHashMap<>();

       subcompanys.stream().forEach(item -> {
           List<Long> ids =  subcompanyIds.get(item.getName());
           if (ids == null) {
               ids = new ArrayList<>();
               subcompanyIds.put(item.getName(), ids);
           }
           ids.add(item.getId());
       });

       List<UserInfoVo.CityInfoVo> citys = new ArrayList<>();

       for (Map.Entry<String, List<Long>> entry: subcompanyIds.entrySet()) {
           UserInfoVo.CityInfoVo cityInfoVo = new UserInfoVo.CityInfoVo();
           cityInfoVo.setName(entry.getKey());
           cityInfoVo.setSubcompanyIds(entry.getValue());
           citys.add (cityInfoVo);
       }

       baseInfoVo.setCitys(citys);

       return baseInfoVo;
    }

    @Override
    public BaseQueryResponseVo getUserInfoVoByCondition(UserBaseInfoRequestVo requestVo) {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }

        Integer currentPage = requestVo.getCurrentPage();
        Integer pageSize    = requestVo.getPageSize();
        Integer offset = (currentPage-1)*pageSize;
        Integer limit  = pageSize;
        requestVo.setOffset(offset);
        requestVo.setLimit(limit);
        if (!StringUtils.isBlank(requestVo.getSubCompanyIds())) {
            String[] ids = requestVo.getSubCompanyIds().split(",");
            requestVo.setSubcompanys(ids);
        }

        logger.info("requestVo -> {}", requestVo);

        List<UserBaseInfoDto> userBaseInfoDtos = userMapper.selectUserBaseInfoList(requestVo);

        Long count = userMapper.countUserBaseInfoList(requestVo);

        BaseQueryResponseVo baseQueryResponseVo = new BaseQueryResponseVo();

        Map<Long, String> groupNameValuePair = workGroupService.getWorkGroupsIdNamePairFromCache();

        userBaseInfoDtos.stream().forEach(userBaseInfoDto -> {
            filterEmptyDate(userBaseInfoDto);
            userBaseInfoDto.setWorkGroup(groupNameValuePair.get(userBaseInfoDto.getWorkGroupId()));
        });

        baseQueryResponseVo.setTotalRecord(count.intValue());

        baseQueryResponseVo.setItems(userBaseInfoDtos);

        return baseQueryResponseVo;
    }

    @Override
    public UserDetailBaseInfoDto getUserDetailInfo(Long userId) throws Exception {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }
        UserDetailBaseInfoDto userDetailBaseInfoDto =  userMapper.selectUserJoinUserDetailByUserId(userId);
        userDetailBaseInfoDto.setFullName(userDetailBaseInfoDto.getLastName() + userDetailBaseInfoDto.getFirstName());
        filterMarryFlag (userDetailBaseInfoDto);
        filterEmptyDate(userDetailBaseInfoDto);
        decryptFields (userDetailBaseInfoDto);
        return userDetailBaseInfoDto;
    }

    @Override
    public UserWorkInfoDto getUserWorkInfo(Long userId) throws Exception {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }
        UserWorkInfoDto userWorkInfoDto = userMapper.selectUserWorkInfoByUserId(userId);
        filterEmptyDate(userWorkInfoDto);
        String fields[] = {"email"};
        descrypt (userWorkInfoDto , fields);
        return userWorkInfoDto;
    }

    @Override
    public List<UserChildrenInfoDto> getUserChildrenInfo(Long userId) {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }
        List<UserChildrenInfoDto> childrenInfoDtos = userChildrenInfoMapper.selectByUserId(userId);
        filterEmptyDateList(childrenInfoDtos);
        return childrenInfoDtos;
    }

    @Override
    public List<UserNdaDto> getUserNdaInfo(Long userId) {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }

        List<UserNdaDto> userNdaDtos = userNdaMapper.selectByUserId(userId);
        filterEmptyDateList(userNdaDtos);
        return userNdaDtos;
    }

    @Override
    public List<UserWorkExperienceDto> getUserWorkExperienceInfo(Long userId) {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }
        List<UserWorkExperienceDto> userWorkExperienceDtos = userWorkExperienceMapper.selectByUserId(userId);
        filterEmptyDateList(userWorkExperienceDtos);
        return userWorkExperienceDtos;
    }

    @Override
    public List<UserEduExperienceDto> getUserEduExperienceInfo(Long userId) {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }
        List<UserEduExperienceDto> userEduExperienceDtos = userEduExperienceMapper.selectByUserId(userId);
        filterEmptyDateList(userEduExperienceDtos);
        return userEduExperienceDtos;
    }

    @Override
    public List<UserCertificationDto> getUserCertification(Long userId) {
        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }
        List<UserCertificationDto> userCertificationDtos = userCertificationMapper.selectByUserId(userId);
        filterEmptyDateList(userCertificationDtos);
        return userCertificationDtos;
    }

    @Override
    public String exportUserInfo(UserBaseInfoRequestVo userBaseInfoRequestVo) throws IllegalAccessException {

        if (!MyEnvUtils.isLocalEnv()) {
            menuService.checkUserPermission("employee-list");
        }

        Set<String> exports =  userBaseInfoRequestVo.getExports();
        if (exports!=null && !exports.isEmpty()) {
           if ( exports.contains("department")){
               userBaseInfoRequestVo.setNeedDepartment(Boolean.TRUE);
           }
        }

        List<UserBaseInfoExportDto> userBaseInfoExportDto = userMapper.selectUserJoinUserDetailAndDeparemtnyByUserId(userBaseInfoRequestVo);
        Map<Long, String>  groupMap = workGroupService.getWorkGroupsIdNamePairFromCache();

        List<String> titles = new ArrayList<>();

        List<String> fields = new ArrayList<>();

        exports.stream().forEach(item -> {
            if (titleValues.containsKey(item)){
                titles.add (titleValues.get(item));
                fields.add (item);
            }
        });


        String f [] = {
                "certificationNo","qq","emgContactPerson","emgContactPersonRel","emgContactPersonTel","familyContactPerson","familyContactPersonRel","familyContactPersonPlane",
                "familyContactPersonTel","homeAddress","currentAddress","email","phone","personalEmail", "msn"
        };

        List<String> list = Arrays.stream(f).collect(Collectors.toList());

        Set<String> filter = new HashSet<>();

        for (String s : fields ) {
            if (list.contains(s)) {
                filter.add (s);
            }
        }

        String[] strings = new String[filter.size()];

        for (UserBaseInfoExportDto dto: userBaseInfoExportDto) {
            String groupName = groupMap.get(dto.getWorkGroupId());
            dto.setWorkGroup(groupName);
            Date born = dto.getBirthday();
            if (born != null) {
                Date now  = new Date();
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                c1.setTime(born);
                c2.setTime(now);
                Integer year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
                Integer month =  c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
                if (month<0) --year;
                dto.setAge(year);
            }

            filterMarryFlag (dto);
            filterEmptyDate(dto);
            if (dto.getGender()!=null) {
                if (dto.getGender().equals("0")) {
                    dto.setGender("男");
                } else if (dto.getGender().equals("1")) {
                    dto.setGender("女");
                } else {
                    dto.setGender("未知");
                }
            }


            descrypt(dto, filter.toArray(strings) );
        }

        String filePath = fileExportDiskPath + File.separator + "员工信息.xls";

        String path = fileSystemDiskPath + File.separator + filePath;

        ExcelHelper.ExcelBuilder excelBuilder = new ExcelHelper.ExcelBuilder(path);

        excelBuilder.buildSheetWidth(titles);

        excelBuilder.buildHead(titles);

        excelBuilder.buildBody(fields, userBaseInfoExportDto);

        excelBuilder.export();

        return filePath;
    }

    private void decryptFields (UserDetailBaseInfoDto userDetailBaseInfoDto) throws IllegalAccessException {
        userDetailBaseInfoDto.setFullName(userDetailBaseInfoDto.getLastName() + userDetailBaseInfoDto.getFirstName());
        String fields[] = {
                "certificationNo","qq","emgContactPerson","emgContactPersonRel","emgContactPersonTel","familyContactPerson","familyContactPersonRel","familyContactPersonPlane",
                "familyContactPersonTel","homeAddress","currentAddress","email","phone","personalEmail", "msn"
        };
        descrypt(userDetailBaseInfoDto, fields);
    }


    private void descrypt (Object o, String[] fields) throws IllegalAccessException {

        for (String fieldName : fields) {
            Field field = null;

            for (Class clazz = o.getClass(); clazz!=Object.class; clazz = clazz.getSuperclass()) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    if (!field.getType().getSimpleName().equals("String")) continue;
                    String value = (String) field.get(o);
                    if (StringUtils.isBlank(value)) continue;
                    try{
                        value = MyEncryptorUtils.decryptByAES(value);
                        field.set(o, value);
                    }catch (Exception e) {
                        logger.error("descrypt , error -> {}", e.getMessage());
                    }
                } catch (NoSuchFieldException e) {

                }
            }
        }

    }

    private void filterEmptyDateList (List o) {
        if (o!=null) {
            o.stream().forEach(obj -> {
                filterEmptyDate(obj);
            });
        }
    }


    private void filterEmptyDate (Object o) {

        for (Class clazz = o.getClass(); clazz!=Object.class; clazz = clazz.getSuperclass()) {
            Field []fields = clazz.getDeclaredFields();
            for (Field f: fields) {
                f.setAccessible(Boolean.TRUE);
                if (f.getType().getSimpleName().equals("Date")) {
                    Date d = null;
                    try {
                        d = (Date)f.get(o);
                        if (d == null) continue;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(d);
                        if (calendar.get(Calendar.YEAR) == 1900) f.set(o, null);
                    } catch (IllegalAccessException e) {
                        logger.error("filterEmptyDate, error ->{}", e.getMessage());
                    }

                }
            }
        }
    }

    private void filterMarryFlag (UserDetailBaseInfoDto dto) {
        if (dto.getMarryFlag()!=null) {
            if (dto.getMarryFlag().equals("0")) {
                dto.setMarryFlag("单身未育");
            } else if (dto.getMarryFlag().equals("1")) {
                dto.setMarryFlag("已婚未育");
            } else if (dto.getMarryFlag().equals("2")) {
                dto.setMarryFlag("已婚已育");
            } else if (dto.getMarryFlag().equals("3")) {
                dto.setMarryFlag("单身已育");
            } else {
                dto.setMarryFlag("未知");
            }
        }
    }

}
