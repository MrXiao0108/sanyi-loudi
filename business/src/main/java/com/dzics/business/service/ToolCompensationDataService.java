package com.dzics.business.service;

import com.dzics.common.model.entity.DzToolCompensationData;
import com.dzics.common.model.request.toolinfo.AddToolConfigureVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

import java.util.List;

public interface ToolCompensationDataService {
    /**
     * 查询刀具配置信息列表
     * @param sub
     * @param pageLimit
     * @param groupNo
     * @return
     */
    Result<List<DzToolCompensationData>> getToolConfigureList(String sub, PageLimit pageLimit, Integer groupNo);

    /**
     * 新增刀具配置信息
     * @param addToolConfigureVo
     * @return
     */
    Result addToolConfigure(AddToolConfigureVo addToolConfigureVo);

    /**
     * 删除刀具配置信息
     * @param id
     * @return
     */
    Result delToolConfigure(Integer id);

    /**
     * 查询设备指定刀具组下为绑定的刀具
     * @param equipmentId
     * @param groupNo
     * @return
     */
    Result getToolByEqIdAndGroupNo(Long equipmentId, Integer groupNo,Long toolGroupsId);

    /**
     * 修改刀具配置信息
     * @param addToolConfigureVo
     * @return
     */
    Result putToolConfigure(AddToolConfigureVo addToolConfigureVo);

    /**
     *
     * @param lineId
     * @return
     */
    Result getEquipmentByLine(Long lineId);

    /**
     * 根据id查询刀具配置详情
     * @param id
     * @return
     */
    Result getToolConfigureById(Integer id);

    /**
     * 根据设备id 批量绑定刀具信息
     * @param byEquipmentId
     * @return
     */
    Result addToolConfigureById(Long byEquipmentId);
}
