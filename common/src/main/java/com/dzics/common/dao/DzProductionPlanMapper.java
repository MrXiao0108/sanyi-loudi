package com.dzics.common.dao;

import com.dzics.common.model.entity.DzProductionPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.plan.SelectProductionPlanVo;
import com.dzics.common.model.response.plan.ProductionPlanDo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产线日生产计划表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
@Mapper
@Repository
public interface DzProductionPlanMapper extends BaseMapper<DzProductionPlan> {

    List<ProductionPlanDo> list(SelectProductionPlanVo selectProductionPlanVo);
}
