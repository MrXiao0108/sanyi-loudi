package com.dzics.sanymom.service;

import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimitBase;

/**
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
public abstract class AgvLogAbs {
    abstract public Result getLogPropMsg(PageLimitBase pageLimitBase);

     abstract public Result getLogPropMsgMom(String groupId, PageLimitBase pageLimitBase);

}
