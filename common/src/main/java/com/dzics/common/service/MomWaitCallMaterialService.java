package com.dzics.common.service;

import com.dzics.common.model.custom.CallMaterial;
import com.dzics.common.model.entity.MomWaitCallMaterial;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 等待叫料的订单 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
public interface MomWaitCallMaterialService extends IService<MomWaitCallMaterial> {

    List<CallMaterial> getWorkStation(String proTaskId, String stationCode);

    /**
     * 根据订单号 和工位 查询物料信息
     * @param wiporderno  MOM订单号
     * @param stationId 工位编号
     * @param orderCode
     * @param lineNo
     * @return
     */
    List<MomWaitCallMaterial> getWorkStationOrderId(String wiporderno, String stationId, String orderCode, String lineNo);

    String getOprSequenceNo(String workStation, String proTaskOrderId);


    CallMaterial getCallMaterial(String lineNo, String orderCode, List<CallMaterial> materials);


    boolean removeProTaskId(String proTaskOrderId);
}
