package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusProDeteTempl;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.service.DzProductDetectionTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Service
public class BusProDeteTemplImpl implements BusProDeteTempl {
    @Autowired
    private DzProductDetectionTemplateService productDetectionTemplateService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean save(List<DzProductDetectionTemplate> addtempLs) {
        return productDetectionTemplateService.saveBatch(addtempLs);
    }

    @Override
    public boolean delGroupId(Long groupId) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("group_Id", groupId);
        return productDetectionTemplateService.remove(wp);
    }

    @Override
    public Integer getProductNo(String productNo, Long departId, String orderId, String lineId) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("depart_id", departId);
        wp.eq("product_no",productNo);
        wp.eq("order_id",orderId);
        wp.eq("line_id",lineId);
        return productDetectionTemplateService.count(wp);
    }

}
