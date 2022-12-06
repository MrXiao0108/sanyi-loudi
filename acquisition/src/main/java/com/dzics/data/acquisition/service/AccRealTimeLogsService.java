package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.response.Result;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-06
 */
public interface AccRealTimeLogsService {
    /**
     * 保存日志
     * @param rabbitmqMessage
     * @return
     */
    SysRealTimeLogs saveRealTimeLog(RabbitmqMessage rabbitmqMessage);
    void saveRealTimeLog(List<RabbitmqMessage> rabbitmqMessageList);
    /**
     * 实时日志debug
     * @return
     * @param orderNo
     * @param lineNo
     * @param deviceType
     */
    Result getLogDebug(String orderNo, String lineNo, String deviceType);
    /**
     * 实时告警日志
     * @return
     * @param orderNo
     * @param lineNo
     * @param deviceType
     */
    Result getLogWarn(String orderNo, String lineNo, String deviceType);


}
