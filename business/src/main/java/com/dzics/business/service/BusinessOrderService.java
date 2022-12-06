package com.dzics.business.service;

import com.dzics.common.model.entity.DzOrder;
import com.dzics.common.model.request.AddOrderVo;
import com.dzics.common.model.request.depart.DepartParms;
import com.dzics.common.model.request.order.OrderParmsModel;
import com.dzics.common.model.response.DzOrderDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.commons.Orders;
import com.dzics.common.model.response.commons.SelOrders;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface BusinessOrderService {

    Result<DzOrder> add(String sub, AddOrderVo data);


    Result del(String sub, Long id);

    Result<List<DzOrderDo>> list(String sub, OrderParmsModel orderParmsModel);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo
     * @return
     */
    DzOrder selOrderNo(String orderNo);

    Result put(String sub, AddOrderVo data);

    /**
     * 所有订单
     *
     * @param selOrders
     * @param sub
     * @return
     */
    Result setlOrders(SelOrders selOrders, String sub);

    /**
     * 所有产品
     * @param sub
     * @return
     */
    Result selProduct(String sub,String lineType);

    /**
     * 根据订单查询所有产线
     * @param selOrders
     * @param sub
     * @return
     */
    Result selLines(SelOrders selOrders, String sub);

    /**
     * 根据站点获取站点下的订单
     * @param departParms
     * @param sub
     * @return
     */
    Result selOrdersDepart(DepartParms departParms, String sub);
}
