package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.custom.StartWokeOrderMooM;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.request.mom.GetMomOrderVo;
import com.dzics.common.model.response.mom.GetMomOrderDo;
import com.dzics.common.model.response.mom.MomAuthOrderRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * mom下发订单表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Mapper
@Repository
public interface MomOrderMapper extends BaseMapper<MonOrder> {

    StartWokeOrderMooM getProgressStatus(@Param("progressStatus") String progressStatus, @Param("guid") String guid, @Param("orderId") Long orderId, @Param("lineId") Long lineId);

    String getMomOrderByProducBarcode(String producBarcode);

    List<GetMomOrderDo>getMomOrder(GetMomOrderVo getMomOrderVO);


    MonOrder getOrderCallMaterialStatus(@Param("orderId") Long orderId, @Param("lineId") Long lineId, @Param("productNo") String productNo, @Param("down") String down);

    List<MomAuthOrderRes> getMomAuthOrderRes(@Param("orderId") Long orderId, @Param("lineId") Long lineId);

    /**
     * 查询进行中的订单
     * 或者订单暂停的订单
     * 或者在操作进行中的订单
     * @param lineId  产线ID
     * @return
     */
    default List<MonOrder> getOrderLoding(Long lineId) {
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("line_id", lineId);
        wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING).or().eq("ProgressStatus", MomProgressStatus.STOP).or().eq("order_operation_result", 1));
        List<MonOrder> list = selectList(wp);
        return list;
    }

    Integer getNowWorkPlanNum(@Param("orderId")String orderId,@Param("beginTime")String beginTime,@Param("endTime")String endTime);

}
