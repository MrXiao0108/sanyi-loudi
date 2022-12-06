package com.dzics.common.service;

import com.dzics.common.model.entity.MomMaterialPoint;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.request.mom.GetFeedingAgvVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.DzicsStationCode;

/**
 * <p>
 * 料点编码 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
public interface MomMaterialPointService extends IService<MomMaterialPoint> {
    /**
     * 查询AGV投料点
     */
    Result getFeedingPoints(GetFeedingAgvVo getFeedingAgvVo);




    /**
     * 删除AGV投料点
     *
     * @return
     */
    Result delFeedingPoint(String materialPointId);


    /**
     * @param orderCode  订单
     * @param lineNo     产线
     * @param basketType 小车编号
     * @return 料点工位信息
     */
    MomMaterialPoint getOrderLineNoBasketType(String orderCode, String lineNo, String basketType);

    MomMaterialPoint getOrderLineNoBasketTypeNg(String orderCode, String lineNo);

    String getNextPoint(String orderCode, String lineNo,String basketType);

    String getNextPoint(Long orderId, Long id);

    String getDzStationCode(Long orderId, Long lineId, String stationCode, String orderNo, String lineNo);

    Result getDzicsStationCode(String lineId);

    MomMaterialPoint getProint(String destNo, String lineNo, String orderNo);
}
