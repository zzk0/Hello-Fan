package com.test.model.dto;

import com.test.model.entity.User;

public class UserDTO {
    public User user;
    public String code;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
