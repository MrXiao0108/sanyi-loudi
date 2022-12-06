package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzProductDetectionTemplateMapper;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.product.DzProductDetectionTemplateParms;
import com.dzics.common.service.DzProductDetectionTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@SuppressWarnings("ALL")
@Service
public class DzProductDetectionTemplateServiceImpl extends ServiceImpl<DzProductDetectionTemplateMapper, DzProductDetectionTemplate> implements DzProductDetectionTemplateService {

    @Autowired
    private DzProductDetectionTemplateMapper detectionTemplateMapper;

    @Override
    public List<DzProductDetectionTemplateParms> listGroupBy(String field, String type, String productName, Long departId, String orderId, String lineId) {
        List<DzProductDetectionTemplateParms> templates = detectionTemplateMapper.listGroupBy(field, type, productName, departId,orderId,lineId);
        return templates;
    }

    @Override
    public List<Map<String, Object>> listProductNo(String productNo,String orderNo,String lineNo) {
        return detectionTemplateMapper.listMap(productNo,orderNo,lineNo);
    }

    @Override
    public List<DzDetectTempVo> selProductTemplateProductId(String orderId, String lineId, String productNo) {
        List<DzDetectTempVo> vos = detectionTemplateMapper.productId(orderId,lineId,productNo);
        return vos;
    }

    @Override
    public DzProductDetectionTemplate getProductNoItem(String productNo, String item) {
        return detectionTemplateMapper.getProductNoItem(productNo, item);

    }

    @Override
    public List<Map<String, Object>> listProductId(String productNo) {
        return detectionTemplateMapper.listProductIdMap(productNo);
    }

    @Override
    public List<Map<String, Object>> getDefoutDetectionTemp() {
        return detectionTemplateMapper.getDefoutDetectionTemp();
    }


}
