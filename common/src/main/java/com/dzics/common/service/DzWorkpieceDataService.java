package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.stationbg.CheckItems;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备检测数据V2新版记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface DzWorkpieceDataService extends IService<DzWorkpieceData> {

    /**
     * 根据二维码获取检测 数据 和关联的产品
     * 获取最新的一条检测记录
     * @param qrCode
     * @return
     */
    DzWorkpieceData getQrCodeProduct(String qrCode);

    /**
     * 根据产品id 获取 该 产品所绑定 工位的 检测项
     * @param productId
     * @param orderId
     * @param lineId
     * @return
     */
    Map<String, List<CheckItems>>   getProductIdCheckItems(String productId, Long orderId, Long lineId);

    List<Map<String, Object>> newestThreeData(List<String> infoList);

    List<String> getNewestThreeDataId(String orderNo, String lineNo,int size);

    DzWorkpieceData getLastDzWorkpieceData(String orderNo, String lineNo, String now);

    List<Map<String, Object>> newestThreeDataMom(List<String> infoList);

    List<String>getWorkPieceData(String orderNo,String lineNo,Integer size);

    Map<String, Object> newestThreeDataMomSingle(String id);

    Result getMaErBiaoDetectionMonitor(String orderNo, String lineNo, String qrCode);
}
