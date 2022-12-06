package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzMaterial;
import com.dzics.common.dao.DzMaterialMapper;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;

/**
 * <p>
 * 产品物料表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-16
 */
@Service
public class DzMaterialServiceImpl extends ServiceImpl<DzMaterialMapper, DzMaterial> implements DzMaterialService {

    @Override
    public Result getMaterialByProductId(String productId) {

        QueryWrapper<DzMaterial>wrapper=new QueryWrapper<>();
        wrapper.eq("product_id",productId);
        List<DzMaterial> list = this.list(wrapper);
        return Result.ok(list);
    }
}
