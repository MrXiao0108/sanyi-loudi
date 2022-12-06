package com.dzics.business.service;

import com.dzics.common.model.request.kb.KbParms;
import com.dzics.common.model.response.Result;

/**
 * 接口组调用
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
public interface KbParmsService {

    /**
     * 根据订单号 和 方法组名称 调用
     *
     * @param kbParms
     * @return
     */
    Result getMethodsGroup(KbParms kbParms);
}
