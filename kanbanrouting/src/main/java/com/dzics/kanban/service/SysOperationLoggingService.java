package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysOperationLogging;

import java.util.List;

/**
 * <p>
 * 操作日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
public interface SysOperationLoggingService extends IService<SysOperationLogging> {

    List<SysOperationLogging> queryOperLog(int startLimit, int limit);

    void delOperationLog(int i);
}
