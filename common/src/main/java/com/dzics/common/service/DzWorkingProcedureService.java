package com.dzics.common.service;

import com.dzics.common.model.entity.DzWorkingProcedure;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.productiontask.workingProcedure.SelProcedureProduct;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;

import java.util.List;

/**
 * <p>
 * 工序表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
public interface DzWorkingProcedureService extends IService<DzWorkingProcedure> {

    /**
     *
     * @param field
     * @param type
     * @param orderId 订单id
     * @param lineId 产线id
     * @param workCode 工件编码
     * @param workName 工件名称
     * @return
     */
    List<WorkingProcedureRes> selWorkingProcedure(String field, String type, String orderId, String lineId, String workCode, String workName);

    /**
     * @param productNo  工件编号
     * @param workingProcedureId  工序ID
     * @return
     */
    List<SelProcedureProduct> selProcedureProduct(String productNo, String workingProcedureId);

}
