package com.seasun.management.dto;

import com.seasun.management.model.FmUserRole;

public class FixRoleInfoDto extends FmUserRole {

    /**
     * 项目/平台的id
     */
    private Long pId;
    /**
     * 项目/平台名称
     */
    private String name;

    /**
     * 用户的角色名称：项目固化/平台固化/项目固化首次审核员
     */
    private String roleName;

    /**
     * 用户的名字
     */
    private String userName;

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
