package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.AgvPalletType;
import com.dzics.common.model.constant.PointType;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.request.agv.AgvClickSignal;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.*;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.exception.CustomMomException;
import com.dzics.sanymom.framework.OperLogReportWork;
import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.common.AgvRollBackStatus;
import com.dzics.sanymom.model.common.MyReqMomType;
import com.dzics.sanymom.model.request.agv.AgvTask;
import com.dzics.sanymom.model.request.agv.AutomaticGuidedVehicle;
import com.dzics.sanymom.model.response.searchframe.MaterialFrameRes;
import com.dzics.sanymom.service.AgvRobackService;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.MomHttpRequestService;
import com.dzics.sanymom.service.impl.mq.AgvFeedbackDataLocalImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AgvRobackServiceImpl implements AgvRobackService {
    @Autowired
    private CallAgvBoxServiceImpl callAgvService;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MomWaitCallMaterialReqService waitCallMaterialReqService;
    @Autowired
    private MomHttpRequestService httpRequestService;
    @Autowired
    private MomDistributionWaitRequestService distributionWaitRequestService;

    @Value("${business.robot.ip}")
    private String busIpPort;
    @Value("${business.robot.material.click.path}")
    private String materialClick;

    @Autowired
    private AgvFeedbackDataLocalImpl agvFeedbackDataLocal;
    @Autowired
    private CachingApi cachingApi;

    /**
     * 处理agv 反馈 交换机
     */
    @Value("${call.direct.agv.exchange}")
    private String exchange;
    /**
     * 延时消费队列
     */
    @Value("${call.direct.agv.queue.delayed}")
    private String queryName;
    @Value("${call.direct.agv.routing.repeatTradeRouting}")
    private String routing;

    @OperLogReportWork(operModul = "AGV反馈",operDesc = "AGV反馈")
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public synchronized ResultDto automaticGuidedVehicle(AutomaticGuidedVehicle vehicle) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(vehicle);
            log.info("接收到 AGV搬运反馈信息确认到中控 json：{}", json);
            if (vehicle == null) {
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR43.getChinese(), MomVersion.VERSION, redisUniqueID.getUUID());
            }
            AgvTask task = vehicle.getTask();
            if (task == null) {
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR12.getChinese(), vehicle.getVersion(), vehicle.getTaskId());
            }
            String reqId = task.getReqId();
            String order_status = task.getOrder_Status();
            String myReqMomType = httpRequestService.getMyReqTypeId(reqId);
            if (MyReqMomType.CALL_MATERIAL.equals(myReqMomType)) {
                log.info("叫料请求反馈信息:{}", json);
//               叫料请求反馈
                MomWaitCallMaterialReq materialReq = new MomWaitCallMaterialReq();
                materialReq.setOrderStatus(order_status);
                QueryWrapper<MomWaitCallMaterialReq> wp = new QueryWrapper<>();
                wp.eq("reqId", reqId);
                boolean update = waitCallMaterialReqService.update(materialReq, wp);
                log.info("更新叫料请求订单状态：{},materialReq:{}", update, materialReq);
                if (AgvRollBackStatus.End_Point_OK.equals(order_status)) {
                    MomWaitCallMaterialReq one = waitCallMaterialReqService.getOne(wp);
                    if (one==null){
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR93);
                    }
                    String sourceno = one.getSourceno();
                    if (!StringUtils.isEmpty(sourceno)) {
                        updatePointStatus(sourceno, "生产叫料完成");
                    }
                    try {
                        if(StringUtil.isEmpty(task.getDestNo())){
                            throw new CustomException(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR12.getChinese());
                        }
                        String basketType = one.getBasketType();
                        DzProductionLine line = cachingApi.getOrderIdAndLineId();
                        String lineNo = line.getLineNo();
                        String orderNo = line.getOrderNo();
                        log.info("发送给机器人叫料信号到位 开始,订单:{} 产线：{} :小车：{}", basketType,orderNo, lineNo);
                        String groupId = redisUniqueID.getGroupId();
                        String innerGroupId = redisUniqueID.getGroupId();
                        MaterialFrameRes frameRes = httpRequestService.getStringPalletType(innerGroupId, groupId, orderNo, lineNo, sourceno, "");
                        boolean b = chlickSignal(basketType, orderNo, lineNo, frameRes.getPalletNo());
                        log.info("发送给机器人叫料信号到位 完成,订单:{} 产线：{} :小车：{}", basketType, orderNo, lineNo);
                    } catch (Throwable throwable) {
                        log.error("发送给机器人叫料信号到位失败：{}", throwable.getMessage(), throwable);
                    }
                }
                ResultDto resultDto = new ResultDto();
                resultDto.setCode("0");
                resultDto.setMsg("OK");
                resultDto.setVersion(vehicle.getVersion());
                resultDto.setTaskId(vehicle.getTaskId());
                return resultDto;
            } else {
                MomDistributionWaitRequest waitRequest = new MomDistributionWaitRequest();
                waitRequest.setOrderStatus(order_status);
                QueryWrapper<MomDistributionWaitRequest> wp = new QueryWrapper<>();
                wp.eq("reqId", reqId);
                boolean update = distributionWaitRequestService.update(waitRequest, wp);
                log.info("工序间配送反馈信息 跟新请求状态完成 update:{},waitRequest:{}", update, waitRequest);
                if (AgvRollBackStatus.Starting_Point_OK.equals(order_status)) {
                    String ishand = redisUtil.get(reqId + ":" + order_status);
                    if (ishand != null) {
                        log.error("AGV反馈信号重复 内容:{}", JSONObject.toJSONString(vehicle));
                    } else {
                        boolean ok = redisUtil.set(reqId + ":" + order_status, "OK", 24 * 60 * 60);
                        if (!ok) {
                            log.error("屏蔽信号失败: 需要屏蔽内容: {}", JSONObject.toJSONString(vehicle));
                        }
                        boolean b1 = agvFeedbackDataLocal.sendMq(vehicle, routing, exchange, queryName);
                        if (b1) {
                            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0.getChinese(), vehicle.getVersion(), vehicle.getTaskId());
                        }
                    }


                }
                ResultDto resultDto = new ResultDto();
                resultDto.setCode("0");
                resultDto.setMsg("OK");
                resultDto.setVersion(vehicle.getVersion());
                resultDto.setTaskId(vehicle.getTaskId());
                return resultDto;
            }
        } catch (Throwable throwable) {
            Gson gson = new Gson();
            String json = gson.toJson(vehicle);
            log.error("接收到 AGV搬运反馈信息:{} 确认到中控 执行异常：{}", json, throwable.getMessage(), throwable);
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0.getChinese(), vehicle.getVersion(), vehicle.getTaskId());
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handelAgvMessage(String reqId) {
        try {
            QueryWrapper<MomDistributionWaitRequest> wpRes = new QueryWrapper<>();
            wpRes.eq("reqId", reqId);
            MomDistributionWaitRequest request = distributionWaitRequestService.getOne(wpRes);
            if (request == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR94);
            }
            String isUpMach = request.getIsUpMach();
            String basketType = request.getBasketType();
            String orderNo = request.getOrderNo();
            String lineNo = request.getLineNo();
            String taskType = request.getTaskType();
            String sourceno = request.getSourceno();
            if (MomTaskType.CALL_MATERIAL.equals(taskType)) {
                if (MomReqContent.CALL_AGV_REQ_TYPE_3.equals(request.getReqtype())) {
                    log.info("发送给机器人 空料框 到位 开始 ,订单:{} 产线：{} :小车：{}", orderNo, lineNo, basketType);
                    updatePointStatus(sourceno, "空料框到位");
                    String groupId = redisUniqueID.getGroupId();
                    String innerGroupId = redisUniqueID.getGroupId();
                    MaterialFrameRes frameRes = httpRequestService.getStringPalletType(innerGroupId, groupId, orderNo, lineNo, sourceno, request.getProductNo());
                    String palletNo = frameRes.getPalletNo();
                    request.setPalletno(palletNo);
                    boolean b = distributionWaitRequestService.updateById(request);
                    log.info("跟新请求空框料框编码:请求ID: {},料框编码: {}", request.getWorkpieceDistributionId(), palletNo);
                    chlickSignal(basketType, orderNo, lineNo, palletNo);
                    log.info("发送给机器人 空料框 到位 完成 ,订单:{} 产线：{} :小车：{}", orderNo, lineNo, basketType);
                }
                if (MomReqContent.CALL_AGV_REQ_TYPE_4.equals(request.getReqtype())) {
                    if (PointType.SLXL.equals(isUpMach)) {
                        updatePointStatus(sourceno, "移出满料框完成");
//                                    自动呼叫物料
                        EmptyFrameMovesDzdc frameMoves = new EmptyFrameMovesDzdc();
                        frameMoves.setBasketType(basketType);
                        frameMoves.setPalletType(AgvPalletType.DNU);
                        frameMoves.setOrderCode(orderNo);
                        frameMoves.setLineNo(lineNo);
                        frameMoves.setDeviceType("DZDC");
                        frameMoves.setGroupId(redisUniqueID.getGroupId());
                        frameMoves.setInnerGroupId(redisUniqueID.getGroupId());
                        Result result = callAgvService.callAgv(frameMoves);
                        log.info("自动呼叫物料结束:{}", JSONObject.toJSONString(result));
                    } else  {
                        try {
//                          满料框拉走完成  直接呼叫空料框
                            updatePointStatus(sourceno, "移出满料框完成");
                            EmptyFrameMovesDzdc movesDzdc = new EmptyFrameMovesDzdc();
                            movesDzdc.setBasketType(basketType);
                            movesDzdc.setPalletType(AgvPalletType.GNU);
                            movesDzdc.setOrderCode(orderNo);
                            movesDzdc.setLineNo(request.getLineNo());
                            movesDzdc.setQuantity(0);
                            movesDzdc.setDeviceType("DZDC");
                            movesDzdc.setGroupId(redisUniqueID.getGroupId());
                            movesDzdc.setInnerGroupId(redisUniqueID.getGroupId());
                            movesDzdc.setSyProductNo(request.getProductNo());
                            Result result = callAgvService.callAgv(movesDzdc);
                            log.info("呼叫空料框完成:{}", result);
                        } catch (Throwable throwable) {
                            log.error("呼叫空料框错误：:{}", throwable.getMessage(), throwable);
                        }
                    }
                }
                if (MomReqContent.CALL_AGV_REQ_TYPE_2.equals(request.getReqtype())) {
//                            空料框回缓存区
                    if (PointType.UP.equals(isUpMach)) {
                        updatePointStatus(sourceno, "移出满料框完成");
                        EmptyFrameMovesDzdc frameMoves = new EmptyFrameMovesDzdc();
                        frameMoves.setBasketType(basketType);
                        frameMoves.setPalletType(AgvPalletType.DNU);
                        frameMoves.setOrderCode(orderNo);
                        frameMoves.setLineNo(lineNo);
                        frameMoves.setDeviceType("DZDC");
                        frameMoves.setGroupId(redisUniqueID.getGroupId());
                        frameMoves.setInnerGroupId(redisUniqueID.getGroupId());
                        Result result = callAgvService.callAgv(frameMoves);
                        log.info("自动呼叫物料结束:{}", JSONObject.toJSONString(result));
                    }
                }
            } else {
                log.error("接口请求类型为识别：{}", taskType);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR941);
            }

        } catch (Throwable throwable) {
            log.error("AGV 反馈发送到机器人 reqId:{},错误：{}", reqId, throwable.getMessage(), throwable);
        }
    }

    private void updatePointStatus(String sourceno, String pointStatus) {
        try {
            if (!StringUtils.isEmpty(sourceno)) {
                redisUtil.set(RedisKey.MATERIAL_POINT_STATUS + sourceno, pointStatus);
            }
        } catch (Throwable throwable) {
            log.error("更新料点状态错误:{}", throwable.getMessage(), throwable);
        }
    }


    private boolean chlickSignal(String basketType, String orderNo, String lineNo, String palletNo) {
        String url = busIpPort + materialClick;
        AgvClickSignal clickSignal = new AgvClickSignal();
        clickSignal.setBasketType(basketType);
        clickSignal.setOrderNo(orderNo);
        clickSignal.setLineNo(lineNo);
        clickSignal.setPalletNo(palletNo);
        ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, clickSignal, Result.class);
        log.info("触发AGV到位信号 url: {} ，参数:{} ，返回结果：{}", url, JSONObject.toJSONString(clickSignal), JSONObject.toJSONString(resultResponseEntity));
        return true;
    }

}
