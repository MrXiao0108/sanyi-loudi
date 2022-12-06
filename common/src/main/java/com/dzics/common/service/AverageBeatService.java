package com.dzics.common.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 平均节拍接口
 *
 * @author ZhangChengJun
 * Date 2021/6/4.
 * @since
 */
@Service
public interface AverageBeatService {

    /**
     * 计算设备的平均节拍
     * @return
     * @param times
     * @param id
     */
    String getDeviceIdAverageBeat(List<Long> times, Long id);
}
