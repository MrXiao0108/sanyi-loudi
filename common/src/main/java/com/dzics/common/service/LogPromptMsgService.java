package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.LogPromptMsg;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-12
 */
public interface LogPromptMsgService extends IService<LogPromptMsg> {

    void saveLogPromptMsg(LogPromptMsg tinvokCoreLog);

    LogPromptMsg getBtGroupId(String groupId);
}
