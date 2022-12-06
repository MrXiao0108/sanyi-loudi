package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzWorkingFlow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.mom.GetWorkingDetailsVo;
import com.dzics.common.model.response.mom.GetWorkingDetailsDo;
import com.dzics.common.model.response.productiontask.ProcedureAndStation;
import com.dzics.common.model.response.productiontask.station.WorkingFlowRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 工件制作流程记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
@Mapper
@Repository
public interface DzWorkingFlowMapper extends BaseMapper<DzWorkingFlow> {

    /**
     * 所有工序绑定工位信息
     *
     * @return
     */
    List<ProcedureAndStation> getWorkingProcedureAndStation();


    List<WorkingFlowRes> getWorkingFlow(@Param("list") List<String> list, @Param("orderId") Long orderId, @Param("lineId") Long lineId);

    List<String> getWorkingFlowBigQrCode(@Param("orderId") Long orderId, @Param("lineId") Long lineId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<GetWorkingDetailsDo> getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo);

    default List<DzWorkingFlow> getQrcode(String producBarcode) {
        QueryWrapper<DzWorkingFlow> wp = new QueryWrapper<>();
        wp.eq("qr_code", producBarcode);
        return selectList(wp);
    }
}
