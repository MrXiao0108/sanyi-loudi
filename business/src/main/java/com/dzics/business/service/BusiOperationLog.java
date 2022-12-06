package com.dzics.business.service;

import com.dzics.common.model.request.SysOperationLoggingVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.dzics.common.model.entity.SysOperationLogging;

import java.util.List;

/**
 * 操作日志接口
 *
 * @author ZhangChengJun
 * Date 2021/1/15.
 * @since 1.0.0
 */
public interface BusiOperationLog {

    /**
     * @param pageLimit 分页参数
     * @param sub 操作用户
     * @param code
     * @param sysOperationLoggingVo
     * @return
     */
    Result queryOperLog(PageLimit pageLimit, String sub, String code, SysOperationLoggingVo sysOperationLoggingVo);

    /**
     * 根據id集合删除操作日志
     * @param ids
     * @return
     */
    Result delOperLog(List<Integer> ids);


}
