package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysLoginLog;

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
