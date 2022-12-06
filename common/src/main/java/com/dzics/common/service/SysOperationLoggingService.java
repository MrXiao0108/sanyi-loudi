package com.dzics.common.service;

import com.dzics.common.model.entity.SysOperationLogging;
import com.baomidou.mybatisplus.extension.service.IService;

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


    void delOperationLog(int i);
}
