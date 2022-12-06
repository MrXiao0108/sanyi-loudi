package com.dzics.common.service;

import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.product.DzProductDetectionTemplateParms;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
public interface DzProductDetectionTemplateService extends IService<DzProductDetectionTemplate> {

    List<DzProductDetectionTemplateParms> listGroupBy(String field, String type, String productName, Long departId, String orderId, String lineId);


    List<Map<String, Object>> listProductNo(String productNo,String orderNo,String lineNo);

    /**
     * 根据产品Id 获取检测配置模板
     *
     * @param orderId
     * @param lineId
     * @param productNo
     * @return
     */
    List<DzDetectTempVo> selProductTemplateProductId(String orderId, String lineId, String productNo);

    /**
     * 根据产品编号 和 检测 检测项获取 上线下线值
     * @param productNo
     * @param item
     * @return
     */
    DzProductDetectionTemplate getProductNoItem(String productNo, String item);

    List<Map<String, Object>> listProductId(String productNo);

    List<Map<String, Object>> getDefoutDetectionTemp();

}
