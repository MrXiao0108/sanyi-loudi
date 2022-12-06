package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.response.GetDetectionLineChartDo;
import com.dzics.common.model.response.SelectTrendChartDo;
import com.dzics.common.model.response.feishi.DayDataDo;

import java.lang.reflect.InvocationTargetException;

public interface LineDataService {
    /**
     * 检测项多项数据折线图推送
     * @return
     */
    GetDetectionLineChartDo charts(DzWorkpieceData dzWorkpieceData) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * 根据订单，产线，产品编号 获取检测数据
     * @param orderNo 产线
     * @param lineNo 订单
     * @return
     */
    SelectTrendChartDo getSelectTrendChartDo(DzWorkpieceData dzWorkpieceData,String orderNo,String lineNo);
    /**
     * 日生产综合报表
     * @return
     */
    DayDataDo dayData();
    /**
     * 月生产综合报表
     * @return
     */
    DayDataDo monthData();

    /**
     * 查询产线id
     * @return
     */
    Long getLineId();

    /**
     * 根据机床区分检测数据趋势图
     * @param dzWorkpieceData
     * @param orderNo
     * @param lineNo
     * @return
     */
    SelectTrendChartDo getDetectionByMachine(DzWorkpieceData dzWorkpieceData, String orderNo, String lineNo);

    /**
     * 根据机床区分检测数据趋势图 缓存获取
     * @param dzWorkpieceData
     * @return
     */
    SelectTrendChartDo getCharts(DzWorkpieceData dzWorkpieceData);
}
