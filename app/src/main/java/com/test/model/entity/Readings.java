package com.test.model.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Readings implements Serializable {
    private String digest;
    private String title;   //阅读标题
    private String url;     //阅读链接地址
    private String group;   //阅读分组
    private String content;//内容
    private String img_url;//图片链接
    private String date;

    public Readings(String title, String url, String group, String digest, String img_url, String date) {
        this.title = title;
        this.url = url;
        this.group = group;
        this.digest = digest;
        this.content = content;
        this.img_url = img_url;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}