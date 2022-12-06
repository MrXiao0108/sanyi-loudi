package com.dzics.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzWorkingProcedure;
import com.dzics.common.model.entity.DzWorkingProcedureProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.commons.WorkingProcedures;

import java.util.List;

/**
 * <p>
 * 工序-工件关联关系表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
public interface DzWorkingProcedureProductService extends IService<DzWorkingProcedureProduct> {

    /**
     * 获取所有工序
     * @return
     */
    List<WorkingProcedures> getWorkingProcedures();

}
