package com.dzics.common.dao;

import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.ProductTemp;
import com.dzics.common.model.request.DBDetectTempVo;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.product.DzProductDetectionTemplateParms;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
@Repository
public interface DzProductDetectionTemplateMapper extends BaseMapper<DzProductDetectionTemplate> {

    List<DzDetectTempVo> groupById(@Param("groupId") String groupId);

    List<DzProductDetectionTemplateParms> listGroupBy(@Param("field") String field, @Param("type") String type, @Param("productName") String productName, @Param("departId") Long departId, @Param("orderId") String orderId, @Param("lineId") String lineId);

    List<DBDetectTempVo> geteditdbdetectoritem(@Param("groupId") String groupId);

    Integer updateTemplate(@Param("oldProductNo") String productNo, @Param("newProductNo") String newProductNo);

    List<ProductTemp> getDzProDetectIonTemp(@Param("productNo") String productNo);

    List<DzProductDetectionTemplate> getOneObj(@Param("productNo") String productNo);

    List<Map<String, Object>> listMap(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);
    List<Map<String, Object>> listMapUpload(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<DzDetectTempVo> productId( @Param("orderId") String orderId,  @Param("lineId") String lineId, @Param("productNo") String productNo);

    DzProductDetectionTemplate getProductNoItem(@Param("productNo") String productNo, @Param("item") String item);

    List<Map<String, Object>> listProductIdMap(@Param("productNo") String productNo);

    List<Map<String, Object>> getDefoutDetectionTemp();
    List<Map<String, Object>> getDefoutDetectionTempUpLoad();

}
