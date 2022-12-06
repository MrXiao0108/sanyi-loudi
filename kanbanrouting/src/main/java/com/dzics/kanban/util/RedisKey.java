package com.dzics.kanban.util;

import org.springframework.stereotype.Component;

/**
 * @author ZhangChengJun
 */
@Component
public class RedisKey {

    /**
     * 登录用户缓存
     */
    public static final String STRING = "USERPERSIONPFXKEY";
    public static final String REF_TOEKN_TIME = "Ref:Toekn:Time:";
    public static final String REF_TOEKN_TIME_TOKEN = "Ref:Toekn:Time:Token";
    public static final String USER_NAME_AND_USER_TYPE = "User:Name:And:User:Type";

    public static final String LEASE_CAR_TOKEN_HISTORY = "lease:car:token:history:";
    public static final String SYS_BUS_TASK_ARRANGE = "SysBusTask:Arrange";
    public static final String KEY_RUN_MODEL_DANGER = "SYSTEM:KEY:RUN:MODEL:DANGER";
    public static final String KET_DEL_WARE = "SYSTEM:KET:DEL:WARE:";
}
