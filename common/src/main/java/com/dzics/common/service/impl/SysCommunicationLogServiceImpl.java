package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.SysCommunicationLog;
import com.dzics.common.dao.SysCommunicationLogMapper;
import com.dzics.common.service.SysCommunicationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 通信日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
@Service
public class SysCommunicationLogServiceImpl extends ServiceImpl<SysCommunicationLogMapper, SysCommunicationLog> implements SysCommunicationLogService {

    /**
     * dzics-dev-cutting-tool-detection
     * dzics-dev-gather-v1-checkout-equipment
     * dzics-dev-gather-v1-product-position
     * dzics-dev-gather-v1-pulse-signal
     * dzics-dev-gather-v1-queue
     * dzics-dev-gather-v1-state
     * */
    @Override
    public void delCommunicationLog(Integer i,Integer delPostionLog) {
//        清楚报工日志之外的所有日志
        long time = System.currentTimeMillis() - ((long) i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysCommunicationLog> wrapper = new QueryWrapper<>();
        wrapper.ne("queuename","dzics-dev-gather-v1-product-position");
        wrapper.le("Timestamp", date);
        remove(wrapper);
//        清楚报工日志
        wrapper.clear();
        time = System.currentTimeMillis() - ((long) delPostionLog * (24 * 3600 * 1000));
        wrapper.eq("queuename","dzics-dev-gather-v1-product-position");
        wrapper.le("Timestamp", new Date(time));
        remove(wrapper);
    }
}
