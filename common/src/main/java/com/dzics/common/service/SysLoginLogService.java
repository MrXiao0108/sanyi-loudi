package com.dzics.common.service;

import com.dzics.common.model.entity.SysLoginLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 登陆日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
public interface SysLoginLogService extends IService<SysLoginLog> {


    void delLoginLog(int i);
}
