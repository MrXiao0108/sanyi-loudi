package com.dzics.common.service;

import com.dzics.common.model.entity.MomOrderPathMaterial;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 工序物料表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
public interface MomOrderPathMaterialService extends IService<MomOrderPathMaterial> {


    /**
     * 根据工序ID 获取组件物料
     * @param workingProcedureId
     * @return
     */
    List<MomOrderPathMaterial> getMaterialNo(String workingProcedureId);
}
