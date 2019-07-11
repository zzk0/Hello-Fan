package com.test.model.dto;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String userName;
    private String nickName;
    private String sex;
    private String school;
    private String brief;
    private String avatarUrl;
    private String studyDays;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStudyDays() {
        return studyDays;
    }

    public void setStudyDays(String studyDays) {
        this.studyDays = studyDays;
    }
}
