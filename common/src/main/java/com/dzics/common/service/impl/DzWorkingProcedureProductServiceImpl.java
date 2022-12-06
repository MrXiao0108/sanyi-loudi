package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzWorkingProcedureMapper;
import com.dzics.common.dao.DzWorkingProcedureProductMapper;
import com.dzics.common.model.entity.DzWorkingProcedureProduct;
import com.dzics.common.model.response.commons.WorkingProcedures;
import com.dzics.common.service.DzWorkingProcedureProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工序-工件关联关系表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Service
public class DzWorkingProcedureProductServiceImpl extends ServiceImpl<DzWorkingProcedureProductMapper, DzWorkingProcedureProduct> implements DzWorkingProcedureProductService {
    @Autowired
    private DzWorkingProcedureMapper dzWorkingProcedureMapper;

    @Override
    public List<WorkingProcedures> getWorkingProcedures() {
        return dzWorkingProcedureMapper.getWorkingProcedures();
    }
}
