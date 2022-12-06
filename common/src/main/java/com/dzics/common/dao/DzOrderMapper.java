package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzOrder;
import com.dzics.common.model.response.DzOrderDo;
import com.dzics.common.model.response.commons.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzOrderMapper extends BaseMapper<DzOrder> {

    List<DzOrderDo> listOrder(@Param("orgCode") String orgCode,
                              @Param("departName") String departName,
                              @Param("orderNo") String orderNo,
                              @Param("departId") Integer departId,
                              @Param("field") String field,
                              @Param("type") String type);

    List<Orders> selOrders(@Param("orderId") String orderId);


    List<Orders> selOrdersDepart(@Param("departId") String departId);

}
