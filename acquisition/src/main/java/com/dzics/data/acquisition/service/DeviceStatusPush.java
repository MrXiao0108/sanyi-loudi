package com.dzics.data.acquisition.service;


import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.model.SocketDowmSum;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 设备状态推送接口
 *
 * @author ZhangChengJun
 * Date 2021/3/25.
 * @since
 */
public interface DeviceStatusPush {

    void sendReatimLogs(SysRealTimeLogs b);

    /**
     * 发送给前端
     * @param dzWorkpieceData 检测设备数据
     * @return
     */
    boolean sendWorkpieceData(DzWorkpieceData dzWorkpieceData);

    /**
     * 发送设备状态
     * @param dzEquipment
     */
    void sendStateEquiment(DzEquipment dzEquipment);

    /**
     * 工件编码
     * @param qrCode
     */
    void getWorkingFlow(ReqWorkQrCodeOrder qrCode);

    /**
     * 发送停机记录
     * @param dowmSum
     */
    void senddeviceStopStatusPush(SocketDowmSum dowmSum);
    Result getSendBaseRunState(SocketDowmSum dzEquipment);
    void dzRefresh(String msg);

    void sendRabbitmqRealTimeLogs(List<RabbitmqMessage> rabbitmqMessageList);

    /**
     *
     * 检测项多项数据折线图推送
     * @return
     *
     */
    boolean sendSanYiDetectionCurve(DzWorkpieceData dzWorkpieceData) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    /**
     * 单项检测推送
     * @param dzWorkpieceData
     * @return
     */
    boolean sendSingleProbe(DzWorkpieceData dzWorkpieceData);

    /**
     * 推送刀具信息
     * @param result
     * @return
     */
    boolean sendToolDetection(RabbitmqMessage rabbitmqMessage,Result result);

    void sendSysRealTimeLogs(SysRealTimeLogs sysRealTimeLogs);

    void sendMomReceiveMaterial(MomReceiveMaterial message);

    /**
     * 发送最新状态到前端页面
     * @param momOrder
     * @param i
     */
    void sendMomOrderRef(MonOrder momOrder, int i);

    /**
     * 发送扫描JSON 数据
     * @param split
     */
    void pushFrdiJson(String[] split);

    /**
     * 发送扫描原始数据
     * @param split
     */
    void pushFridOld(String[] split);

    /**
     * 发送到前端 提示用湖输入二维码
     * @param orderNo
     * @param lineNo
     */
    void sendInputQrCode(String orderNo, String lineNo);

    /**
     * 根据机床区分检测数据趋势图
     * @param dzWorkpieceData
     * @return
     */
    boolean sendDetectionByMachine(DzWorkpieceData dzWorkpieceData);

    void sendRabbitmqRealTimeLogsPush(List<RabbitmqMessage> rabbitmqMessageList);


    /**
     * 转发检测数据到 mom上发队列
     * @param dzWorkpieceData
     * @return
     */

    void sendUploadQualityParam(DzWorkpieceData dzWorkpieceData);

    /**
     * 智能检测推送
     * @param dzWorkpieceData
     * @return
     */
    boolean sendIntelligentDetection(DzWorkpieceData dzWorkpieceData) throws Exception;

    /**
     * 检测记录推送
     * @param orderNo,lineNO,qrCode
     * @return
     */
    boolean sendDetection(String orderNo,String lineNo,String Id);

    /**
     * 人工打磨看板 实时监控数据推送
     * */
    boolean sendDetectionMonitor(String orderNo,String lineNo,String qrCode);
}
