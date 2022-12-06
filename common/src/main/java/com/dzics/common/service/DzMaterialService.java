package com.dzics.common.service;

import com.dzics.common.model.entity.DzMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.Result;

/**
 * <p>
 * 产品物料表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-16
 */
public interface DzMaterialService extends IService<DzMaterial> {

    Result getMaterialByProductId(String productId);
}
