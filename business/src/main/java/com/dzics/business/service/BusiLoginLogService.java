package com.dzics.business.service;

import com.dzics.common.model.request.SysloginVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.dzics.common.model.entity.SysLoginLog;

import java.util.List;

/**
 * 登录日志接口
 *
 * @author ZhangChengJun
 * Date 2021/1/18.
 * @since 1.0.0
 */
public interface BusiLoginLogService {

    /**
     * 登录日志查询
     * @param pageLimit
     * @param sysloginVo
     * @param sub
     * @param code
     * @return
     */
    Result queryLogin(PageLimit pageLimit, SysloginVo sysloginVo, String sub, String code);

    /**
     * 根据id集合删除登录日志
     * @param ids
     * @return
     */
    Result delLoginLog(List<Integer> ids);
}
