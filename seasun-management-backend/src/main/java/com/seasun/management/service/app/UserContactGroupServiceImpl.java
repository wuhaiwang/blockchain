package com.seasun.management.service.app;

import com.seasun.management.dto.ContactGroupUserDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.RUserContactGroupMapper;
import com.seasun.management.mapper.UserContactGroupMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserContactGroup;
import com.seasun.management.util.MyEmojiUtil;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.AppUserContactGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserContactGroupServiceImpl implements UserContactGroupService {

    @Autowired
    private UserContactGroupMapper userContactGroupMapper;

    @Autowired
    private RUserContactGroupMapper rUserContactGroupMapper;

    @Override
    public List<AppUserContactGroupVo> getUserContactGroups() {
        List<AppUserContactGroupVo> result = new ArrayList<>();
        User currentUser = MyTokenUtils.getCurrentUser();
        List<UserContactGroup> contactGroups = userContactGroupMapper.selectByUserId(currentUser.getId());
        if (!contactGroups.isEmpty()) {
            List<Long> contactGroupIds = new ArrayList<>();
            for (UserContactGroup contactGroup : contactGroups) {
                AppUserContactGroupVo appUserContactGroupVo = new AppUserContactGroupVo();
                appUserContactGroupVo.setId(contactGroup.getId());
                appUserContactGroupVo.setName(MyEmojiUtil.resolveToEmojiFromByte(contactGroup.getName()));
                contactGroupIds.add(contactGroup.getId());
                result.add(appUserContactGroupVo);
            }
            List<ContactGroupUserDto> workGroupUsers = rUserContactGroupMapper.selectContactGroupUsersByUserContactGroupIds(contactGroupIds);
            if (!workGroupUsers.isEmpty()) {
                Map<Long, List<ContactGroupUserDto>> workGroupUsersByGroupIdMap = workGroupUsers.stream().collect(Collectors.groupingBy(x -> x.getUserContactGroupId()));
                for (AppUserContactGroupVo appUserContactGroupVo : result) {
                    List<ContactGroupUserDto> contactGroupUserDtos = workGroupUsersByGroupIdMap.get(appUserContactGroupVo.getId());
                    if (contactGroupUserDtos == null) {
                        contactGroupUserDtos = new ArrayList<>();
                    }
                    appUserContactGroupVo.setTotal(contactGroupUserDtos.size());

                    Collections.sort(contactGroupUserDtos, new Comparator<ContactGroupUserDto>() {
                        @Override
                        public int compare(ContactGroupUserDto o1, ContactGroupUserDto o2) {
                            return o1.getLoginId().compareTo(o2.getLoginId());
                        }
                    });
                    appUserContactGroupVo.setUsers(contactGroupUserDtos);
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUserContactGroup(Long id, AppUserContactGroupVo userContactGroup) {
        UserContactGroup originalUserContactGroup = userContactGroupMapper.selectByPrimaryKey(id);
        if (originalUserContactGroup != null) {
            if (!originalUserContactGroup.getName().equals(userContactGroup.getName())) {
                originalUserContactGroup.setName(MyEmojiUtil.resolveToByteFromEmoji(userContactGroup.getName()));
                userContactGroupMapper.updateByPrimaryKeySelective(originalUserContactGroup);
            }
            rUserContactGroupMapper.deleteByUserContactGroupId(id);
            return rUserContactGroupMapper.batchInsert(id, userContactGroup.getUsers());
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserContactGroup(Long id) {
        rUserContactGroupMapper.deleteByUserContactGroupId(id);
        return userContactGroupMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUserContactGroup(AppUserContactGroupVo appUserContactGroupVo) {
        User currentUser = MyTokenUtils.getCurrentUser();
        appUserContactGroupVo.setUserId(currentUser.getId());
        appUserContactGroupVo.setName(MyEmojiUtil.resolveToByteFromEmoji(appUserContactGroupVo.getName()));
        List<UserContactGroup> contactGroups = userContactGroupMapper.selectByUserId(currentUser.getId());
        for (UserContactGroup contactGroup : contactGroups) {
            if (contactGroup.getName().equals(appUserContactGroupVo.getName())) {
                return 0;
            }
        }
        int i = userContactGroupMapper.insertSelective(appUserContactGroupVo);

        if (i > 0 && appUserContactGroupVo.getUsers() != null && appUserContactGroupVo.getUsers().size() > 0) {
            return rUserContactGroupMapper.batchInsert(appUserContactGroupVo.getId(), appUserContactGroupVo.getUsers());
        }
        return 0;
    }

    @Override
    public AppUserContactGroupVo getUserContactGroupInfo(Long id) {
        UserContactGroup userContactGroup = userContactGroupMapper.selectByPrimaryKey(id);
        if (userContactGroup != null) {
            AppUserContactGroupVo appUserContactGroupVo = new AppUserContactGroupVo();
            appUserContactGroupVo.setName(MyEmojiUtil.resolveToEmojiFromByte(userContactGroup.getName()));
            appUserContactGroupVo.setId(userContactGroup.getId());
            List<ContactGroupUserDto> contactGroupUserDtos = rUserContactGroupMapper.selectContactGroupUsersByUserContactGroupId(id);
            appUserContactGroupVo.setTotal(contactGroupUserDtos.size());
            appUserContactGroupVo.setUsers(contactGroupUserDtos);
            return appUserContactGroupVo;
        }
        return null;
    }
}
