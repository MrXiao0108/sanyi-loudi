package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.DzWorkStationManagementMapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.exception.BindQrCodeException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.constant.QrCode;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.entity.*;
import com.dzics.common.service.DzWorkingFlowBigService;
import com.dzics.common.service.DzWorkingFlowService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.model.EqMentStatus;
import com.dzics.data.acquisition.service.AccOrderQrCodeService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.WorkingFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
@Service
@Slf4j
public class WorkingFlowServiceImpl implements WorkingFlowService {
    @Autowired
    private MomOrderAgvServiceImpl momOrderAgvService;
    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;
    @Autowired
    private DzWorkingFlowBigService dzWorkingFlowBigService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private AccOrderQrCodeService accOrderQrCodeService;
    @Autowired
    private DzWorkStationManagementMapper stationManagementMapper;

    /**
     * 1-10 工位 重置01
     * 11-15  重置 11
     *
     * @param rabbitmqMessage
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ReqWorkQrCodeOrder processingData(RabbitmqMessage rabbitmqMessage) {

        String deviceType = rabbitmqMessage.getDeviceType();
        String message = rabbitmqMessage.getMessage();
        String orderCode = rabbitmqMessage.getOrderCode();
        String deviceCode = rabbitmqMessage.getDeviceCode();
//        两米活塞杆 三米活塞杆 重置 01 -15

        if ("DZ-1871".equals(orderCode) || "DZ-1872".equals(orderCode) || "DZ-1873".equals(orderCode) || "DZ-1874".equals(orderCode) || "DZ-1875".equals(orderCode) || "DZ-1876".equals(orderCode) || "DZ-1877".equals(orderCode)) {
//            两米活塞杆
            if (deviceCode.equals("02") || deviceCode.equals("03") || deviceCode.equals("04") || deviceCode.equals("05")
                    || deviceCode.equals("06") || deviceCode.equals("07") || deviceCode.equals("08") || deviceCode.equals("09")
                    || deviceCode.equals("10") || deviceCode.equals("11") || deviceCode.equals("12") || deviceCode.equals("13") || deviceCode.equals("14")
                    || deviceCode.equals("15")) {
                deviceCode = "01";
            } else if (deviceCode.equals("26")) {
                deviceCode = "25";
//                磨床一序缓存一  工位 25
//                磨床一序缓存二  工位 26
            } else if (deviceCode.equals("28") || deviceCode.equals("33")) {
                deviceCode = "27";
//             机床缓存台三 工位 27
//             机床缓存台四 工位 28
//             机床缓存台五 工位 33
            }
        } else if ("DZ-1878".equals(orderCode) || "DZ-1879".equals(orderCode) || "DZ-1880".equals(orderCode)) {
            //三米活塞杆
            if ("02".equals(deviceCode) || "03".equals(deviceCode) || "04".equals(deviceCode) || "05".equals(deviceCode)
                    || "06".equals(deviceCode) || "07".equals(deviceCode) || "08".equals(deviceCode) || "09".equals(deviceCode)
                    || "10".equals(deviceCode) || "11".equals(deviceCode) || "12".equals(deviceCode) || "13".equals(deviceCode) || "14".equals(deviceCode)
                    || "15".equals(deviceCode)) {
                deviceCode = "01";
            }
        } else if ("DZ-1887".equals(orderCode) || "DZ-1888".equals(orderCode) || "DZ-1889".equals(orderCode)) {
//            两米缸筒
          /*  if (deviceCode.equals("21")) {
                deviceCode = "18";
//                机床1加工   18
//                机床2加工  21
            } else*/
            if ("20".equals(deviceCode)) {
//                17 机床1中转
//                20  机床2中转
                deviceCode = "17";
            } else if ("22".equals(deviceCode)) {
                deviceCode = "19";
//                19 机床1中转2
//                22 机床2中转2
            } else if ("35".equals(deviceCode)) {
                deviceCode = "34";
//                34 清洗机清洗1
//                35 清洗机清洗2
            } else if ("02".equals(deviceCode)) {
                deviceCode = "01";
//                01 机器人缓存1
//                02 机器人缓存2
            }
          /*else if (deviceCode.equals("12")) {
                deviceCode = "10";
//                10 焊接岛1
//                12 焊接岛2
            }*/
            else if ("33".equals(deviceCode)) {
                deviceCode = "32";
//                32 清洗机缓存台1
//                33 清洗机缓存台2
            } else if ("38".equals(deviceCode) || "39".equals(deviceCode) || "40".equals(deviceCode)) {
                deviceCode = "37";
//                 OK缓存1  37
//                 OK缓存2  38
//                 OK缓存3  39
//                 OK缓存4  40
            }

        } else if ("DZ-1955".equals(orderCode) || "DZ-1956".equals(orderCode)) {
//            两米粗加工
            if ("38".equals(deviceCode) || "39".equals(deviceCode) ||
                    "40".equals(deviceCode) || "41".equals(deviceCode)
                    || "42".equals(deviceCode)) {
                deviceCode = "37";
//            37 OK出料缓存1
//            38 OK出料缓存2
//            39 校直NG缓存1
//            40 校直NG缓存2
//            41 焊接NG缓存1
//            42 焊接NG缓存2
            } else if ("11".equals(deviceCode)) {
                deviceCode = "10";
//                10 OK出料小车A
//                11 OK出料小车B
//                12 NG出料小车C
            }
        }


        String timestamp = rabbitmqMessage.getTimestamp();
        String lineNo = rabbitmqMessage.getLineNo();
//            校验指令信息，设备类型是否正确
        String[] check = checkParmsDeviceTypeMessage(message, deviceType);
        if (check != null) {
//                校验订单信息是否存在
            OrderIdLineId line = getOrderNoLineNoId(orderCode, lineNo);
            if (line != null) {
//            校验工位信息
                Long orderId = line.getOrderId();
                Long lineId = line.getLineId();
//                根据产线 订单 工位编号，查询工位信息
                DzWorkStationManagement workStation = getWorkStation(deviceCode, orderId, lineId, orderCode, lineNo);
                DzWorkStationManagement workStationSpare = null;
                if (workStation != null) {
                    if (!StringUtils.isEmpty(workStation.getMergeCode())) {
                        workStationSpare = getWorkStation(orderId, lineId, deviceCode, workStation.getMergeCode(), orderCode, lineNo);
                    }
                    String outInputType = check[0];
                    String qrcode = check[1];
                    try {
                        if (qrcode.contains("_")) {
                            int index = qrcode.indexOf("_") + 1;
                            qrcode = qrcode.substring(index,qrcode.length());
                        }
                    } catch (Throwable throwable) {
                        log.error("处理报工数据处理二维码错误：{}", throwable.getMessage(), throwable);
                    }
                    MonOrder orderQrCode = cacheService.getMomOrderNoProducBarcode(qrcode, orderCode, lineNo);
                    if (orderQrCode == null) {
                        log.error("处理报工数据: 识别到当前二维码：{}为异常件，未经过1202信号处理，数据库中不存在当前二维码记录，不做处理",qrcode);
                        throw new BindQrCodeException(CustomExceptionType.SYSTEM_ERROR, "当前二维码："+qrcode + "为异常件，未经过1202信号处理，数据库中不存在当前二维码记录，不做处理");
//                        //绑定二维码,增加生成数量,获取当前生产报工中的订单
//                        MonOrder startOrder = momOrderService.getMomOrder(orderCode, lineNo, MomProgressStatus.LOADING);
//                        if (startOrder != null) {
//                            Integer sum = accOrderQrCodeService.bandMomOrderQrCode(qrcode, startOrder, orderCode, lineNo);
//                            momOrderAgvService.updateOrderStateSum(DzUdpType.UDP_CMD_CONTROL_SUM, "", orderCode, lineNo, startOrder, sum);
//                        } else {
//                            log.warn("处理报工数据: 二维码绑定订单时MOM生产订单不存在,订单：{},产线：{},订单状态：{},startOrder: {}", orderCode, lineNo, MomProgressStatus.LOADING, startOrder);
//                            throw new BindQrCodeException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, orderCode + ": 当前生产订单不存在");
//                        }
                    }
//                    生成工序
                    ReqWorkQrCodeOrder qrCodeOrder = workFlow(line, workStation, timestamp, qrcode, outInputType, rabbitmqMessage.getDeviceCode(), workStationSpare);
                    if (qrCodeOrder != null) {
                        qrCodeOrder.setOutFlag(workStation.getOutFlag());
                        qrCodeOrder.setNgCode(workStation.getNgCode());
                    }
                    return qrCodeOrder;
                }
            }
        }
        return null;

    }

    private DzWorkStationManagement getWorkStation(Long orderId, Long lineId, String deviceCode, String mergeCode, String orderCode, String lineNo) {
        try {
            DzWorkStationManagement stationManagement = cacheService.getStationIdMergeCode(orderId, lineId, deviceCode, mergeCode);
            if (stationManagement != null) {
                return stationManagement;
            }
            log.warn("根据工位编号：{},订单: {},产线: {},没有获取到工位信息", deviceCode, orderCode, lineNo);
        } catch (Throwable throwable) {
            log.error("查找备用工位失败订单: {},产线: {} 工位:{},合并标识：{},错误信息：{}", orderCode, lineNo, deviceCode, mergeCode, throwable.getMessage(), throwable);
        }
        return null;
    }

    /**
     * 校验指令信息，设备类型是否正确
     *
     * @param message
     * @param deviceType
     * @return 返回指令处理后信息
     */
    private String[] checkParmsDeviceTypeMessage(String message, String deviceType) {
        if (!StringUtils.isEmpty(message)) {
            String[] split = message.split("\\|");
            if (split.length < 2) {
                return null;
            } else {
                String cmd = split[0];
                if (cmd.equals(EqMentStatus.CMD_ROB_QRCODE_TRACE)) {
                    String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(split[1], "[");
                    deviceItemValue = deviceItemValue.substring(0, deviceItemValue.length() - 1);
                    String[] msg = deviceItemValue.split(",");
                    if (msg.length < 2) {
                        if (msg.length == 1) {
                            String[] msgx = new String[2];
                            msgx[0] = msg[0];
                            msgx[1] = "";
                            return msgx;
                        }
                        log.warn("处理工件位置队列数据,指令信息错误：根据，号分号后长度应该>=2，device_item_value：{}", deviceItemValue);
                        return null;
                    } else {
                        if (deviceType.equals(String.valueOf(EquiTypeEnum.EQCODE.getCode()))) {
                            return msg;
                        } else {
                            log.warn("设备类型错误：{} 不是检测工件位置的设备，设备类型应该是：6", deviceType);
                            return null;
                        }
                    }
                } else {
                    log.warn("处理工件位置队列数据，数据中指令错误：CMD：{}", cmd);
                    return null;
                }
            }
        } else {
            log.error("处理工件位置队列数据，数据错误没有指令数据内容：message：{}", message);
            return null;
        }
    }

    /**
     * 校验工位信息是否存在
     *
     * @param deviceCode
     * @param orderId
     * @param lineId
     * @param orderCode
     * @param lineNo
     * @return
     */
    private DzWorkStationManagement getWorkStation(String deviceCode, Long orderId, Long lineId, String orderCode, String lineNo) {
        DzWorkStationManagement stationManagement = cacheService.getStationId(deviceCode, orderId, lineId);
        if (stationManagement != null) {
            return stationManagement;
        }
        log.warn("根据工位编号：{},订单: {},产线: {},没有获取到工位信息", deviceCode, orderCode, lineNo);
        return null;
    }

    /**
     * 校验订单信息是否存在
     *
     * @param orderCode
     * @param lineNo
     * @return
     */
    private OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo) {
        OrderIdLineId orderIdLineId = cacheService.getOrderNoLineNoId(orderCode, lineNo);
        if (orderIdLineId != null) {
            return orderIdLineId;
        }
        log.error("根据订单号和产线号没有获取到 [订单ID 和产线ID] 无法进行后续设置工件位置存储：orderNo:{},lineNo:{},orderIdLineId:{}", orderCode, lineNo, orderIdLineId);
        return null;
    }

    /**
     * @param orderIdLineId    产线Id，订单Id  产线序号 订单序号
     * @param saMt             工位信息
     * @param timestamp        时间
     * @param qrCode           二维码
     * @param outInputType     1=去工位放料进   2=去工位取料  出
     * @param deviceCode
     * @param workStationSpare
     * @return
     */
//    {"completeTime":1627887568585,"lineNo":"1","orderNo":"DZ-1875","outInputType":"2",
//    "proTaskId":"1421480090002411521","processFlowId":"1422089650999111682","productNo":"11473081",
//    "qrCode":"SANYI072700021","stationCode":"18","stationId":"1419623082255941634","wipOrderNo":"86500012563","workCode":"G01"}
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ReqWorkQrCodeOrder workFlow(OrderIdLineId orderIdLineId, DzWorkStationManagement saMt, String timestamp, String qrCode, String outInputType, String deviceCode, DzWorkStationManagement workStationSpare) {
        String stationId = saMt.getStationId();
        String stationCode = saMt.getStationCode();
        Long orderId = orderIdLineId.getOrderId();
        Long lineId = orderIdLineId.getLineId();
        String orderNo = orderIdLineId.getOrderNo();
        String lienNo = orderIdLineId.getLienNo();
        MonOrder startWokeOrderMooM = cacheService.getMomOrderNoProducBarcode(qrCode, orderNo, lienNo);
        if (startWokeOrderMooM == null) {
            log.error("当前订单 {}, 产线: {} ,二维码：{},没有绑定订单", orderNo, lienNo, qrCode);
            return null;
        }
        ReqWorkQrCodeOrder qrCodeOrder = new ReqWorkQrCodeOrder();
        qrCodeOrder.setLineId(lineId);
        qrCodeOrder.setOrderId(orderId);
        qrCodeOrder.setStationId(stationId);
        qrCodeOrder.setProductNo(startWokeOrderMooM.getProductNo());
        qrCodeOrder.setOutInputType(outInputType);
        qrCodeOrder.setProTaskId(startWokeOrderMooM.getProTaskOrderId());
        qrCodeOrder.setWipOrderNo(startWokeOrderMooM.getWiporderno());
        qrCodeOrder.setDzStationCode(saMt.getDzStationCode());
        if (workStationSpare != null) {
            qrCodeOrder.setDzStationCodeSpare(workStationSpare.getDzStationCode());
        }
        DzWorkingFlow dzWorkingFlow = dzWorkingFlowService.getQrCodeStationCode(stationId, qrCode, orderId, lineId);
        Date date = DateUtil.stringDateToformatDate(timestamp);
        LocalDate nowLocalDate = DateUtil.dataToLocalDate(date);
        String workingProcedureId = saMt.getWorkingProcedureId();

        if (dzWorkingFlow == null) {
//                                      插入工件当前位置
            dzWorkingFlow = new DzWorkingFlow();
            dzWorkingFlow.setLineId(lineId);
            dzWorkingFlow.setOrderId(orderId);
//                                     获取工序id
            dzWorkingFlow.setWorkingProcedureId(workingProcedureId);
            dzWorkingFlow.setStationId(stationId);
            dzWorkingFlow.setProTaskId(startWokeOrderMooM.getProTaskOrderId());
            dzWorkingFlow.setQrCode(qrCode);
            dzWorkingFlow.setWorkpieceCode(startWokeOrderMooM.getWiporderno());
            dzWorkingFlow.setWorkDate(nowLocalDate);

            if (outInputType.equals(QrCode.QR_CODE_OUT)) {
                List<DzWorkStationManagement> dzWorkStationManagements = stationManagementMapper.selectList(new QueryWrapper<DzWorkStationManagement>().eq("order_id", orderId).eq("on_off",1).orderByDesc("sort_code"));
                if(dzWorkStationManagements.get(0).getStationId().equals(stationId)){
                    //说明当前是出料工序
//                        设置开始时间
                    dzWorkingFlow.setStartTime(date);
                }
//              设置结束时间
                dzWorkingFlow.setCompleteTime(date);
                dzWorkingFlow.setRemarks(QrCode.RRAMARKS);
                dzWorkingFlowService.save(dzWorkingFlow);
                saveBig(qrCode, nowLocalDate, date, orderId, lineId, orderNo, lienNo);
                qrCodeOrder.setProcessFlowId(dzWorkingFlow.getProcessFlowId());
                qrCodeOrder.setCompleteTime(date);
                qrCodeOrder.setQrCode(qrCode);
                return qrCodeOrder;
            } else if (outInputType.equals(QrCode.QR_CODE_IN)) {
//                                   设置开始时间
                dzWorkingFlow.setStartTime(date);
                dzWorkingFlowService.save(dzWorkingFlow);
                saveBig(qrCode, nowLocalDate, date, orderId, lineId, orderNo, lienNo);
                qrCodeOrder.setProcessFlowId(dzWorkingFlow.getProcessFlowId());
                qrCodeOrder.setStartTime(date);
                qrCodeOrder.setQrCode(qrCode);
                return qrCodeOrder;
            } else {
                log.warn("订单:{}, 产线: {} ,重置后工位: {},原始工位:{} 处理工件位置队列数据,获取到的进出指令类型错误：outInputType：{},应该是：1=去工位放料 2=去工位取料", orderNo, lienNo, stationCode, outInputType, deviceCode);
                return null;
            }
        } else {
//                              更新工件出去时间
            if (outInputType.equals(QrCode.QR_CODE_OUT)) {
                if (dzWorkingFlow.getCompleteTime() == null) {
                    dzWorkingFlow.setCompleteTime(date);
                    dzWorkingFlowService.updateById(dzWorkingFlow);
                    qrCodeOrder.setProcessFlowId(dzWorkingFlow.getProcessFlowId());
                    qrCodeOrder.setCompleteTime(date);
                    qrCodeOrder.setQrCode(qrCode);
                    return qrCodeOrder;
                } else {
                    log.warn("已存在该二维码:{} 的出去工位记录，不处理,订单: {},产线: {} ,重置后工位: {},原始工位:{} ", qrCode, orderNo, lienNo, stationCode, deviceCode);
                    return null;
                }
//                                   设置结束时间
            } else if (outInputType.equals(QrCode.QR_CODE_IN)) {
                if (dzWorkingFlow.getStartTime() == null) {
                    dzWorkingFlow.setStartTime(date);
                    dzWorkingFlowService.updateById(dzWorkingFlow);
                    qrCodeOrder.setProcessFlowId(dzWorkingFlow.getProcessFlowId());
                    qrCodeOrder.setStartTime(date);
                    qrCodeOrder.setQrCode(qrCode);
                    log.warn("已存在该二维码:{} 的进入工位记录，但是没有收到开始时间，先收到结束时间，后续才收到开始时间，处理, 订单: {} ,产线: {} ,重置后工位: {},原始工位:{} ", qrCode, orderNo, lienNo, stationCode, deviceCode);
                    return qrCodeOrder;
                } else {
                    log.warn("已存在该二维码:{} 的进入工位记录，该信号重复，不做处理,订单: {}, 产线: {} ,重置后工位: {},原始工位:{}  ", qrCode, orderNo, lienNo, stationCode, deviceCode);
                }
                return null;
            } else {
                log.warn("订单:{}, 产线: {} ,重置后工位: {} ,原始工位:{} 处理工件位置队列数据,获取到的进出指令类型错误：outInputType：{},应该是：1=去工位放料 2=去工位取料", orderNo, lienNo, stationCode, outInputType, deviceCode);
                return null;
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveBig(String qrCode, LocalDate nowLocalDate, Date workTime, Long orderId, Long lineId, String orderNo, String lienNo) {
        QueryWrapper<DzWorkingFlowBig> wp = new QueryWrapper<>();
        wp.eq("qr_code", qrCode);
        wp.eq("order_id", orderId);
        wp.eq("line_id", orderId);
        DzWorkingFlowBig one = dzWorkingFlowBigService.getOne(wp);
        if (one != null) {
            return;
        }
        LocalTime now = LocalTime.now();
        DzLineShiftDay lineShiftDays = cacheService.getLingShifuDay(lienNo, "01", String.valueOf(EquiTypeEnum.JQR.getCode()), orderNo, nowLocalDate, now);
//        根据机器人上传时间,获取班次名称
        DzWorkingFlowBig big = new DzWorkingFlowBig();
        big.setWorkName(lineShiftDays != null ? lineShiftDays.getWorkName() : null);
        big.setLineId(lineId);
        big.setOrderId(orderId);
        big.setQrCode(qrCode);
        big.setWorkDate(nowLocalDate);
        big.setWorkTime(workTime);
        dzWorkingFlowBigService.save(big);
    }
}
