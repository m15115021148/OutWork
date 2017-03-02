package com.sitemap.nanchang.model;

/**
 * Created by Administrator on 2016/12/2.
 */

public class PersonModel {
    private String uuid;//      用户uuid
    private String username;//  用户名称
    private String password;// 用户密码（初始密码：123456）

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
