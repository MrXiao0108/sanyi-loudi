package com.dzics.common.service;

import com.dzics.common.model.custom.WorkNumberName;
import com.dzics.common.model.entity.DzProduct;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 产品列表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
public interface DzProductService extends IService<DzProduct> {

    WorkNumberName getProductNo(String modelNumber);

    WorkNumberName getProductType(String productType);


    DzProduct getSyProductNo(String syProductNo);

    DzProduct getNameAndOrder(String name, String lineType);
}
