package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.config.SpringContextUtil;
import com.dzics.business.service.InterfaceCombination;
import com.dzics.common.model.entity.SysInterfaceMethod;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Service
@Slf4j
public class InterfaceCombinationImpl implements InterfaceCombination {

    /**
     * 组合调用传递的所有接口 方法 集合 封装返回
     *
     * @param interfaceListMethod 接口方法集合
     * @return
     */
    @Override
    public Result getInterFaceMethods(List<SysInterfaceMethod> interfaceListMethod, GetOrderNoLineNo orderNoLineNo) {
        Result<Object> ok = Result.ok();
        if (CollectionUtils.isNotEmpty(interfaceListMethod)) {
            Map<String, Object> map = new HashMap<>();
            for (SysInterfaceMethod method : interfaceListMethod) {
                try {
                    Object buProductionQuantityServiceImpl = SpringContextUtil.getBean(method.getBeanName());
                    Class quantityServiceImpl = buProductionQuantityServiceImpl.getClass();
                    long ls = System.currentTimeMillis();
                    Integer cacheDuration = method.getCacheDuration();
                    orderNoLineNo.setCacheTime(cacheDuration);
                    Method m = quantityServiceImpl.getDeclaredMethod(method.getMethodName(), GetOrderNoLineNo.class);
                    Object invoke = m.invoke(buProductionQuantityServiceImpl, orderNoLineNo);
                    long le = System.currentTimeMillis();
                    long su = le - ls;
                    map.put(method.getResponseName() + "_time", su);
                    map.put(method.getResponseName(), invoke);
                } catch (NoSuchMethodException e) {
                    log.error("获取Bean中的方法失败：{}", e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    log.error("执行方法参数异常：{}", e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    log.error("构造方法异常:{}", e.getMessage(), e);
                }
            }
            ok.setData(map);
        }
        return ok;
    }


}
