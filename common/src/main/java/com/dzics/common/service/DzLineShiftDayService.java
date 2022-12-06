package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzLineShiftDay;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备产线 每日 排班表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-19
 */
public interface DzLineShiftDayService extends IService<DzLineShiftDay> {

    /**
     * @return 班次
     * @param eqId
     */
    List<DzLineShiftDay> getBc(List<Long> eqId);

    /**
     * @return 未排班的设备id
     * @param now
     */
    List<Long> getNotPb(LocalDate now);

    /**
     * @param lineNum 产线序号
     * @param deviceNum 设备序号
     * @param deviceType 设备类型
     * @param orderNumber
     * @param nowLocalDate 班次日期
     * @return
     */
    List<DzLineShiftDay> getlingshifudays(String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate);
}
