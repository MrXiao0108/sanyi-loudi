package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.DzMomReceiveMaterialService;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomReceiveMaterialService;
import com.dzics.common.util.PageLimitAgv;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DzMomReceiveMaterialServiceImpl implements DzMomReceiveMaterialService {
    @Autowired
    private MomReceiveMaterialService momReceiveMaterialService;

    @Override
    public Result chlickOkMaterialHistory(PageLimitAgv pageLimit) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<MomReceiveMaterial> wp = new QueryWrapper<>();
        wp.eq("line_no",pageLimit.getLineNo());
        wp.eq("order_no",pageLimit.getOrderNo());
        wp.orderByDesc("create_time","material_check");
        List<MomReceiveMaterial> list = momReceiveMaterialService.list(wp);
        PageInfo<MomReceiveMaterial> momReceiveMaterialPageInfo = new PageInfo<>(list);
        Result<Object> ok = Result.ok();
        ok.setCount(momReceiveMaterialPageInfo.getTotal());
        ok.setData(momReceiveMaterialPageInfo.getList());
        return ok;
    }
}
