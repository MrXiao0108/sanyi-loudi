package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.DzWorkingFlow;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetWorkDateDo;
import com.dzics.common.model.response.mom.WorkStationParms;
import com.dzics.common.model.response.productiontask.ProDetection;
import com.dzics.common.model.response.productiontask.WorkingProcedureStation;
import com.dzics.common.model.response.productiontask.station.ResponseWorkStation;
import com.dzics.common.model.response.productiontask.station.WorkingFlowRes;
import com.dzics.common.model.response.productiontask.stationbg.ResponseWorkStationBg;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工件制作流程记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
public interface DzWorkingFlowService extends IService<DzWorkingFlow> {

    DzWorkingFlow getQrCodeStationCode(String stationId, String qrCode, Long orderId, Long lineId);

    Result<GetWorkDateDo> getDayWorkDate(DzProductionLine line) throws ParseException;

    /**
     * 报工看板以工序展示的数据返回
     * @param list 工件二维码
     * @return
     */
    List<ResponseWorkStation> getWorkpiecePosition(List<String> list);

    /**
     * 去除重复工件，生成最新更新时间
     * map 键 是工件 二维码
     * @param workingFlowRes
     * @return
     */
    Map<String, WorkingFlowRes> toRepeat(List<WorkingFlowRes> workingFlowRes,List<String> qrCodes);



    /**
     * 获取所有工序包含工位信息
     * 下绑定的工位信息
     *
     * @return
     */
    Map<String,WorkingProcedureStation> getWorkingProcedure();

    /**
     * 获取最近检测位置的工件信息
     *
     * @param qrCode
     * @return
     */
    List<WorkingFlowRes> getWorkingFlow(List<String> qrCode,Long orderId,Long lineId);


    /**
     * 报工看板 以工位展示数据
     * @param qrCode
     * @param orderId
     * @param lineId
     * @return
     */
    List<ResponseWorkStationBg> getPosition(List<String> qrCode, Long orderId, Long lineId);

    /**
     *
     * @param outInputType 报工类型 1进开工 2出完工
     * @param processFlowId
     * @param isSendOK 是否报工成功
     * @return
     */
    boolean updateQrcodeOutInptType(String outInputType, String processFlowId, String isSendOK);

    Result getLineWorkPostion(WorkStationParms workStationParms);

    /**
     * @param qrCode 工件二维码
     * @param orderId 订单
     * @param lineId 产线
     * @return 返回以表格形式展示的报工数据
     */
    ProDetection getPositionTable(List<String> qrCode, Long orderId, Long lineId,String single);

}
