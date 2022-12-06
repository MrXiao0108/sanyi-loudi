package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzProductMapper;
import com.dzics.common.model.custom.WorkNumberName;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.service.DzProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品列表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Service
public class DzProductServiceImpl extends ServiceImpl<DzProductMapper, DzProduct> implements DzProductService {

    @Override
    public WorkNumberName getProductNo(String modelNumber) {
        return this.baseMapper.getProductNo(modelNumber);
    }

    @Override
    public WorkNumberName getProductType(String productType) {
        return this.baseMapper.getProductType(productType);
    }

    @Override
    public DzProduct getSyProductNo(String syProductNo) {
        QueryWrapper<DzProduct> wp = new QueryWrapper<>();
        wp.eq("sy_productNo", syProductNo);
        List<DzProduct> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            DzProduct dzProduct = list.get(0);
            return dzProduct;
        }
        return null;
    }

    @Override
    public DzProduct getNameAndOrder( String name, String lineType) {
        QueryWrapper<DzProduct> wp = new QueryWrapper<>();
        wp.eq("product_name", name);
        wp.eq("line_type", lineType);
        DzProduct one = getOne(wp);
        return one;
    }


}
