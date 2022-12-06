package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomOrderQrCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * mom 二维码 订单关系表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-10
 */
public interface MomOrderQrCodeMapper extends BaseMapper<MomOrderQrCode> {

    default MomOrderQrCode getQrMomOrder(String producBarcode, String orderNo, String lineNo) {
        QueryWrapper<MomOrderQrCode> wp = new QueryWrapper<>();
        wp.eq("product_code", producBarcode);
        wp.eq("order_no", orderNo);
        wp.eq("line_no", lineNo);
        return selectOne(wp);
    }
}
