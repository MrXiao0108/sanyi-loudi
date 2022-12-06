package com.dzics.kanban.service;

import com.dzics.kanban.model.response.UserTokenMsg;
import eu.bitwalker.useragentutils.UserAgent;

import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
public interface BusiSysLoginLogService {
    void saveLogin(UserAgent userAgent, String ipAddr, UserTokenMsg login, Map<String, String> userObj, Exception o, String orgCode);
}
