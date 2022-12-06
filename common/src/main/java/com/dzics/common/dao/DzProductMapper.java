package com.dzics.common.dao;

import com.dzics.common.model.custom.WorkNumberName;
import com.dzics.common.model.entity.DzProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.DzProductDo;
import com.dzics.common.model.response.GetProductByOrderIdDo;
import com.dzics.common.model.response.ProductParm;
import com.dzics.common.model.response.commons.Products;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产品列表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
@Repository
public interface DzProductMapper extends BaseMapper<DzProduct> {

    List<GetProductByOrderIdDo> getByOrderId(Long departId);

    List<DzProductDo> listProduct(@Param("field") String field, @Param("type") String type, @Param("productName") String productName, @Param("useOrgCode") String useOrgCode,@Param("lineType")  String lineType);

    List<ProductParm> getByDepartId(@Param("departId") Long departId);

    Long getByProeuctNoDepartId(@Param("productNo") String productNo);

    WorkNumberName getProductNo(@Param("modelNumber") String modelNumber);

    WorkNumberName getProductType(@Param("productType") String productType);

    List<Products> selProducts(@Param("lineType")String lineType);

    List<Products> getDepartLineType(@Param("lineType") String lineType);
}
