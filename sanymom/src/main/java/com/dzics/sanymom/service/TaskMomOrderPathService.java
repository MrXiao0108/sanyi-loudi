package com.dzics.sanymom.service;

import com.dzics.common.model.entity.MomOrderPath;

import java.util.List;

/**
 * 订单工序接口
 *
 * @author ZhangChengJun
 * Date 2021/5/27.
 * @since
 */
public interface TaskMomOrderPathService {
    /**
     * 保存 工序 信息
     * @param momOrderPath
     * @return
     */
    List<MomOrderPath> saveMomOrderPath(List<MomOrderPath> momOrderPath);

    /**
     * 根据mom订单id  删除工序
     * @param orderId
     */
    void delByOrderId(String orderId);

    /**
     * 根据mom订单id  查询工序
     * @param orderId
     * @return
     */
    List<MomOrderPath> getListByOrderId(String orderId);
}
