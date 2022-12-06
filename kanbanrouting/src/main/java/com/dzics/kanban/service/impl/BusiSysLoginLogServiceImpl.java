package com.dzics.kanban.service.impl;

import com.alibaba.fastjson.JSON;
import com.dzics.kanban.enums.OperTypeStatus;
import com.dzics.kanban.model.entity.SysLoginLog;
import com.dzics.kanban.model.response.UserTokenMsg;
import com.dzics.kanban.service.BusiSysLoginLogService;
import com.dzics.kanban.service.SysLoginLogService;
import com.dzics.kanban.util.DateUtil;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
@Service
@Slf4j
public class BusiSysLoginLogServiceImpl implements BusiSysLoginLogService {
    @Autowired
    private SysLoginLogService sysLoginLogService;
    @Autowired
    private DateUtil dateUtil;

    @Override
    public void saveLogin(UserAgent userAgent, String ipAddr, UserTokenMsg login, Map<String, String> userObj, Exception e, String orgCode) {
        Date date = new Date();
        String username = userObj.get("username");
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUserName(username);
        sysLoginLog.setOperIp(ipAddr);
        sysLoginLog.setBrowser(JSON.toJSONString(userAgent.getBrowser()));
        sysLoginLog.setOperatingSystem(JSON.toJSONString(userAgent.getOperatingSystem()));
        OperTypeStatus status = login == null ? OperTypeStatus.ERROR : OperTypeStatus.SUCCESS;
        sysLoginLog.setLoginStatus(status);
        sysLoginLog.setLoginMsg(login == null ? JSON.toJSONString(e.getMessage()) : JSON.toJSONString(status));
        sysLoginLog.setCreateTime(date);
        sysLoginLog.setOrgCode(orgCode);
        sysLoginLogService.save(sysLoginLog);
        String dateTime = dateUtil.getDateTime(date);
        log.info("用户：{} ,登录时间：{},状态：{}", username, dateTime, e== null ? status.getDesc():e.getMessage());
    }
}
