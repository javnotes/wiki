package com.example.wiki.exception;

/**
 * @author luf
 * @date 2023/03/21 20:52
 **/
public enum BusinessExceptionCode {
    USER_LOGIN_NAME_EXIST("用户登录名已存在"),
    LOGIN_USER_ERROR("用户名不存在或密码错误"),
    ;
    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
