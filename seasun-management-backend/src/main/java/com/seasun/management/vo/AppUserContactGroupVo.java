package com.seasun.management.vo;

import com.seasun.management.dto.ContactGroupUserDto;
import com.seasun.management.model.UserContactGroup;

import java.util.List;

public class AppUserContactGroupVo extends UserContactGroup {

    private int total;

    private List<ContactGroupUserDto> users;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ContactGroupUserDto> getUsers() {
        return users;
    }

    public void setUsers(List<ContactGroupUserDto> users) {
        this.users = users;
    }
}
