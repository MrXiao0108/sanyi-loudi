package com.dzics.common.service;

import com.dzics.common.model.custom.DzOrderNoLineNo;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.DzProductionLine;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 产线表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzProductionLineService extends IService<DzProductionLine> {


    /**
     * @param productionLineNumber 产线序号
     * @return
     */
    Long getOnelineNo(String productionLineNumber);

    OrderIdLineId getOrderNoAndLineNo(String orderCode, String lineNo);


    OrderIdLineId getOrderIdLineId(Long orderId, Long lineId);
}
