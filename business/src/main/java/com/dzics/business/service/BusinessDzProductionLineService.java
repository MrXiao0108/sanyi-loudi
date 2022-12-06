package com.dzics.business.service;

import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.request.*;
import com.dzics.common.model.request.line.LineParmsList;
import com.dzics.common.model.response.LineDo;
import com.dzics.common.model.response.Result;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public interface BusinessDzProductionLineService {
    Result add(String sub, AddLineVo data) throws Exception;



    Result list(String sub, LineParmsList lineParmsList);

    @CacheEvict(cacheNames = "cacheService.lineId",allEntries = true)
    Result del(String sub, Long id);

    @CacheEvict(cacheNames = "cacheService.lineId",allEntries = true)
    Result pud(String sub, PudLineVo data) throws Exception;

    Result<LineDo> getById(String sub, Long id);

    Result putStatus(String sub, Long id);

    /**
     * 产线排班信息判断
     * @param workShiftVos
     * @return
     */
    boolean lineWorkTime(List<AddWorkShiftVo> workShiftVos) throws Exception;

    /**
     * 根据订单id查询产线列表
     * @param sub
     * @param id
     * @return
     */
    Result<DzProductionLine> getByOrderId(String sub, Long id);

    /**
     * 绑定产线统计产量的设备
     * @param sub
     * @return
     */
    Result bingEquipment(String sub, BingEquipmentVo bingEquipmentVo);

    /**
     * 查询所有产线
     * @param sub
     * @return
     */
    Result allLineList(String sub);

    /**
     * 查询所有产线 公共方法
     * @param sub
     * @return
     */
    Result allList(String sub);

    Result getByOrderIdV2(String sub, Long valueOf);

    /**
     * 根据产线ID获取产线信息
     * @param lineId 产线ID
     * @return
     */
    DzProductionLine getLineId(Long lineId);
}
