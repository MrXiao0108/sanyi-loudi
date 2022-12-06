package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzProductionLineMapper;
import com.dzics.common.model.custom.DzOrderNoLineNo;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.DzOrder;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.service.DzOrderService;
import com.dzics.common.service.DzProductionLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产线表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
public class DzProductionLineServiceImpl extends ServiceImpl<DzProductionLineMapper, DzProductionLine> implements DzProductionLineService {

    @Autowired
    private DzProductionLineMapper dzProductionLineMapper;

    @Override
    public Long getOnelineNo(String productionLineNumber) {
        QueryWrapper<DzProductionLine> wpProLine = new QueryWrapper<>();
        wpProLine.select("id");
        wpProLine.eq("line_no", productionLineNumber);
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectOne(wpProLine);
        return dzProductionLine != null ? dzProductionLine.getId() : null;
    }

    @Override
    public OrderIdLineId getOrderNoAndLineNo(String orderCode, String lineNo) {
        return dzProductionLineMapper.getOrderNoAndLineNo(orderCode, lineNo);
    }

    @Override
    public OrderIdLineId getOrderIdLineId(Long orderId, Long lineId) {
        return dzProductionLineMapper.getOrderIdLineId(orderId, lineId);
    }


}
