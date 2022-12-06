package com.dzics.business.service;

import com.dzics.business.model.vo.CommuLogPrm;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

/**
 * 通信日志
 *
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
public interface BusCommunicationLogService {
    /**
     * 通信日志
     * @param pageLimit
     * @param commuLogPrm
     * @return
     */
    Result communicationLog(PageLimit pageLimit, CommuLogPrm commuLogPrm);

    /**
     * 通信指令日志
     * @param pageLimit
     * @param commuLogPrm
     * @return
     */
    Result communicationLogTcp(PageLimit pageLimit, CommuLogPrm commuLogPrm);

    /**
     * 清理系统操作日志
     */
    void delDiskFile(Integer days);
}
