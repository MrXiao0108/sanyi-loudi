package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzWorkingProcedureMapper;
import com.dzics.common.model.entity.DzWorkingProcedure;
import com.dzics.common.model.response.productiontask.workingProcedure.SelProcedureProduct;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;
import com.dzics.common.service.DzWorkingProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工序表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Service
public class DzWorkingProcedureServiceImpl extends ServiceImpl<DzWorkingProcedureMapper, DzWorkingProcedure> implements DzWorkingProcedureService {

    @Autowired
    private DzWorkingProcedureMapper dzWorkingProcedureMapper;

    @Override
    public List<WorkingProcedureRes> selWorkingProcedure(String field, String type, String orderId, String lineId, String workCode, String workName) {
        return dzWorkingProcedureMapper.selWorkingProcedure(field,type,orderId, lineId, workCode, workName);
    }

    @Override
    public List<SelProcedureProduct> selProcedureProduct(String productNo, String workingProcedureId) {
        return dzWorkingProcedureMapper.selProcedureProduct(productNo, workingProcedureId);
    }
}
