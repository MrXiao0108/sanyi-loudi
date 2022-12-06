package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.WmsOrderConfigMapper;
import com.dzics.common.model.entity.WmsOrderConfig;
import com.dzics.common.model.response.wms.GetOrderCfig;
import com.dzics.common.service.WmsOrderConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
@Service
public class WmsOrderConfigServiceImpl extends ServiceImpl<WmsOrderConfigMapper, WmsOrderConfig> implements WmsOrderConfigService {

    @Override
    public List<GetOrderCfig> getCfg(String field, String type) {
        return baseMapper.getCfg(field, type);
    }

    @Override
    public WmsOrderConfig getMaterialCode(String materialCode) {
        QueryWrapper<WmsOrderConfig> wms = new QueryWrapper<>();
        wms.eq("order_status", false);
        wms.eq("material_code", materialCode);
        WmsOrderConfig one = getOne(wms);
        return one;
    }

    @Override
    public WmsOrderConfig addMaterialCode(String materialCode, String orderNum, String rfid) {
        WmsOrderConfig wmsOrderConfig = new WmsOrderConfig();
        wmsOrderConfig.setRfid(rfid);
        wmsOrderConfig.setOrderNum(orderNum);
        wmsOrderConfig.setMaterialCode(materialCode);
        wmsOrderConfig.setOrgCode("WMS-000");
        wmsOrderConfig.setDelFlag(false);
        wmsOrderConfig.setCreateBy("UI");
        this.save(wmsOrderConfig);
        return wmsOrderConfig;
    }

    @Override
    public void updateOrderNum(String orderNum) {
        WmsOrderConfig wmsOrderConfig = new WmsOrderConfig();
        wmsOrderConfig.setOrderStatus(true);
        QueryWrapper<WmsOrderConfig> wp = new QueryWrapper<>();
        wp.eq("order_num", orderNum);
        this.update(wmsOrderConfig, wp);
    }
}
