package com.dzics.common.dao;

import com.dzics.common.model.entity.DzWorkingFlowBig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 工件制作流程记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-20
 */
@Mapper
public interface DzWorkingFlowBigMapper extends BaseMapper<DzWorkingFlowBig> {

    List<DzWorkingFlowBig> getInitFlowBig();

}
