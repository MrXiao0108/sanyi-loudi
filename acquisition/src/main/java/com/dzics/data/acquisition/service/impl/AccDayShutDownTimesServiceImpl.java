package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzDayShutDownTimes;
import com.dzics.common.service.DzDayShutDownTimesService;
import com.dzics.data.acquisition.service.AccDayShutDownTimesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/3/23.
 * @since
 */
@Service
@Slf4j
public class AccDayShutDownTimesServiceImpl implements AccDayShutDownTimesService {

    @Autowired
    private DzDayShutDownTimesService dzDayShutDownTimesService;

    @Override
    public DzDayShutDownTimes getDayShoutDownTime(String lineNum, String deviceNum, Integer deviceType, String orderNumber, LocalDate nowLocalDate) {
        QueryWrapper<DzDayShutDownTimes> wp = new QueryWrapper<>();
        wp.eq("work_date", nowLocalDate);
        wp.eq("order_no", orderNumber);
        wp.eq("line_no", lineNum);
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.select("id","down_sum","work_date","order_no","line_no","equipment_no","equipment_type");
        List<DzDayShutDownTimes> list = dzDayShutDownTimesService.list(wp);
        if (list == null || list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            log.warn("相同设备一天的停机次数记录存在多次：lineNum:{},deviceNum:{},deviceType:{}, orderNumber:{}, nowLocalDate:{}",
                    lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
            return list.get(0);
        } else {
            return list.get(0);
        }
    }

    @Override
    public boolean saveDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes) {
        return dzDayShutDownTimesService.save(dzDayShutDownTimes);
    }

    @Override
    public boolean updateById(DzDayShutDownTimes dzDayShutDownTimes) {
        return dzDayShutDownTimesService.updateById(dzDayShutDownTimes);
    }
}
