package com.dzics.business.service.agv.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.config.MapConfig;
import com.dzics.business.model.vo.udp.SendPlcModel;
import com.dzics.business.service.agv.AgvService;
import com.dzics.business.service.mq.RabbitmqService;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.agv.search.MomResultSearch;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.MomOrderQrCode;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.qrCode.QrCodeParms;
import com.dzics.common.model.request.agv.AgvClickSignal;
import com.dzics.common.model.request.agv.AgvClickSignalConfirmV2;
import com.dzics.common.model.request.dzcheck.DzOrderCheck;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzProductionLineService;
import com.dzics.common.service.MomOrderQrCodeService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.service.MomReceiveMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AgvServiceImpl implements AgvService {
    @Autowired
    private MomOrderQrCodeService momOrderQrCodeService;
    @Autowired
    private RabbitmqService rabbitmqService;
    @Value(("${accq.read.cmd.queue.equipment.realTime}"))
    private String logQuery;

    @Autowired
    private MapConfig mapConfig;

    @Value("${dzdc.udp.client.qr.port}")
    private Integer plcPort;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MomOrderService momOrderService;

    @Autowired
    private MomReceiveMaterialService receiveMaterialService;

    @Autowired
    private DzProductionLineService dzProductionLineService;

    /**
     * 来料信号确认
     *
     * @param clickSignal
     * @return
     */
    @Override
    public Result chlickSignal(AgvClickSignal clickSignal) {
        SysRealTimeLogs timeLogs = new SysRealTimeLogs();
        String orderNo = clickSignal.getOrderNo();
        String lineNo = clickSignal.getLineNo();
        String msgID = UUID.randomUUID().toString().replaceAll("-", "");
        timeLogs.setMessageId(msgID);
        timeLogs.setQueueName(logQuery);
        timeLogs.setClientId(LogClientType.BUS_AGV);
        timeLogs.setOrderCode(orderNo);
        timeLogs.setLineNo(lineNo);
        timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
        timeLogs.setDeviceCode(clickSignal.getBasketType());
        timeLogs.setMessageType(1);
        timeLogs.setMessage("收到 " + clickSignal.getBasketType() + " 号小车来料信号");
        timeLogs.setTimestampTime(new Date());
        //     发送到日志队列
        boolean b = rabbitmqService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
        //      将信号发送到 mq 触发到 UDP 发送
        Map<String, String> mapIps = mapConfig.getMaps();
        String plcIp = mapIps.get(orderNo + lineNo);
        if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
            String palletNo = clickSignal.getPalletNo();
            if (StringUtils.isEmpty(palletNo)) {
                palletNo = "";
                log.warn("AGV 到位信号料框编码为空：palletNo: {}", palletNo);
            }
            SendPlcModel sendPlcModel = new SendPlcModel();
            sendPlcModel.setIp(plcIp);
            sendPlcModel.setPort(plcPort);
            sendPlcModel.setMessage("Q," + DzUdpType.UDP_TYPE_AGV + "," + DzUdpType.udpTypeAgvSinal + "," + clickSignal.getBasketType() + "," + msgID + "," + orderNo + "," + lineNo + "," + palletNo);
            rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
        } else {
            log.error("小车来料信号发送数据到UDP IP 配置不存在orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
        }

        return Result.ok();
    }


    private void sendLogMq(String basketType, String message, String orderNo, String lineNo, String msgID) {
        try {
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(msgID);
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(basketType);
            timeLogs.setMessageType(1);
//                .订单号:" + momOrderNo + " 物料号: " + material + " 数量: " + prodCount
            timeLogs.setMessage(message);
            timeLogs.setTimestampTime(new Date());
            //     发送到日志队列
            boolean b = rabbitmqService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
        } catch (Throwable throwable) {
            log.error("确认物料信息发送至消息队列错误：{} ", throwable.getMessage(), throwable);
        }
    }


    @Override
    public Result checkOrder(DzOrderCheck dzOrderCheck) {
        log.info("校验订单参数：{}", dzOrderCheck);
//           发送到页面  存储上发物料信息
        String momOrderNo = dzOrderCheck.getMomOrderNo();
        momOrderNo = getString(momOrderNo);

        String material = dzOrderCheck.getMaterial();
        material = getString(material);

        String workNo = dzOrderCheck.getWorkNo();
        workNo = getString(workNo);

        String palletNo = dzOrderCheck.getPalletNo();
        palletNo = getString(palletNo);

        String prodCount = dzOrderCheck.getProdCount();
        prodCount = getString(prodCount);
        if (StringUtils.isEmpty(prodCount)) {
            prodCount = "0";
        }
        String orderNo = dzOrderCheck.getOrderCode();
        orderNo = getString(orderNo);

        String lineNo = dzOrderCheck.getLineNo();
        lineNo = getString(lineNo);

        String basketType = dzOrderCheck.getBasketType();
        basketType = getString(basketType);
//        存储机器人发送来的物料信息
        MomReceiveMaterial momReceiveMaterial = getMomReceiveMaterial(dzOrderCheck, momOrderNo, material, workNo, palletNo, prodCount, orderNo, lineNo, basketType);
//        托盘号 必有()  数量 工序 物料号   订单号
        Result ok = Result.ok();
        try {
            ok.setData("ERRERR");
            if (StringUtils.isEmpty(palletNo)) {
                log.error("机器人扫码RFID 托盘编号|料框编号 palletNo 不存在:{}", palletNo);
                return ok;
            }
            if ("0".equals(prodCount) && StringUtils.isEmpty(workNo) && StringUtils.isEmpty(material) && StringUtils.isEmpty(momOrderNo)) {
                ok.setData("OKOK");
                log.warn("机器人扫码RFID prodCount:{} ,workNo:{} ,material:{},momOrderNo:{}  ", prodCount, workNo, material, momOrderNo);
                return ok;
            }
            Result result = momOrderService.checkOrder(dzOrderCheck);
//            来料日志发送
            return result;
        } catch (Throwable throwable) {
            return ok;
        } finally {
            //          发送到  socket 页面
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            String msgID = UUID.randomUUID().toString().replaceAll("-", "");
            timeLogs.setMessageId(msgID);
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(basketType);
            timeLogs.setMessageType(1);
            timeLogs.setMessage(JSONObject.toJSONString(momReceiveMaterial));
            timeLogs.setTimestampTime(new Date());
//     发送到日志队列
            boolean b = rabbitmqService.sendRabbitmqLogMaterial(JSONObject.toJSONString(timeLogs));
        }
    }

    private MomReceiveMaterial getMomReceiveMaterial(DzOrderCheck dzOrderCheck, String momOrderNo, String material, String workNo, String palletNo, String prodCount, String orderNo, String lineNo, String basketType) {
        MomReceiveMaterial momReceiveMaterial = new MomReceiveMaterial();
        momReceiveMaterial.setGuid(dzOrderCheck.getGuid());
        momReceiveMaterial.setMomOrderNo(momOrderNo);
        momReceiveMaterial.setOrderNo(orderNo);
        momReceiveMaterial.setLineNo(lineNo);
        momReceiveMaterial.setMaterialNo(material);
        momReceiveMaterial.setWorkNo(workNo);
        momReceiveMaterial.setPalletNo(palletNo);
        momReceiveMaterial.setProdCount(prodCount);
        momReceiveMaterial.setOrgCode("ROB");
        momReceiveMaterial.setDelFlag(false);
        momReceiveMaterial.setCreateBy("ROB");
        momReceiveMaterial.setMaterialCheck(false);
        momReceiveMaterial.setBasketType(basketType);
        receiveMaterialService.save(momReceiveMaterial);
        return momReceiveMaterial;
    }

    @Override
    public Result getFrid(AgvClickSignal clickSignal) {
        String msgID = UUID.randomUUID().toString().replaceAll("-", "");
        String lineNo = clickSignal.getLineNo();
        String orderNo = clickSignal.getOrderNo();
        //      将信号发送到 mq 触发到 UDP 发送
        if (mapConfig == null) {
            log.error("配置IP信息不存在：{} ", mapConfig);
        }
        Map<String, String> mapIps = mapConfig.getMaps();
        if (mapIps == null) {
            log.error("配置IP信息不存在：mapIps : {} ", mapIps);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR49);
        }
        if (CollectionUtils.isNotEmpty(mapIps)) {
            String plcIp = mapIps.get(orderNo + lineNo);
            if (!StringUtils.isEmpty(plcIp)) {
                SendPlcModel sendPlcModel = new SendPlcModel();
                sendPlcModel.setIp(plcIp);
                sendPlcModel.setPort(plcPort);
                sendPlcModel.setMessage("Q," + DzUdpType.UDP_TYPE_AGV + "," + DzUdpType.udpTypeAgvSinalFRID + "," + clickSignal.getBasketType() + "," + msgID + "," + orderNo + "," + lineNo);
                rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
            } else {
                log.error("扫描FRID发送数据到UDP端  订单：{} ,产线 : {} 配置ID  不存在,mapIps : {}", orderNo, lineNo, mapIps);
            }

        } else {
            log.error("扫描FRID发送数据到UDP端  订单：{} ,产线 : {} 配置ID  不存在,mapIps : {}", orderNo, lineNo, mapIps);
        }
        return Result.ok();
    }

    @Override
    public Result chlickOkConfirmMaterialV2(AgvClickSignalConfirmV2 confirm) {
        MomReceiveMaterial receiveMaterialId = receiveMaterialService.getById(confirm.getReceiveMaterialId());
        if (receiveMaterialId == null) {
            log.error("确认来料触发 错误: 无法获取到物料信息 receiveMaterialId : {}", receiveMaterialId);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        String basketType = receiveMaterialId.getBasketType();
        String orderNo = receiveMaterialId.getOrderNo();
        String lineNo = receiveMaterialId.getLineNo();
        log.info("确认来料触发 订单:{},产线: {} ,参数: {}", orderNo, lineNo, JSONObject.toJSONString(confirm));
        if (receiveMaterialId.getMaterialCheck()) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR46);
        }

//      人工点击确认，把物料信息，传递给机器人，检验正常，发送给机器人
        try {
            receiveMaterialId.setOkNg(false);
            String msgID = UUID.randomUUID().toString().replaceAll("-", "");
            String message = "点击确认 " + basketType + " 号小车物料信息";
            sendLogMq(basketType, message, orderNo, lineNo, msgID);
            OrderIdLineId orderNoAndLineNo = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
            MonOrder progressStatus = momOrderService.getMomOrder(orderNoAndLineNo.getOrderNo(), orderNoAndLineNo.getLienNo(), MomProgressStatus.LOADING);
            if (progressStatus == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR52);
            }
            if (progressStatus.getProgressStatus().equals(MomProgressStatus.LOADING) && progressStatus.getOrderOperationResult().intValue() == 2) {

                if (confirm.getOkNg()) {
//            校验成功    Q,3,A=车辆编号  将信号发送到 mq 触发到 UDP 发送
                    Map<String, String> mapIps = mapConfig.getMaps();
                    String plcIp = mapIps.get(orderNo + lineNo);
                    if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                        SendPlcModel sendPlcModel = new SendPlcModel();
                        sendPlcModel.setIp(plcIp);
                        sendPlcModel.setPort(plcPort);
                        sendPlcModel.setMessage("Q," + DzUdpType.UDP_TYPE_AGV + "," + DzUdpType.undpAgvConfirm + "," + basketType + "," + msgID + "," + orderNo + "," + lineNo);
                        rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
                    } else {
                        log.error("确认物料信息发送到UDP端 订单：{} ，产线 ：{} 配置IP 不存在. mapIps :{}", orderNo, lineNo, mapIps);
                    }
                    return Result.ok();
                } else {
                    log.error("人工确认物料 点击 否 confirm: {},receiveMaterial: {}", confirm, receiveMaterialId);
                    return Result.ok();
                }
            } else {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR54);
            }
//
        } catch (CustomException c) {
            String message = basketType + "号小车确认物料信息检验失败";
            String msgID = UUID.randomUUID().toString().replaceAll("-", "");
            sendLogMq(basketType, message, orderNo, lineNo, msgID);
            throw c;
        } catch (Throwable throwable) {
            log.error("校验物料异常:{}", throwable.getMessage(), throwable);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR45);
        } finally {
            receiveMaterialId.setMaterialCheckTime(new Date());
            receiveMaterialId.setReceiveMaterialId(confirm.getReceiveMaterialId());
            receiveMaterialId.setMaterialCheck(true);
            receiveMaterialId.setOkNg(confirm.getOkNg());
            receiveMaterialService.updateById(receiveMaterialId);
        }

    }

    @Override
    public Result inputQrCode(QrCodeParms qrCodeParms) {
        String lineNo = qrCodeParms.getLineNo();
        String orderNo = qrCodeParms.getOrderNo();
        String qrCode = qrCodeParms.getQrCode();
        QueryWrapper<MomOrderQrCode> wp = new QueryWrapper<>();
        wp.eq("product_code", qrCode);
        wp.eq("order_no", orderNo);
        wp.eq("line_no", lineNo);
        List<MomOrderQrCode> list = momOrderQrCodeService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR56);
        }
        Map<String, String> mapIps = mapConfig.getMaps();
        if (mapIps == null) {
            log.error("配置IP信息不存在：mapIps : {} ", mapIps);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR49);
        }

        String plcIp = mapIps.get(orderNo + lineNo);
        if (!StringUtils.isEmpty(plcIp)) {
//            Q, 7, 1401, orderNo, lineNo, 二维码
            SendPlcModel sendPlcModel = new SendPlcModel();
            sendPlcModel.setIp(plcIp);
            sendPlcModel.setPort(plcPort);
            sendPlcModel.setMessage("Q," + DzUdpType.UDP_CMD_QR_CODE + "," + DzUdpType.QR_CODE_RECEIVE_OK + "," + orderNo + "," + lineNo + "," + qrCode);
            rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
            return Result.ok();
        } else {
            log.error("二维码发送数据到UDP端  订单：{} ,产线 : {} 配置ID  不存在,mapIps : {}", orderNo, lineNo, mapIps);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    @Override
    public Result processDistribution(EmptyFrameMovesDzdc emptyFrameMovesDzdc) {
        String url = "";
        String orderNo = emptyFrameMovesDzdc.getOrderCode();
        String lineNo = emptyFrameMovesDzdc.getLineNo();
        try {
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                url = "http://" + plcIp + ":8107/call/material";
                ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, emptyFrameMovesDzdc, Result.class);
                Result body = resultResponseEntity.getBody();

                log.info("机器人请求 AGV 到单岛 订单:{} ,产线：{} , URL：{},请求参数:{},响应参数：{}", orderNo, lineNo, url, JSONObject.toJSONString(emptyFrameMovesDzdc), JSONObject.toJSONString(body));
                return body;
            } else {
                log.error("机器人请求AGV IP 配置不存在orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
        } catch (Throwable throwable) {
            log.error("机器人请求AGV 订单:{} ,产线：{} , 到单岛 URL：{},请求参数:{},错误信息：{}", orderNo, lineNo, url, JSONObject.toJSONString(emptyFrameMovesDzdc), throwable.getMessage(), throwable);
            return Result.ok(throwable.getMessage());
        }
    }

    /**
     * 获取下个工序号
     *
     * @param dzdcMomSeqenceNo
     * @return
     */
    @Override
    public MomResultSearch getSanyMomNextSpecNo(SearchDzdcMomSeqenceNo dzdcMomSeqenceNo) {
        String url = "";
        String orderNo = dzdcMomSeqenceNo.getOrderCode();
        String lineNo = dzdcMomSeqenceNo.getLineNo();
        try {
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                url = "http://" + plcIp + ":8107/get/next/seqenceno";
                ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, dzdcMomSeqenceNo, Result.class);
                Result body = resultResponseEntity.getBody();
                int code = body.getCode();
                if (code == 0) {
                    MomResultSearch momResultSearch = JSONObject.parseObject(JSONObject.toJSONString(body.getData()), MomResultSearch.class);
                    return momResultSearch;
                }
                log.info("请求 MOM 获取下个工序号 到单岛 订单:{} ,产线：{} , URL：{},请求参数:{},响应参数：{}", orderNo, lineNo, url, JSONObject.toJSONString(dzdcMomSeqenceNo), JSONObject.toJSONString(body));
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR80);
            } else {
                log.error("机器人请求单岛 IP 配置不存在orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
        } catch (Throwable throwable) {
            log.error("机器人请求单岛 订单:{} ,产线：{} , 到单岛 URL：{},请求参数:{},错误信息：{}", orderNo, lineNo, url, JSONObject.toJSONString(dzdcMomSeqenceNo), throwable.getMessage(), throwable);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR80);
        }
    }

    private String getString(String momOrderNo) {
        if (StringUtils.isEmpty(momOrderNo)) {
            momOrderNo = "";
        }
        momOrderNo = momOrderNo.trim();
        return momOrderNo;
    }
}
