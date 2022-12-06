package com.dzics.sanymom.service;

import org.apache.poi.ss.formula.functions.T;

/**
 * @Classname MqSendLocalService
 * @Description 描述
 * @Date 2022/6/14 13:58
 * @Created by NeverEnd
 */
public interface MqSendLocalService<T> {
    boolean sendMq(T t, String routing, String exchange,String queryName);
}
