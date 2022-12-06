package com.dzics.kanban.service;


import com.dzics.kanban.model.entity.SysLoginLog;
import com.dzics.kanban.model.request.SysloginVo;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.util.PageLimit;

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
    Result<SysLoginLog> queryLogin(PageLimit pageLimit, SysloginVo sysloginVo, String sub, String code);

    /**
     * 根据id集合删除登录日志
     * @param ids
     * @return
     */
    Result delLoginLog(List<Integer> ids);
}
