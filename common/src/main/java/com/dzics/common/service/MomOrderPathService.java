package com.dzics.common.service;

import com.dzics.common.model.entity.MomOrderPath;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单工序组工序组 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
public interface MomOrderPathService extends IService<MomOrderPath> {

    /**
     * 根据订单主键 获取 获取工序信息
     * @param proTaskOrderId 订单ID主键
     * @return
     */
    MomOrderPath getproTaskOrderId(String proTaskOrderId);
}
