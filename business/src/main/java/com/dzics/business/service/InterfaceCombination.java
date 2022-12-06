package com.dzics.business.service;

import com.dzics.common.model.entity.SysInterfaceMethod;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;

import java.util.List;

/**
 * 接口组合
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
public interface InterfaceCombination {

    /**
     * 组合调用传递的所有接口 方法 集合 封装返回
     *
     * @param interfaceListMethod 接口方法集合
     * @return
     */
    Result getInterFaceMethods(List<SysInterfaceMethod> interfaceListMethod, GetOrderNoLineNo orderNoLineNo);
}
