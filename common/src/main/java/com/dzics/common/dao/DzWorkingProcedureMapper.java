package com.dzics.common.dao;

import com.dzics.common.model.entity.DzWorkingProcedure;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.commons.WorkingProcedures;
import com.dzics.common.model.response.productiontask.workingProcedure.SelProcedureProduct;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 工序表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Mapper
public interface DzWorkingProcedureMapper extends BaseMapper<DzWorkingProcedure> {

    List<WorkingProcedureRes> selWorkingProcedure(@Param("field") String field, @Param("type") String type, @Param("orderId") String orderId, @Param("lineId") String lineId, @Param("workCode") String workCode, @Param("workName") String workName);

    List<SelProcedureProduct> selProcedureProduct(@Param("productNo") String productNo, @Param("workingProcedureId") String workingProcedureId);

    List<WorkingProcedures> getWorkingProcedures();

}
