package com.dzics.business.service;

import com.dzics.common.model.request.ProductionVo;
import com.dzics.common.model.request.plan.SelectEquipmentProductionDetailsVo;
import com.dzics.common.model.request.plan.SelectEquipmentProductionVo;
import com.dzics.common.model.request.plan.SelectProductionDetailsVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

public interface BusinessEquipmentProNumService {
    Result list(String sub, PageLimit pageLimit, SelectProductionDetailsVo selectProductionDetailsVo);

    /**
     * 设备生产数量明细
     * @param sub
     * @param pageLimit
     * @param selectProductionDetailsVo
     * @return
     */
    Result listProductionEquipment(String sub, PageLimit pageLimit, SelectEquipmentProductionVo selectProductionDetailsVo);

    Result listProductionEquipmentDetails(String sub, SelectEquipmentProductionDetailsVo selectEquipmentProductionDetailsVo);


    /**
     * 设备生产分时数据
     * @param
     * @param
     * @param
     * @return
     */
    Result listProductionTime(String sub, ProductionVo productionVo);
}
