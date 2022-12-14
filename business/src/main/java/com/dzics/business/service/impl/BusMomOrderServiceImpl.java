package com.dzics.business.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.config.MapConfig;
import com.dzics.business.model.vo.udp.SendPlcModel;
import com.dzics.business.service.BusMomOrderService;
import com.dzics.business.service.agv.AgvService;
import com.dzics.business.service.mq.RabbitmqService;
import com.dzics.business.util.RedisUtil;
import com.dzics.business.util.SnowflakeUtil;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.search.MomResultSearch;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.mom.AddMomOrder;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.*;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class BusMomOrderServiceImpl implements BusMomOrderService {
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private DzOrderService dzOrderService;
    @Autowired
    private MomMaterialPointService pointService;
    @Autowired
    private MomWaitCallMaterialService waitCallMaterialService;

    @Autowired
    private MapConfig mapConfig;

    @Value("${dzdc.udp.client.qr.port}")
    private Integer plcPort;
    @Autowired
    private RabbitmqService rabbitmqService;
    @Value(("${accq.read.cmd.queue.equipment.realTime}"))
    private String logQuery;
    @Autowired
    private DzProductService productService;

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AgvService agvService;

    @Autowired
    private DzWorkReportSortService sortService;

    @Override
    public Result addOrder(AddMomOrder momOrder, String sub) {
        Object isAdd = redisUtil.get(RedisKey.MomOrderController_busMomOrderService_addOrder + momOrder.getLineId());
        if (isAdd != null) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }
        redisUtil.set(RedisKey.MomOrderController_busMomOrderService_addOrder + momOrder.getLineId(), momOrder.getLineId(), 2);
        momOrder.setWipOrderNo(String.valueOf(snowflakeUtil.nextId()));
        Result result = momOrderService.addOrder(momOrder, sub);
        return result;
    }

    /**
     * ??????????????????
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result put(String sub, PutMomOrder putMomOrder) {
        String progressStatus = putMomOrder.getProgressStatus();
        if (StringUtils.isEmpty(progressStatus)) {
            log.error("????????????????????????");
        }
        //?????????????????????????????????
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("line_id", putMomOrder.getLineId());
        wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING)
                .or().eq("ProgressStatus", MomProgressStatus.STOP)
                .or().eq("order_operation_result", 1));
        List<MonOrder> list = momOrderService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR51);
        }
        String proTaskOrderId = putMomOrder.getProTaskOrderId();
        MonOrder order = momOrderService.getById(proTaskOrderId);
        if("DZICS-Manual".equals(order.getWiporderno())){
            throw new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR971);
        }
        //????????????????????????????????????
        MonOrder monOrder = new MonOrder();
        monOrder.setProTaskOrderId(proTaskOrderId);
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);//???????????????
        boolean b = momOrderService.updateById(monOrder);
        if (b) {
            log.info("????????????:{}", putMomOrder);
            sendControCmdRob(proTaskOrderId, DzUdpType.CONTROL_STAR);
        }
        return Result.OK(b);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    public Result MomorderBegin(PutMomOrder putMomOrder) {
        if(!"Dzics-MomUser".equals(putMomOrder.getTransPondKey())){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR11.getChinese());
        }
        //????????????????????????????????????
        MonOrder monOrder = new MonOrder();
        monOrder.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);//???????????????
        boolean b = momOrderService.updateById(monOrder);
        if (b) {
            log.info("????????????:{}", putMomOrder);
            sendControCmdRob(putMomOrder.getProTaskOrderId(), DzUdpType.CONTROL_STAR);
        }
        return Result.OK(b);
    }

    public void sendControCmdRob(String proTaskOrderId, String typeContro) {
        try {
            //            ????????????????????????
//            MOM ??????
            MonOrder byId = momOrderService.getById(proTaskOrderId);
            DzProduct dzProduct = productService.getById(byId.getProductId());
            if (dzProduct == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR48);
            }
//            ?????????
            String wiporderno = byId.getWiporderno();
//            ????????????????????????????????????????????? 2021-12-14??????
            String productAliasProductionLine = byId.getProductAliasProductionLine();
            DzOrder order = dzOrderService.getById(byId.getOrderId());
            DzProductionLine line = lineService.getById(byId.getLineId());
            if (order == null || line == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR48);
            }
            Long orderId = line.getOrderId();
            Long id = line.getId();
            String workStation = pointService.getNextPoint(orderId, id);
            String nextOprSeqNo = "0080";
            String orderNo = order.getOrderNo();
            String lineNo = line.getLineNo();
            try {
                String sequenceNo = waitCallMaterialService.getOprSequenceNo(workStation, byId.getProTaskOrderId());
                SearchDzdcMomSeqenceNo seqenceNo = new SearchDzdcMomSeqenceNo();
                seqenceNo.setWipOrderNo(wiporderno);
                seqenceNo.setOprSequenceNo(sequenceNo);
                seqenceNo.setOrderCode(orderNo);
                seqenceNo.setLineNo(lineNo);
                MomResultSearch sanyMomNextSpecNo = agvService.getSanyMomNextSpecNo(seqenceNo);
                nextOprSeqNo = sanyMomNextSpecNo.getReturnData().getNextOprSeqNo();
            } catch (Throwable throwable) {
                log.error("??????MOM ???????????????????????????????????????: {}", nextOprSeqNo);
            }
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replace("-", ""));
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(FinalCode.Device_Code);
            timeLogs.setMessageType(1);
            if (DzUdpType.CONTROL_STAR.equals(typeContro)) {
                timeLogs.setMessage("???????????????" + wiporderno + " ????????????");
            }
            if (DzUdpType.CONTROL_STAR_STOP.equals(typeContro)) {
                timeLogs.setMessage("???????????????" + wiporderno + " ??????");
            }
            if (DzUdpType.CONTROL_STOP.equals(typeContro)) {
                timeLogs.setMessage("???????????????" + wiporderno + " ????????????");
            }
            if (DzUdpType.CONTROL_STAR_STOP_START.equals(typeContro)) {
                timeLogs.setMessage("???????????????" + wiporderno + " ????????????");
            }
            timeLogs.setTimestampTime(new Date());
            //     ?????????????????????
            boolean rab = rabbitmqService.sendRabbitmqLog(JSONObject.toJSONString(timeLogs));
            //      ?????????????????? mq ????????? UDP ??????
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                SendPlcModel sendPlcModel = new SendPlcModel();
                sendPlcModel.setIp(plcIp);
                sendPlcModel.setPort(plcPort);
                String msx = "";
//                if(){
//                    msx = "Q," + DzUdpType.UDP_CMD_CONTROL + "," + DzUdpType.UDP_CMD_CONTROL_INNER + "," + typeContro + "," + wiporderno + "," + orderNo + "," + lineNo
//                            + "," + productAliasProductionLine + "," + nextOprSeqNo + "," + byId.getProductNo() + "," + byId.getQuantity()
//                            + "," + dzProduct.getProductNo();
//                }else{
//                    msx = "Q," + DzUdpType.UDP_CMD_CONTROL + "," + DzUdpType.UDP_CMD_CONTROL_INNER + "," + typeContro + "," + wiporderno + "," + orderNo + "," + lineNo
//                            + "," + productAliasProductionLine + "," + nextOprSeqNo + "," + byId.getProductNo() + "," + byId.getQuantity()
//                            + "," + dzProduct.getProductNo();
//                }
                msx = "Q," + DzUdpType.UDP_CMD_CONTROL + "," + DzUdpType.UDP_CMD_CONTROL_INNER + "," + typeContro + "," + wiporderno + "," + orderNo + "," + lineNo
                        + "," + productAliasProductionLine + "," + nextOprSeqNo + "," + byId.getProductNo() + "," + byId.getQuantity()
                        + "," + dzProduct.getProductNo();
                sendPlcModel.setMessage(msx);
                log.info("????????????????????????:{}", msx);
                rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
                rabbitmqService.sendMsgOrder(JSONObject.toJSONString(byId));
            } else {
                log.error("???????????????????????????UDP??????: {} ,??????: {} ?????????IP?????????mapIps: {} ", lineNo, orderNo, mapIps);
            }
        } catch (Throwable throwable) {
            log.error("????????????????????????:{} ", throwable.getMessage(), throwable);
            throw throwable;
        }


    }

    /**
     * ????????????
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result forceClose(String sub, PutMomOrder putMomOrder) {
        //???????????????????????????????????????
        String proTaskOrderId = putMomOrder.getProTaskOrderId();
        MonOrder monOrder1 = momOrderService.getOne(new QueryWrapper<MonOrder>().eq("pro_task_order_id", proTaskOrderId));
        if (monOrder1 == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder1.getOrderOperationResult().intValue() == 1) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //????????????????????????
        MonOrder monOrder = new MonOrder();
        monOrder.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);
        boolean b = momOrderService.updateById(monOrder);
        sendControCmdRob(putMomOrder.getProTaskOrderId(), DzUdpType.CONTROL_STOP);
        return Result.OK(b);
    }

    /**
     * ????????????
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result orderStop(String sub, PutMomOrder putMomOrder) {
        MonOrder monOrder = momOrderService.getById(putMomOrder.getProTaskOrderId());
        if (monOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder.getOrderOperationResult().intValue() == 1) {
            log.warn("???????????????????????????????????????????????????:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //????????????
        MonOrder monOrder1 = new MonOrder();
        monOrder1.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder1.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder1.setOrderOperationResult(1);//???????????????
        boolean b = momOrderService.updateById(monOrder1);
        if (b) {
            log.info("????????????:{}", putMomOrder);
            sendControCmdRob(putMomOrder.getProTaskOrderId(), DzUdpType.CONTROL_STAR_STOP);
        }
        return Result.OK(b);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    public Result MomorderClose(PutMomOrder putMomOrder) {
        if(!"Dzics-MomUser".equals(putMomOrder.getTransPondKey())){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR11);
        }
        //????????????????????????
        MonOrder monOrder = new MonOrder();
        monOrder.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);//???????????????
        boolean b = momOrderService.updateById(monOrder);
        sendControCmdRob(putMomOrder.getProTaskOrderId(), DzUdpType.CONTROL_STOP);
        return Result.OK(b);
    }

    /**
     * ????????????
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result orderRecover(String sub, PutMomOrder putMomOrder) {
        MonOrder monOrder = momOrderService.getById(putMomOrder.getProTaskOrderId());
        if (monOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder.getOrderOperationResult().intValue() == 1) {
            log.warn("???????????????????????????????????????????????????:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //?????????????????????????????????
        if (!monOrder.getProgressStatus().equals(MomProgressStatus.STOP)) {
            log.warn("????????????????????????:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_7);
        }
        //????????????
        MonOrder monOrder1 = new MonOrder();
        monOrder1.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder1.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder1.setOrderOperationResult(1);//???????????????
        boolean b = momOrderService.updateById(monOrder1);
        if (b) {
            log.info("????????????:{}", putMomOrder);
            sendControCmdRob(putMomOrder.getProTaskOrderId(), DzUdpType.CONTROL_STAR_STOP_START);
        }
        return Result.OK(b);
    }

    /**
     * ????????????
     *
     * @param sub
     * @param proTaskOrderId
     * @return
     */
    @Override
    public Result orderDelete(String sub, String proTaskOrderId) {
        MonOrder byId = momOrderService.getById(proTaskOrderId);
        if (byId != null) {
            if (byId.getProgressStatus().equals(MomProgressStatus.DOWN) && byId.getOrderOperationResult().intValue() == 2) {
                //????????????
                byId.setOrderOperationResult(2);
                byId.setProgressStatus(MomProgressStatus.DELETE);
                boolean b = momOrderService.updateById(byId);
                return Result.ok();
            } else {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
            }
        } else {
            log.error("mom???????????????,??????id:{}", proTaskOrderId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }

    }

    @Override
    public Result orderCancelWorkReporting(String sub, String proTaskOrderId) {
        DzWorkReportSort one = sortService.getOne(new QueryWrapper<DzWorkReportSort>().eq("pro_task_order_id", proTaskOrderId));
        if (one == null) {
            log.warn("??????????????????????????????,??????id:{}", proTaskOrderId);
            return new Result(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR892.getChinese());
        }
        boolean b = sortService.removeById(one.getId());
        if (b) {
            return Result.ok();
        }
        return new Result(CustomExceptionType.SYSTEM_ERROR);
    }
}
