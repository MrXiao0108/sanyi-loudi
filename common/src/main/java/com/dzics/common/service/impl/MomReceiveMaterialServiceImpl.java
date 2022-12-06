package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.dao.MomReceiveMaterialMapper;
import com.dzics.common.service.MomReceiveMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 接收来料信息 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-28
 */
@Service
public class MomReceiveMaterialServiceImpl extends ServiceImpl<MomReceiveMaterialMapper, MomReceiveMaterial> implements MomReceiveMaterialService {

    @Override
    public List<MomReceiveMaterial> listNoCheck(String orderNo, String lineNo) {
        QueryWrapper<MomReceiveMaterial> wp = new QueryWrapper<>();
        wp.eq("material_check",false);
        wp.eq("order_no",orderNo);
        wp.eq("line_no",lineNo);
        List<MomReceiveMaterial> list = list(wp);
        return list;
    }
}
