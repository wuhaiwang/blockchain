package com.seasun.management.dto;

import com.seasun.management.model.User;

public class UserDtoMultObject extends UserDto implements Cloneable {

    private UserDto user;

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public UserDtoMultObject clone() {
        UserDtoMultObject cloned = null;
        cloned = (UserDtoMultObject) super.clone();
        cloned.setUser(user.clone());
        return cloned;
    }
}
