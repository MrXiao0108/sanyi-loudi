package com.dzics.common.service;

import com.dzics.common.model.entity.WmsOrderConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.wms.GetOrderCfig;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
public interface WmsOrderConfigService extends IService<WmsOrderConfig> {

    List<GetOrderCfig> getCfg(String field, String type);

    WmsOrderConfig getMaterialCode(String materialCode);


    WmsOrderConfig addMaterialCode(String materialCode, String orderNum, String rfid);

    void updateOrderNum(String orderNum);
}
