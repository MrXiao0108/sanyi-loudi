package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzLineShiftDay;
import com.dzics.common.service.DzLineShiftDayService;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.AccqLineShiftDayService;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Service
@Slf4j
public class AccqLineShiftDayServiceImpl implements AccqLineShiftDayService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;

    /**
     * 当前时间是否在 开始时间之后和 结束时间之前
     *
     * @param now 当前时间
     * @param st  开始时间
     * @param en  结束时间
     * @return
     */
    public static boolean compareSection(LocalTime now, LocalTime st, LocalTime en) {
        if (now.isAfter(st)) {

        }
        if (now.isAfter(st) && now.isBefore(en)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理时间  LocalTime
     */
    public static void localTime() {
        //获取当前时间  含有毫秒值  17:18:41.571
        LocalTime now = LocalTime.now();

        //获取当前时间   去掉毫秒值   17:45:41
        LocalTime now1 = LocalTime.now().withNano(0);
        //00:46:46.651  提供了把时分秒都设为0的方法
        LocalTime now2 = LocalTime.now().withHour(0);

        //构造时间  00:20:55
        LocalTime time1 = LocalTime.of(0, 20, 55);
        //构造时间  05:43:22
        LocalTime time2 = LocalTime.parse("05:43:22");

        //标准时间 2017-11-06T17:53:15.930
        LocalDateTime lt = LocalDateTime.now();
    }




    @Override
    public DzLineShiftDay getDzLineShiftDay(List<DzLineShiftDay> dzLineShiftDays, LocalTime nowLocalTime) {
        return dzLineShiftDays.get(0);
    }
}
