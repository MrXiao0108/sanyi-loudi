package com.dzics.sanymom.service;

import com.dzics.common.model.agv.MomUpPoint;

/**
 * 工位小车关系
 *
 * @author ZhangChengJun
 * Date 2021/11/5.
 * @since
 */
public interface TaskMomMaterialPointService {

    /**
     * @param basketType  小车 例如 A B C
     * @param orderCode 订单编号
     * @param lineNo 产线编号
     * @return
     */
    MomUpPoint getStationCode(String basketType, String orderCode, String lineNo);
}
