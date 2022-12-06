package com.dzics.business.service.agv;

import com.dzics.common.model.request.mom.AgvLogParms;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimitBase;

/**
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
public abstract class AgvLogAbs {
    /**
     * ------------
     *
     * @param pageLimitBase：分页工具
     * @return Result
     */
    abstract public Result getLogPropMsg(AgvLogParms pageLimitBase);

    /**
     * ------------------
     *
     * @param groupId：组ID
     * @param pageLimitBase:分页工具
     * @return Result
     */
    abstract public Result getLogPropMsgMom(String groupId, PageLimitBase pageLimitBase);
}
