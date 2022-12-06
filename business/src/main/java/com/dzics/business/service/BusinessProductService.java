package com.dzics.business.service;

import com.dzics.business.model.vo.ProductListModel;
import com.dzics.common.model.request.AddProductVo;
import com.dzics.common.model.response.DzProductDo;
import com.dzics.common.model.response.ProductParm;
import com.dzics.common.model.response.Result;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface BusinessProductService {
    /**
     * 产品列表查询
     *
     * @param sub
     * @param productListModel
     * @return
     */
    Result<List<DzProductDo>> list(String sub, ProductListModel productListModel);

    /**
     * 添加产品
     *
     * @param sub
     * @param addProductVo
     * @return
     */
    @CacheEvict(value = {"dzDetectionTemplCache.getByDepartId","cacheService.getProductNameFrequency"}, key = "#addProductVo.departId")
    Result add(String sub, AddProductVo addProductVo);

    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByOrderNoProNo", "dzDetectionTemplCache.getGroupKey", "cacheService.getProductNo","cacheService.getProductType","cacheService.getProductNameFrequency"}, allEntries = true)
    Result put(String sub, AddProductVo addProductVo);

    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByOrderNoProNo", "cacheService.getProductNo","cacheService.getProductType","cacheService.getProductNameFrequency"}, allEntries = true)
    Result del(String sub, Long productId);

    Result getById(String sub, Long productId);

    Result getByOrderId(String sub, Integer page, Integer limit, Long departId);


    List<ProductParm> getByDepartId(Long departId);

    /**
     * @param productNo 产品序号
     * @return 返回产品关联站点
     */
    Long getByProeuctNoDepartId(String productNo);

    /**
     * 根据站点id查询产品列表
     *
     * @param departId
     * @return
     */
    Result getByProductId(Long departId);

    /**
     * 根据产线类型获取产品信息
     * @param lineType
     * @return
     */
    Result getDepartLineType(String lineType);
}
