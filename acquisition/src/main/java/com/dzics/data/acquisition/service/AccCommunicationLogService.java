package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.RabbitmqMessage;

/**
 * 通信日志
 *
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
public interface AccCommunicationLogService {
    void saveRabbitmqMessage(RabbitmqMessage rabbitmqMessage, boolean update,boolean save);
}
