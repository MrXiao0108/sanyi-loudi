package com.dzics.common.service.impl;

import com.dzics.common.model.entity.DzOrder;
import com.dzics.common.dao.DzOrderMapper;
import com.dzics.common.service.DzOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
public class DzOrderServiceImpl extends ServiceImpl<DzOrderMapper, DzOrder> implements DzOrderService {

}
