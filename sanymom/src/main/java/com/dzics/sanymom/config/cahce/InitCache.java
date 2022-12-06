package com.dzics.sanymom.config.cahce;

import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.sanymom.service.CachingApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 重启清除缓存
 *
 * @author ZhangChengJun
 * Date 2021/12/16.
 * @since
 */
@Component
public class InitCache implements ApplicationListener {
    @Autowired
    private CachingApi cachingApi;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        DzProductionLine dzProductionLine = cachingApi.updateOrderIdAndLineId();
        String runModel = cachingApi.getMomRunModel();
    }
}
