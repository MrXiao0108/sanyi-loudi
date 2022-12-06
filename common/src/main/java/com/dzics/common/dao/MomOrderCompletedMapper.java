package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 待报工记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-21
 */
@Mapper
public interface MomOrderCompletedMapper extends BaseMapper<MomOrderCompleted> {

    default List<MomOrderCompleted> selQrCodeAndLine(Long orderId, Long lineId, String qrCode) {
        QueryWrapper<MomOrderCompleted> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.eq("qr_code", qrCode);
        return selectList(wp);
    }
}
