package com.dzics.business.service;

import com.dzics.common.model.entity.DzToolGroups;
import com.dzics.common.model.request.toolinfo.AddDzToolGroupVo;
import com.dzics.common.model.request.toolinfo.GetToolInfoDataListVo;
import com.dzics.common.model.request.toolinfo.PutToolGroupsVo;
import com.dzics.common.model.request.toolinfo.PutToolInfoVo;
import com.dzics.common.model.response.GetToolInfoDataListDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

import java.util.List;

public interface BusinessToolGroupsService {
    /**
     * 查询刀具组
     * @param sub
     * @param pageLimit
     * @param groupNo
     * @return
     */
    Result<List<DzToolGroups>>  getToolGroupsList(String sub, PageLimit pageLimit , String groupNo);

    /**
     * 添加刀具组
     * @param addDzToolGroupVo
     * @return
     */
    Result addToolGroups(AddDzToolGroupVo addDzToolGroupVo);

    /**
     * 删除刀具组
     * @param toolGroupsId
     * @return
     */
    Result delToolGroups(Long toolGroupsId);


    /**
     * 修改刀具组编号
     * @param putToolGroupsVo
     * @return
     */
    Result putToolGroups(PutToolGroupsVo putToolGroupsVo);

    /**
     * 根据刀具组id查询刀具列表
     * @param toolGroupsId
     * @param pageLimit
     * @return
     */
    Result getToolInfoList(Long toolGroupsId, PageLimit pageLimit);

    /**
     * 编辑指定刀具组的所有刀具
     * @param putToolGroupsVo
     * @return
     */
    Result putToolInfo(PutToolInfoVo putToolGroupsVo);

    /**
     * 删除刀具判断
     * @param id
     * @return
     */
    Result delToolInfo(Long id);

    /**
     * 编辑刀具号
     * @param id
     * @param toolNo
     * @return
     */
    Result putToolInfo(Long id, Integer toolNo);

    /**
     * 新增刀具组id
     * @param toolGroupId
     * @param toolNo
     * @return
     */
    Result addToolInfo(Long toolGroupId, Integer toolNo);

    /**
     * 查询所有刀具组
     * @param sub
     * @return
     */
    Result getToolGroupsAll(String sub);

    /**
     * 查询刀具信息数据
     * @param sub
     * @param pageLimit
     * @param getToolInfoDataListVo
     * @return
     */
    Result<List<GetToolInfoDataListDo>> getToolInfoDataList(String sub, PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo);
}
