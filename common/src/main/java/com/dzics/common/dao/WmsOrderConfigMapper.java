package com.dzics.common.dao;

import com.dzics.common.model.entity.WmsOrderConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.wms.GetOrderCfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
@Mapper
public interface WmsOrderConfigMapper extends BaseMapper<WmsOrderConfig> {

    List<GetOrderCfig> getCfg(@Param("field") String field, @Param("type") String type);
}
