package com.dzics.common.exception.enums;

/**
 * http 异常类型定义 以及状态码返回定义
 */
public enum CustomExceptionType {
    /**
     *无权限
     */
    AUTHEN_TICATIIN_FAILURE(401, "无权限"),
    /**
     *成功
     */
    OK(0, "成功"),
    /**
     *无数据
     */
    OK_NO_DATA(0, "无数据"),
    /**
     *参数异常
     */
    Parameter_Exception(400, "参数异常"),
    /**
     *redis数据块缓存饱和无法存储消费
     */
    RedisNoGetError(500, "redis数据块缓存饱和无法存储消费"),
    /**
     *用户输入异常
     */
    USER_INPUT_ERROR(400, "用户输入异常"),
    /**
     *请重新获取token
     */
    AUTHEN_TOKEN_IS_ERROR(402, "请重新获取token"),
    /**
     *账户禁用
     */
    USER_IS_LOCK(201, "账户禁用"),
    /**
     *用户或密码错误
     */
    USER_IS_PASSWORD_ERROR(403, "用户或密码错误"),
    /**
     *用户名不存在
     */
    USER_IS_NULL(404, "用户名不存在"),
    /**
     *用户类型错误
     */
    USER_IS_ERROR_TYPE(405, "用户类型错误"),
    /**
     *请重新登录
     */
    AUTHEN_TOKEN_REF_IS_ERROR(406, "请重新登录"),
    /**
     *参数异常
     */
    TOKEN_PERRMITRE_ERROR(407, "参数异常"),
    /**
     *系统繁忙稍后再试
     */
    SYSTEM_ERROR(500, "系统繁忙稍后再试"),
    /**
     *认证不被信任
     */
    TOKEN_AUTH_ERROR(408, "认证不被信任"),
    /**
     *请绑定手机号
     */
    REGISTER(301, "请绑定手机号");


    CustomExceptionType(int code, String typeDesc) {
        this.code = code;
        this.typeDesc = typeDesc;
    }

    private String typeDesc;//异常类型中文描述

    private int code; //code

    public String getTypeDesc() {
        return typeDesc;
    }

    public int getCode() {
        return code;
    }
}