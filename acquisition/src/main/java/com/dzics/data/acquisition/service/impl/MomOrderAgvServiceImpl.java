package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzProductMapper;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.exception.BindQrCodeException;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.search.MomResultSearch;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.request.mom.CallMaterialStatus;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.*;
import com.dzics.data.acquisition.config.MapConfig;
import com.dzics.data.acquisition.model.SendPlcModel;
import com.dzics.data.acquisition.service.AccOrderQrCodeService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.DeviceStatusPush;
import com.dzics.data.acquisition.service.MomOrderAgvService;
import com.dzics.data.acquisition.service.mq.RabbitmqService;
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

/**
 * @author Administrator
 */
@Slf4j
@Service
public class MomOrderAgvServiceImpl implements MomOrderAgvService {
    @Autowired
    private DzProductMapper productMapper;
    @Autowired
    private MomMaterialPointService pointService;
    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private MomOrderMapper momOrderMapper;
    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String logsName;
    @Value("${dzdc.udp.client.qr.port}")
    private Integer plcPort;
    @Autowired
    private AccOrderQrCodeService accOrderQrCodeService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private CacheService cacheService;

    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private MomWaitCallMaterialService waitCallMaterialService;

    @Override
    public void udpCmdControl(String[] split) {
        try {
            String cmdTypeInner = split[2]; // 指令类型 1200,1201,1202,
            String value = split[3];  // 指令值
            String msgId = split[4];  // 消息ID
            String orderNo = split[5];// 订单号
            String lineNo = split[6];//   产线号
            Map<String, String> maps = mapConfig.getMaps();
            String ip = maps.get(orderNo + lineNo);

            if (DzUdpType.UDP_CMD_CONTROL_INNER.equals(cmdTypeInner)) {
                log.info("机器人回复收下发的指令:{}", (Object) split);
//              保存控制指令下发 回复日志，发送到队列触发到看板
                this.udpCmdControlInner(msgId, cmdTypeInner, value, orderNo, lineNo, null, null);
                return;
            }
            if (DzUdpType.UDP_CMD_CONTROL_UP.equals(cmdTypeInner)) {
                if(!DzUdpType.OK.equals(value) || StringUtils.isEmpty(value)){
                    log.error("MomOrderAgvServiceImpl [udpCmdControl] 机器人1201数据处理，控制类型：{}，指令值：{}",cmdTypeInner,value);
                    return;
                }

                log.info("机器人回复订单开始指令:{}", (Object) split);
//                控制指令回传状态
                OrderIdLineId orderIdLineId = cacheService.getOrderNoLineNoId(orderNo, lineNo);
                if (orderIdLineId == null) {
                    log.error("根据订单号：{},产线: {} ，获取订单产线不存在，无法更新订单状态", orderNo, lineNo);
                    return;
                }
                MonOrder monOrder = momOrderService.getOrderOperationResult(orderIdLineId.getOrderId(), orderIdLineId.getLineId(), MomProgressStatus.OperationResultLoading);
                if (monOrder == null) {
                    log.error("自动开始订单失败:根据订单: {} ,产线: {} ,执行中状态 order_operation_result: {} 获取订单不存在：monOrder：{}  ", orderNo, lineNo, MomProgressStatus.OperationResultLoading, monOrder);
                    return;
                }
                MonOrder monOrderUpdate = momOrderService.updateOrderStates(monOrder, value);
//                发送订单最新状态到前端页面
                deviceStatusPush.sendMomOrderRef(monOrderUpdate, 1);
//                发送控制成功日志
                this.udpCmdControlUp(monOrder.getWiporderno(), cmdTypeInner, value, orderNo, lineNo, monOrderUpdate.getProgressStatus(), null);
                return;
            }
//            Q,5,1202,1,订单号,产线号,Mom订单号,二维码
            if (DzUdpType.UDP_CMD_CONTROL_SUM.equals(cmdTypeInner)) {
                log.info("机器人上发二维码绑定订单指令:{}", (Object) split);
                try {
                    if (value.contains("_")) {
//                        String[] s = value.split("_");
//                        if (s.length == 2) {
//                            value = s[1];
//                        } else {
//                            log.error("处理二维码绑定订单错误 订单：{},产线:{} ,code: {}", orderNo, lineNo, value);
//                        }
                        int index = value.indexOf("_") + 1;
                        value = value.substring(index,value.length());
                    }
                } catch (Throwable throwable) {
                    log.error("处理二维码绑定订单错误：{}", throwable.getMessage(), throwable);
                }
//                绑定二维码,增加生成数量,获取当前生产报工中的订单
                MonOrder startOrder = momOrderService.getMomOrder(orderNo, lineNo, MomProgressStatus.LOADING);
                if (startOrder == null) {
                    log.warn("UDP绑定二维码,获取生产订单不存在-> 岛: {} ,产线: {},ProgressStatus:{},monOrder：{} ", orderNo, lineNo, MomProgressStatus.LOADING, startOrder);
                    throw new BindQrCodeException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "当前生产订单：" + orderNo + "不存在");
                } else {
                    MonOrder mOrder = cacheService.getMomOrderNoProducBarcode(value, orderNo, lineNo);
                    if (mOrder != null) {
                        log.warn("当前订单: {},产线编号: {} 下已存在该二维码: {}", orderNo, lineNo, value);
                        return;
                    }
//                  绑定二维码
                    Integer sum = accOrderQrCodeService.bandMomOrderQrCode(value, startOrder, orderNo, lineNo);
//                        更新订单抓取数量
                    updateOrderStateSum(cmdTypeInner, value, orderNo, lineNo, startOrder, sum);
                    return;
                }
            }
            log.error("设备控制指令 返回数据格式异常指令类型:{},{},{},无法识别当前值：{} ", DzUdpType.UDP_CMD_CONTROL_INNER, DzUdpType.UDP_CMD_CONTROL_UP, DzUdpType.UDP_CMD_CONTROL_SUM, cmdTypeInner);
        } catch (Throwable throwable) {
            log.error("处理UDP上发的指令数据:{},异常：{}", split, throwable.getMessage(), throwable);
        }
    }

    /**
     * 更新订单抓取数量 如果是抓取完成，则开始新的订单
     * @param cmdTypeInner
     * @param value
     * @param orderNo
     * @param lineNo
     * @param monOrderSel
     * @param sum
     */
    @Override
    public synchronized void updateOrderStateSum(String cmdTypeInner, String value, String orderNo, String lineNo, MonOrder monOrderSel, Integer sum) {
        if (sum != null) {
            MonOrder momOder = momOrderService.updaateQuantity(monOrderSel, monOrderSel.getWiporderno(), sum.toString());
            log.info("更新产线:{} 订单: {} 当前订单数量：{}", orderNo,momOder.getWiporderno(),momOder.getQuantity());
            if (momOder.getProgressStatus().equals(MomProgressStatus.SUCCESS)) {
//                发送日志到看板订单数量已完成
                this.udpCmdControlSum(monOrderSel.getWiporderno(), orderNo, lineNo, monOrderSel.getOrderOutput());
                deviceStatusPush.sendMomOrderRef(momOder, 1);
//              订单状态已完成，查看是否有可以开始 和 本次订单相同的物料订单，如果有则自动开始。
                this.startMomOrderV2(momOder, orderNo, lineNo);
            } else {
                log.info("产线：{} 订单：{} 更新订单数量：{}", orderNo,momOder.getWiporderno(),momOder.getQuantity());
            }
            deviceStatusPush.sendMomOrderRef(momOder, 2);
        }
    }

    @Override
    public void startMomOrderV2(MonOrder upOlder, String orderNo, String lineNo) {
        try {
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (StringUtils.isEmpty(plcIp)) {
                log.error("下发订单指令发送到UDP订单: {} ,产线: {} 配置的IP不存在mapIps: {} ", lineNo, orderNo, mapIps);
                return;
            }
            log.info("1.执行自动开始订单流程 开始：orderNo:{},lineNo:{}", orderNo, lineNo);
            log.info("2.执行自动开始订单流程:获取工控机IP地址");
            Long lineId = upOlder.getLineId(); // 产线ID
            Long orderId = upOlder.getOrderId();//订单ID
            //查询进行中的订单，或者订单暂停 或者 在操作进行中的订单
            log.info("3.执行自动开始订单流程:检查是否存在操作中的订单");
            List<MonOrder> list = momOrderMapper.getOrderLoding(lineId);
            if (CollectionUtils.isNotEmpty(list)) {
                log.error("产线中已存在执行中的订单,订单结束后可开始,自动校验订单自动开始执行取消  list: {}", list);
                return;
            }
//            获取可以自动开始的订单
            log.info("4.执行自动开始订单流程：获取可以自动开始的订单");
            MonOrder startOrder = momOrderService.getOrderCallMaterialStatus(orderId, lineId, upOlder.getProductNo(), MomProgressStatus.DOWN);
            if (startOrder == null) {
                log.error("没有可以自动开始执行的订单 orderNo :{},lineNo:{},MomProgressStatus:{},CallMaterialStatus:{}", orderNo, lineNo, MomProgressStatus.DOWN, CallMaterialStatus.STARTED);
                return;
            }
            String wiporderno = startOrder.getWiporderno();
            log.info("5.执行自动开始订单流程：获取订单的物料查询本地物料信息 wipOrderNo：{}", wiporderno);
            DzProduct dzProduct = productMapper.selectById(startOrder.getProductId());
            if (dzProduct == null) {
                log.error("订单数据错误,根据订单中产品ID,无法获取到产品 dzProduct：{},请重新新建订单后重试：{}", dzProduct, JSONObject.toJSONString(startOrder));
                return;
            }
            log.info("6.执行自动开始订单流程：请求MOM查询下个工序号 wipOrderNo：{}", wiporderno);
            String proTaskOrderId = startOrder.getProTaskOrderId();
            String nextOprSeqNo = this.getNextOprSeqNo(orderNo, lineNo, lineId, orderId, startOrder, proTaskOrderId);
            log.info("7.执行自动开始订单流程:封装开始订单指令 wipOrderNo：{}", wiporderno);
            //      将信号发送到 mq 触发到 UDP 发送
            String syProduct = startOrder.getProductNo();
            Integer quantity = startOrder.getQuantity();
            String alias = startOrder.getProductAliasProductionLine();
            String productNo = dzProduct.getProductNo();
            SendPlcModel sendPlcModel = new SendPlcModel();
            sendPlcModel.setIp(plcIp);
            sendPlcModel.setPort(plcPort);
            String msx = "Q," + DzUdpType.UDP_CMD_CONTROL + "," + DzUdpType.UDP_CMD_CONTROL_INNER + "," + DzUdpType.CONTROL_STAR + "," + wiporderno + "," + orderNo + "," + lineNo + "," + alias + "," + nextOprSeqNo + "," + syProduct + "," + quantity + "," + productNo;
            sendPlcModel.setMessage(msx);
            String jsonString = JSONObject.toJSONString(sendPlcModel);
            //更改指定订单状态为进行中
            startOrder.setProgressStatus(MomProgressStatus.LOADING);
            startOrder.setReportStatus(false);
            startOrder.setOrderOperationResult(1);//操作进行中
            boolean updateById = momOrderService.updateById(startOrder);
            log.info("8.执行自动开始订单流程:下发订单指令信息发送队列：{}", jsonString);
            rabbitmqService.sendQrCodeMqUdp(jsonString);
            log.info("9.执行自动开始订单流程:延时检测订单执行状态信息发送队列：{}", jsonString);
            rabbitmqService.sendMsgOrder(JSONObject.toJSONString(startOrder));
            log.info("10.执行自动开始订单流程: 下发指令信息 订单执行检测信息 完成 wipOrderNo：{}", wiporderno);
            log.info("11.执行自动开始订单流程: 更新本地订单状态信息 wipOrderNo：{}", wiporderno);
            log.info("12.执行自动开始订单流程：生产发送开始订单指令日志 wipOrderNo：{}", wiporderno);
            this.extracted(orderNo, lineNo, wiporderno);
            log.info("13.执行自动开始订单流程：日志发送到队列完成 wipOrderNo：{}", wiporderno);
            deviceStatusPush.sendMomOrderRef(startOrder, 1);
            log.info("13.执行自动开始订单流程：推送订单状态信息完成 wipOrderNo：{}", wiporderno);
            log.info("14.执行自动开始订单结束：orderNo:{},lineNo:{},wipOrderNo：{}", orderNo, lineNo, wiporderno);
        } catch (Throwable throwable) {
            log.error("执行自动开始订单异常:{}", throwable.getMessage(), throwable);
        }
    }

    private void extracted(String orderNo, String lineNo, String wiporderno) {
        SysRealTimeLogs timeLogs = new SysRealTimeLogs();
        timeLogs.setMessageId(wiporderno);
        timeLogs.setQueueName("dzics-dev-gather-v1-realTime-logs");
        timeLogs.setClientId(LogClientType.BUS_AGV);
        timeLogs.setOrderCode(orderNo);
        timeLogs.setLineNo(lineNo);
        timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
        timeLogs.setDeviceCode(FinalCode.Device_Code);
        timeLogs.setMessageType(1);
        timeLogs.setMessage("自动执行订单：" + wiporderno + " 开始指令已发送完成");
        timeLogs.setTimestampTime(new Date());
        //     发送到日志队列
        boolean rab = rabbitmqService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
    }

    private String getNextOprSeqNo(String orderNo, String lineNo, Long lineId, Long orderId, MonOrder startOrder, String proTaskOrderId) {
        String workStation = pointService.getNextPoint(orderId, lineId);
        try {
            String sequenceNo = waitCallMaterialService.getOprSequenceNo(workStation, proTaskOrderId);
            SearchDzdcMomSeqenceNo seqenceNo = new SearchDzdcMomSeqenceNo();
            seqenceNo.setWipOrderNo(startOrder.getWiporderno());
            seqenceNo.setOprSequenceNo(sequenceNo);
            seqenceNo.setOrderCode(orderNo);
            seqenceNo.setLineNo(lineNo);
            MomResultSearch sanyMomNextSpecNo = getSanyMomNextSpecNo(seqenceNo);
            return sanyMomNextSpecNo.getReturnData().getNextOprSeqNo();
        } catch (Throwable throwable) {
            log.error("请求MOM 查询下个工序号失败: {}", throwable.getMessage(), throwable);
        }
        return "0080";
    }


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


    public void udpCmdControlSum(String momOrderNo, String orderNo, String lineNo, Integer outPut) {
//      机器人回复收到下发的控制指令
        try {
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
            timeLogs.setQueueName(logsName);
            timeLogs.setClientId(LogClientType.ROB_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(FinalCode.Device_Code);
            timeLogs.setMessageType(1);
            String valueMsg = "";
            valueMsg = "订单:" + momOrderNo + "生产数量:" + outPut + " 订单已完成";
            timeLogs.setMessage(valueMsg);
            timeLogs.setTimestampTime(new Date());
            deviceStatusPush.sendSysRealTimeLogs(timeLogs);
        } catch (Throwable e) {
            log.error("发送订单日志到看板错误:{}", e.getMessage(), e);
        }
    }

    public void udpCmdControlUp(String momOrderNo, String cmdInner, String value, String orderNo, String lineNo, String progressStatus, Integer outPut) {
//      机器人回复收到下发的控制指令
        SysRealTimeLogs timeLogs = new SysRealTimeLogs();
        timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
        timeLogs.setQueueName(logsName);
        timeLogs.setClientId(LogClientType.ROB_AGV);
        timeLogs.setOrderCode(orderNo);
        timeLogs.setLineNo(lineNo);
        timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
        timeLogs.setDeviceCode(FinalCode.Device_Code);
        timeLogs.setMessageType(1);
        String valueMsg = "";
        if (MomProgressStatus.DOWN.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：未开始";
        }
        if (MomProgressStatus.LOADING.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：执行中";
        }
        if (MomProgressStatus.SUCCESS.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：完工";
        }
        if (MomProgressStatus.DELETE.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：删除";
        }
        if (MomProgressStatus.CLOSE.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：强制关闭";
        }
        if (MomProgressStatus.STOP.equals(progressStatus)) {
            valueMsg = "机器人回复订单: " + momOrderNo + "执行结果：暂停";
        }
        timeLogs.setMessage(valueMsg);
        timeLogs.setTimestampTime(new Date());
        deviceStatusPush.sendSysRealTimeLogs(timeLogs);
    }

    public void udpCmdControlInner(String momOrderNo, String cmdInner, String value, String orderNo, String lineNo, String progressStatus, Integer outPut) {
//      机器人回复收到下发的控制指令
        SysRealTimeLogs timeLogs = new SysRealTimeLogs();
        timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
        timeLogs.setQueueName(logsName);
        timeLogs.setClientId(LogClientType.ROB_AGV);
        timeLogs.setOrderCode(orderNo);
        timeLogs.setLineNo(lineNo);
        timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
        timeLogs.setDeviceCode(FinalCode.Device_Code);
        timeLogs.setMessageType(1);
        String valueMsg = "";
        if (DzUdpType.CONTROL_STOP.equals(value)) {
//           终止
            valueMsg = "终止";
        }
        if (DzUdpType.CONTROL_STAR.equals(value)) {
//           开始
            valueMsg = "开始";
        }
        if (DzUdpType.CONTROL_STAR_STOP.equals(value)) {
//            暂停
            valueMsg = "暂停";
        }
        if (DzUdpType.CONTROL_STAR_STOP_START.equals(value)) {
//            继续执行
            valueMsg = "继续执行";
        }
        timeLogs.setMessage("机器人回复收到:" + valueMsg + "信号");
        timeLogs.setTimestampTime(new Date());
        deviceStatusPush.sendSysRealTimeLogs(timeLogs);
    }
}
