package com.seasun.management.service.impl;

import com.seasun.management.common.WechatCropHttpComponent;
import com.seasun.management.constant.WechatCropConstant;
import com.seasun.management.dto.WechatCropConcatUserDto;
import com.seasun.management.exception.WechatCropException;
import com.seasun.management.helper.WechatCropUserFactoryHelper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.User;
import com.seasun.management.service.WechatCropService;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WechatCropServiceImpl implements WechatCropService {

    Logger logger = LoggerFactory.getLogger(WechatCropServiceImpl.class);

    @Autowired
    WechatCropHttpComponent wechatCropHttpComponent;


    @Autowired
    UserMapper userMapper;

    private WechatCropUserFactoryHelper.WechatCropUserFactory wechatCropUserFactory (WechatCropConstant.WechatCropRequestEnum wechatCropRequestEnum) {

        if (wechatCropRequestEnum == WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTEMPLOYEENO) {
            return new  WechatCropUserEmployeeNoServiceImpl();
        } else if (wechatCropRequestEnum == WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTLOGINID) {
            return new WechatCropUserLoginIdServiceImpl();
        } else if (wechatCropRequestEnum == WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTPHONEANDUSERNAME) {
            return new WechatCropUserNameAndPhoneServiceImpl();
        }
        return null;
    }


    @Override
    public WechatCropOperationResultVo updateUsers(List<RequestUserVo> requestUserVoList, String type) throws Exception{

        WechatCropOperationResultVo wechatCropOperationResultVo = new WechatCropOperationResultVo();

        List<List< WechatCropBaseVo.WechatCropOperationVo>> list = new ArrayList<>();

        WechatCropConstant.WechatCropRequestEnum wechatCropRequestEnum = null;

        if (StringUtils.isBlank(type)) throw new WechatCropException("空的类型参数");

        if (type.equals(WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTEMPLOYEENO.getType())) {
            wechatCropRequestEnum = WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTEMPLOYEENO;
        } else if (type.equals(WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTLOGINID.getType())) {
            wechatCropRequestEnum = WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTLOGINID;
        } else if (type.equals(WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTPHONEANDUSERNAME.getType())){
            wechatCropRequestEnum = WechatCropConstant.WechatCropRequestEnum.WECHATCROPREQUESTPHONEANDUSERNAME;
        } else {
            throw new WechatCropException("错误的类型参数");
        }

        WechatCropUserFactoryHelper.WechatCropUserFactory wechatCropUserFactory = this.wechatCropUserFactory(wechatCropRequestEnum);

        wechatCropUserFactory.setRequestUserVoList(requestUserVoList);

        List<WechatCropConcatUserDto> wechatCropConcatUserDtoList = wechatCropUserFactory.createWechatCropUser();

        List<WechatCropBaseVo.WechatCropOperationVo> userNotFound = wechatCropUserFactory.compareFindUserNotFound(wechatCropConcatUserDtoList);

        for (WechatCropConcatUserDto wechatCropConcatUserDto: wechatCropConcatUserDtoList) {
            list.add (updateUser(wechatCropConcatUserDto));
        }

        wechatCropOperationResultVo.setResultSet(list);

        wechatCropOperationResultVo.setUserNotFoundSet(userNotFound);

        return wechatCropOperationResultVo;
    }


    private WechatCropBaseVo.WechatCropOperationVo translate (String loginId, WechatCropBaseVo vo) {
        WechatCropBaseVo.WechatCropOperationVo wechatCropOperationVo = new WechatCropBaseVo.WechatCropOperationVo();
        wechatCropOperationVo.setErrcode(vo.getErrcode());
        wechatCropOperationVo.setErrmsg(vo.getErrmsg());
        wechatCropOperationVo.setTransactionId(loginId);
        return wechatCropOperationVo;
    }

    //todo: 由于调用了微信api, 过程涉及了删除和添加两个操作, 相当于分布式事务, 当前做法只是将所有步骤操作结果返回并且事务id 为用户的loginid, 日后需要加消息表记录哪个操作成功和失败, 方便人工回滚
    private List<WechatCropBaseVo.WechatCropOperationVo> updateUser (WechatCropConcatUserDto wechatCropConcatUserDto) throws Exception {

        List<WechatCropBaseVo.WechatCropOperationVo> list = new LinkedList<>();

        WechatCropAccessToken accessToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();

        logger.info ("WechatCropAccessToken -> {}", accessToken);

        WechatCropConcatUserVo wechatCropConcatUserVo = wechatCropHttpComponent.httpRequestFindWechatCropConcatUserByLoginId(wechatCropConcatUserDto.getUserid(), accessToken);

        logger.debug ("wechatCropConcatUserVo -> {}", wechatCropConcatUserVo);

        //delete

        WechatCropBaseVo deleteVo =  wechatCropHttpComponent.httpRequestDeleteWechatCropConcatUser(wechatCropConcatUserDto.getUserid(), accessToken);

        list.add (translate(wechatCropConcatUserDto.getUserid(), deleteVo));

        // describe dto

        this.describe (wechatCropConcatUserDto, wechatCropConcatUserVo);

        //create
        WechatCropBaseVo createVo = wechatCropHttpComponent.httpRequestCreateWechatCropConcatUser(wechatCropConcatUserDto, accessToken);

        list.add (translate(wechatCropConcatUserDto.getUserid(), createVo));

        return list;
    }

    private void describe(final WechatCropConcatUserDto wechatCropConcatUserDto, final WechatCropConcatUserVo wechatCropConcatUserVo) {
        wechatCropConcatUserDto.setStatus(wechatCropConcatUserVo.getStatus());
        wechatCropConcatUserDto.setIsleader(wechatCropConcatUserVo.getIsleader());
        wechatCropConcatUserDto.setDepartment(wechatCropConcatUserVo.getDepartment());
        wechatCropConcatUserDto.setEmail(wechatCropConcatUserVo.getEmail());
        wechatCropConcatUserDto.setUserid(wechatCropConcatUserVo.getUserid());
        wechatCropConcatUserDto.setGender(wechatCropConcatUserVo.getGender());
    }


    public class WechatCropUserLoginIdServiceImpl extends WechatCropUserFactoryHelper.WechatCropUserFactory {


        public WechatCropUserLoginIdServiceImpl () {

        }

        public WechatCropUserLoginIdServiceImpl(List<RequestUserVo> requestUserVoList) {
            super(requestUserVoList);
        }

        @Override
        public List<WechatCropConcatUserDto> createWechatCropUser() throws WechatCropException {
            Map<String, RequestUserVo> map = new HashMap<>();
            List<WechatCropConcatUserDto> wechatCropConcatUserDtoList = new ArrayList<>();
            List<String> loginIds = new LinkedList<>();
            requestUserVoList.stream().forEach(requestUserVo -> {
                String loginId = requestUserVo.getLoginId();
                Boolean notNull = StringUtils.isBlank(loginId);
                if (!notNull) {
                    map.put(requestUserVo.getLoginId(), requestUserVo);
                    loginIds.add (loginId);
                }
            });

            logger.info ("loginIds -> {}", loginIds);
            if (loginIds.size() == 0) throw new WechatCropException("参数错误, 请检查loginId");
            List<User> userList = userMapper.selectBYUserLoginIds(loginIds);
            userList.stream().forEach(user->{
                WechatCropConcatUserDto dto = new WechatCropConcatUserDto();
                dto.setName(user.getName());
                dto.setUserid(user.getLoginId());
                dto.setEmployeeNo(user.getEmployeeNo());
                try {
                    dto.setEmail(MyEncryptorUtils.decryptByAES(user.getEmail()));
                    RequestUserVo requestUserVo = map.get(user.getLoginId());
                    if (!StringUtils.isBlank(requestUserVo.getPhone())) dto.setMobile(requestUserVo.getPhone());
                    else dto.setMobile(MyEncryptorUtils.decryptByAES(user.getPhone()));
                } catch (Exception e) {
                    logger.error("{}", e.getMessage());
                }
                wechatCropConcatUserDtoList.add (dto);
            });
            return wechatCropConcatUserDtoList;
        }

        @Override
        public List<WechatCropBaseVo.WechatCropOperationVo> compareFindUserNotFound(List<WechatCropConcatUserDto> wechatCropConcatUserDtoList) {
            Map<String, RequestUserVo> map = new HashMap<>();
            List<WechatCropBaseVo.WechatCropOperationVo> wechatCropOperationVoList = new LinkedList<>();
            requestUserVoList.stream().forEach(requestUserVo -> {
                String loginId = requestUserVo.getLoginId();
                //if (!StringUtils.isBlank(loginId)){
                    map.put(loginId, requestUserVo);
                //}
            });

            wechatCropConcatUserDtoList.stream().forEach(wechatCropConcatUserDto -> {
                if (map.containsKey(wechatCropConcatUserDto.getUserid())) {
                    map.remove(wechatCropConcatUserDto.getUserid());
                }
            });

            for (Map.Entry<String, RequestUserVo> entry: map.entrySet() ) {
                WechatCropBaseVo.WechatCropOperationVo wechatCropOperationVo = new WechatCropBaseVo.WechatCropOperationVo();
                wechatCropOperationVo.setTransactionId(entry.getKey());
                wechatCropOperationVo.setErrmsg("loginId not found");
                wechatCropOperationVo.setErrcode(-1L);
                wechatCropOperationVoList.add (wechatCropOperationVo);
            }
            return wechatCropOperationVoList;
        }

    }

    public class WechatCropUserEmployeeNoServiceImpl extends WechatCropUserFactoryHelper.WechatCropUserFactory {

        public WechatCropUserEmployeeNoServiceImpl() {
            super();
        }

        public WechatCropUserEmployeeNoServiceImpl(List<RequestUserVo> requestUserVoList) {
            super(requestUserVoList);
        }

        @Override
        public List<WechatCropConcatUserDto> createWechatCropUser() throws WechatCropException{
            Map<Long, RequestUserVo> map = new HashMap<>();
            List<Long> employeeNos = new LinkedList<>();
            requestUserVoList.stream().forEach(requestUserVo -> {
                if (null != requestUserVo.getEmployeeNo()) {
                    map.put(requestUserVo.getEmployeeNo(), requestUserVo);
                    employeeNos.add (requestUserVo.getEmployeeNo());
                }
            });

            if (employeeNos.size() == 0 ) throw new WechatCropException("参数错误, 请检查 employeeNo");

            List<WechatCropConcatUserDto> wechatCropConcatUserDtoList = new ArrayList<>();
            List<User> userList = userMapper.selectUsersByEmployeeNos(employeeNos);
            userList.stream().forEach(user->{
                WechatCropConcatUserDto dto = new WechatCropConcatUserDto();
                dto.setName(user.getName());
                dto.setUserid(user.getLoginId());
                dto.setEmployeeNo(user.getEmployeeNo());
                try {
                    dto.setEmail(MyEncryptorUtils.decryptByAES(user.getEmail()));
                    RequestUserVo requestUserVo = map.get(user.getEmployeeNo());
                    if (!StringUtils.isBlank(requestUserVo.getPhone())) dto.setMobile(requestUserVo.getPhone());
                    else dto.setMobile(MyEncryptorUtils.decryptByAES(user.getPhone()));
                } catch (Exception e) {
                    logger.error("{}", e.getMessage());
                }
                wechatCropConcatUserDtoList.add (dto);
            });
            return wechatCropConcatUserDtoList;
        }

        @Override
        public List<WechatCropBaseVo.WechatCropOperationVo> compareFindUserNotFound(List<WechatCropConcatUserDto> wechatCropConcatUserDtoList) {
            Map<Long, RequestUserVo> map = new HashMap<>();
            List<WechatCropBaseVo.WechatCropOperationVo> wechatCropOperationVoList = new LinkedList<>();
            requestUserVoList.stream().forEach(requestUserVo -> {
                Long employeeNo = requestUserVo.getEmployeeNo();
                map.put(employeeNo, requestUserVo);

            });

            wechatCropConcatUserDtoList.stream().forEach(wechatCropConcatUserDto -> {
                if (map.containsKey(wechatCropConcatUserDto.getEmployeeNo())) {
                    map.remove(wechatCropConcatUserDto.getEmployeeNo());
                }
            });

            for (Map.Entry<Long, RequestUserVo> entry: map.entrySet() ) {
                WechatCropBaseVo.WechatCropOperationVo wechatCropOperationVo = new WechatCropBaseVo.WechatCropOperationVo();
                if (entry.getKey() != null)  wechatCropOperationVo.setTransactionId(Long.toString(entry.getKey()));
                wechatCropOperationVo.setErrmsg("employeeNo not found");
                wechatCropOperationVo.setErrcode(-1L);
                wechatCropOperationVoList.add (wechatCropOperationVo);
            }
            return wechatCropOperationVoList;
        }
    }

    public class WechatCropUserNameAndPhoneServiceImpl extends WechatCropUserFactoryHelper.WechatCropUserFactory {
        public WechatCropUserNameAndPhoneServiceImpl() {

        }

        public WechatCropUserNameAndPhoneServiceImpl(List<RequestUserVo> requestUserVoList) {
            super(requestUserVoList);
        }

        @Override
        public List<WechatCropConcatUserDto> createWechatCropUser() throws WechatCropException{
            Map<String, RequestUserVo> map = new HashMap<>();
            List<String> fullNames = new LinkedList<>();
            for (RequestUserVo requestUserVo : requestUserVoList) {
                if (!StringUtils.isBlank(requestUserVo.getName()) &&  !StringUtils.isBlank(requestUserVo.getPhone())) {
                    map.put(requestUserVo.getName() + requestUserVo.getPhone(), requestUserVo);
                    fullNames.add (requestUserVo.getName());
                }else {
                    throw new WechatCropException("需要填齐姓名: " + requestUserVo.getName() +" 手机号码: " + requestUserVo.getPhone());
                }
            }
            List<WechatCropConcatUserDto> wechatCropConcatUserDtoList = new ArrayList<>();
            List<User> userList = userMapper.selectUsersByFullName(fullNames);

            userList = userList.stream().filter(item->{
                if (StringUtils.isBlank(item.getPhone())) return Boolean.FALSE;
                String phone = null;
                try {
                    phone =  MyEncryptorUtils.decryptByAES(item.getPhone());
                } catch (Exception e) {
                    logger.error("decrept phone error : {}", e.getMessage());
                }
                if (map.containsKey(item.getName() + phone)) {
                    RequestUserVo requestUserVo = map.get(item.getName() + phone);
                    if (requestUserVo.getPhone().equals(phone))
                        return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }).collect(Collectors.toList());


            userList.stream().forEach(user->{
                WechatCropConcatUserDto dto = new WechatCropConcatUserDto();
                dto.setName(user.getName());
                dto.setUserid(user.getLoginId());
                dto.setEmployeeNo(user.getEmployeeNo());
                try {
                    dto.setEmail(MyEncryptorUtils.decryptByAES(user.getEmail()));
                    String phone = MyEncryptorUtils.decryptByAES(user.getPhone());
                    RequestUserVo requestUserVo = map.get(user.getName() + phone);
                    if (!StringUtils.isBlank(requestUserVo.getPhone())) dto.setMobile(requestUserVo.getPhone());
                    else dto.setMobile(MyEncryptorUtils.decryptByAES(user.getPhone()));
                } catch (Exception e) {
                    logger.error("{}", e.getMessage());
                }
                wechatCropConcatUserDtoList.add (dto);
            });
            return wechatCropConcatUserDtoList;
        }

        @Override
        public List<WechatCropBaseVo.WechatCropOperationVo> compareFindUserNotFound(List<WechatCropConcatUserDto> wechatCropConcatUserDtoList) {
            Map<String, RequestUserVo> map = new HashMap<>();
            List<WechatCropBaseVo.WechatCropOperationVo> wechatCropOperationVoList = new LinkedList<>();
            requestUserVoList.stream().forEach(requestUserVo -> {
                map.put(requestUserVo.getName() + requestUserVo.getPhone(), requestUserVo);
            });

            wechatCropConcatUserDtoList.stream().forEach(wechatCropConcatUserDto -> {
                if (map.containsKey(wechatCropConcatUserDto.getName() + wechatCropConcatUserDto.getMobile())) {
                    map.remove(wechatCropConcatUserDto.getName() + wechatCropConcatUserDto.getMobile());
                }
            });

            for (Map.Entry<String, RequestUserVo> entry: map.entrySet() ) {
                WechatCropBaseVo.WechatCropOperationVo wechatCropOperationVo = new WechatCropBaseVo.WechatCropOperationVo();
                if (entry.getKey() != null)  wechatCropOperationVo.setTransactionId(entry.getKey());
                wechatCropOperationVo.setErrmsg("user not found");
                wechatCropOperationVo.setErrcode(-1L);
                wechatCropOperationVoList.add (wechatCropOperationVo);
            }
            return wechatCropOperationVoList;
        }
    }



}
