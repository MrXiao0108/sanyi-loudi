package com.dzics.sanymom.service;

import com.dzics.common.model.entity.MomOrderPathMaterial;

import java.util.List;

/**
 * 工序物料接口
 *
 * @author ZhangChengJun
 * Date 2021/5/27.
 * @since
 */

public interface TaskPathMaterialService {

    /**
     * 保存 工序物料信息
     * @param materialList
     */
    void savePathMaterials(List<MomOrderPathMaterial> materialList);

    /**
     * 根据工序id集合  删除所有物料
     * @param collect
     */
    void delByOrdPathIds(List<String> collect);
}
