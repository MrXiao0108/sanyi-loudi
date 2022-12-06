package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.IotSendData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-09
 */
public interface IotSendDataService extends IService<IotSendData> {
    /**
     * 删除Iot数据
     * */
    void delDateBaseIot(Integer days);
}
