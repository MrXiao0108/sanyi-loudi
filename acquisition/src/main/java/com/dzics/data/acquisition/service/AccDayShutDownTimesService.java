package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzDayShutDownTimes;

import java.time.LocalDate;

/**
 * <p>
 * 设备每日停机次数 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-23
 */
public interface AccDayShutDownTimesService {

    /**
     * 获取当天的停机次数信息
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @param orderNumber
     * @param nowLocalDate
     * @return
     */
    DzDayShutDownTimes getDayShoutDownTime(String lineNum, String deviceNum, Integer deviceType, String orderNumber, LocalDate nowLocalDate);

    /**
     * 保存当天的停机次数
     * @param dzDayShutDownTimes
     * @return
     */
    boolean saveDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes);

    boolean updateById(DzDayShutDownTimes dzDayShutDownTimes);
}
