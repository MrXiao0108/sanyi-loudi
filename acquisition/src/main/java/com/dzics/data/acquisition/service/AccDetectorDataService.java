package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzDetectorData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionOneDo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.ProDetection;

import java.util.List;
import java.util.Map;

/**
 * 检测数据
 *
 * @author ZhangChengJun
 * Date 2021/2/10.
 * @since
 */
public interface AccDetectorDataService {
    boolean saveDataList(List<DzDetectorData> dataList);

    /**
     * 产品检测数据
     *
     * @return
     * @param orderNo
     * @param lineNo
     */
    Result<JCEquimentBase<ProDetection<List<Map<String, Object>>>>> getDetectorData(String orderNo, String lineNo);

    /**
     * 检测记录单项查询
     * @param data
     * @return
     */
    List<GetDetectionOneDo> getDetectionOne(GetOrderNoLineNo data);

    /**
     * 根据检测项字段名  获取对应检测结果的字段名
     * @param tableColVal
     * @return
     */
     String getOutOk(String tableColVal);


    /**
     * 根据检测记录ID 获取 检测记录 包含MOM 订单号 物料号
     *
     * @param ids     检测记录主键 ID
     * @param orderNo
     * @param lineNo
     * @return
     */
    Result getDetectionRecordMom(List<String> ids, String orderNo, String lineNo);

    Result<JCEquimentBase<ProDetection<Map<String, Object>>>> getDetectionRecordMomSingle(String id, String orderNo, String lineNo);

    Result getMaErBiaoDetectionMonitor(String orderNo,String lineNo,String qrCode);
}
