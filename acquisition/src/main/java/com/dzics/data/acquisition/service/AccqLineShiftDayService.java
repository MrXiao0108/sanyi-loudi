package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzLineShiftDay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 设备排班数据接口
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public interface AccqLineShiftDayService {

    /**
     * @param dzLineShiftDays 今日排版
     * @param nowLocalTime 当前时间
     * @return
     */
     DzLineShiftDay getDzLineShiftDay(List<DzLineShiftDay> dzLineShiftDays, LocalTime nowLocalTime);
}
