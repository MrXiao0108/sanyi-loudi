package com.dzics.sanymom.service;

import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.entity.LogPromptMsgMom;

/**
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
public interface SanyLogPromptMsgService {
    void saveLogPromptMsg(LogPromptMsg tinvokCoreLog);

    void saveLogPromptMsgMom(LogPromptMsgMom tinvokCoreLog);
}
