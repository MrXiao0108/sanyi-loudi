package com.dzics.common.dao;

import com.dzics.common.model.entity.DzWorkingStationProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.locationartifacts.LocationArtifactsVo;
import com.dzics.common.model.response.locationartifacts.LocationArtifactsDo;
import com.dzics.common.model.response.productiontask.workingProcedure.SelProcedureProduct;

import java.util.List;

/**
 * <p>
 * 工位-工件关联关系表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-28
 */
public interface DzWorkingStationProductMapper extends BaseMapper<DzWorkingStationProduct> {


    List<LocationArtifactsDo> locationArtifactsList(LocationArtifactsVo locationArtifactsVo);
}
