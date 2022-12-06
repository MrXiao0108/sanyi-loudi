package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionLineChartDo;
import com.dzics.common.model.response.SelectTrendChartDo;

import java.util.List;
import java.util.Map;

/**
 * 产品检测接口
 *
 * @author ZhangChengJun
 * Date 2021/6/3.
 * @since
 */
public interface AccqDzProductService {
    /**
     * 三一产品检测趋势
     * @return
     * @param orderNo
     * @param lineNo
     */
    SelectTrendChartDo getSanYiDetectionCurve(String orderNo, String lineNo);

    DzProduct getById(String productId);

    List<SelectTrendChartDo> getDetectionByMachine(String orderNo, String lineNo, GetOrderNoLineNo data);

    /**
     * 查询检测项多项折线图
     * @param data
     * @return
     */
    GetDetectionLineChartDo getDetectionLineChart(GetOrderNoLineNo data);


    /**
     * 根据产品序号和订单产线 查询该产品绑定的检测项
     * @param productNo
     * @param orderNo
     * @param lineNo
     * @return
     */
    List<Map<String,String>> getProductNoShowDetection(String productNo, String orderNo, String lineNo);

    String getNameAndOrder( String name, String lineType);

    /**
     * 智能检测系统推送
     * @param
     * @return
     */
    Map<String,Object> getIntelligentDetection(DzWorkpieceData dzWorkpieceData) throws Exception;
}
