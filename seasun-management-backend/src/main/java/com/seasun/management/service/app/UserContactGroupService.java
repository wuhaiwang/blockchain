package com.seasun.management.service.app;

import com.seasun.management.vo.AppUserContactGroupVo;
import java.util.List;

public interface UserContactGroupService {

    List<AppUserContactGroupVo> getUserContactGroups();

    int updateUserContactGroup(Long id, AppUserContactGroupVo users);

    int deleteUserContactGroup(Long id);

    int insertUserContactGroup(AppUserContactGroupVo appUserContactGroupVo);

    AppUserContactGroupVo getUserContactGroupInfo(Long id);
}
