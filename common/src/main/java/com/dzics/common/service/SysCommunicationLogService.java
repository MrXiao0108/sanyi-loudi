package com.dzics.common.service;

import com.dzics.common.model.entity.SysCommunicationLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 通信日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
public interface SysCommunicationLogService extends IService<SysCommunicationLog> {

    void delCommunicationLog(Integer delCommunicationLog,Integer delPostionLog);

}
