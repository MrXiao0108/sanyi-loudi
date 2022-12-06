package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.MomOrderPath;
import com.dzics.common.dao.MomOrderPathMapper;
import com.dzics.common.service.MomOrderPathService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单工序组工序组 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Service
@Slf4j
public class MomOrderPathServiceImpl extends ServiceImpl<MomOrderPathMapper, MomOrderPath> implements MomOrderPathService {

    /**
     * 获取工序信息
     *
     * @param proTaskOrderId
     * @return
     */
    @Override
    public MomOrderPath getproTaskOrderId(String proTaskOrderId) {
        QueryWrapper<MomOrderPath> wp = new QueryWrapper<>();
        wp.eq("mom_order_id",proTaskOrderId);
        List<MomOrderPath> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            log.warn("根据MOM订单ID 获取的工序信息，工序不存在 proTaskOrderId : {}", proTaskOrderId);
            return null;
        }
    }
}
