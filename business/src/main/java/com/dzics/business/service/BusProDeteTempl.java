package com.dzics.business.service;

import com.dzics.common.model.entity.DzProductDetectionTemplate;

import java.util.List;

/**
 * 产品设置检测项接口
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
public interface BusProDeteTempl {
    /**
     * @param addtempLs
     * 保存产品检测项设置
     * @return
     */
    boolean save(List<DzProductDetectionTemplate> addtempLs);

    /**
     * @param groupId 同组检测配置id
     * 删除产品配置检测
     * @return
     */
    boolean delGroupId(Long groupId);


    /**
     * 根据产品站点获取 ，是否存在配置
     * @param productNo 产品编号
     * @param departId 站点id
     * @param orderId
     * @param lineId
     * @return
     */
    Integer getProductNo(String productNo, Long departId, String orderId, String lineId);
}
