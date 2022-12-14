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
     * ??????agv ?????? ?????????
     */
    @Value("${call.direct.agv.exchange}")
    private String exchange;
    /**
     * ??????????????????
     */
    @Value("${call.direct.agv.queue.delayed}")
    private String queryName;
    @Value("${call.direct.agv.routing.repeatTradeRouting}")
    private String routing;

    @OperLogReportWork(operModul = "AGV??????",operDesc = "AGV??????")
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public synchronized ResultDto automaticGuidedVehicle(AutomaticGuidedVehicle vehicle) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(vehicle);
            log.info("????????? AGV????????????????????????????????? json???{}", json);
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
                log.info("????????????????????????:{}", json);
//               ??????????????????
                MomWaitCallMaterialReq materialReq = new MomWaitCallMaterialReq();
                materialReq.setOrderStatus(order_status);
                QueryWrapper<MomWaitCallMaterialReq> wp = new QueryWrapper<>();
                wp.eq("reqId", reqId);
                boolean update = waitCallMaterialReqService.update(materialReq, wp);
                log.info("?????????????????????????????????{},materialReq:{}", update, materialReq);
                if (AgvRollBackStatus.End_Point_OK.equals(order_status)) {
                    MomWaitCallMaterialReq one = waitCallMaterialReqService.getOne(wp);
                    if (one==null){
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR93);
                    }
                    String sourceno = one.getSourceno();
                    if (!StringUtils.isEmpty(sourceno)) {
                        updatePointStatus(sourceno, "??????????????????");
                    }
                    try {
                        if(StringUtil.isEmpty(task.getDestNo())){
                            throw new CustomException(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR12.getChinese());
                        }
                        String basketType = one.getBasketType();
                        DzProductionLine line = cachingApi.getOrderIdAndLineId();
                        String lineNo = line.getLineNo();
                        String orderNo = line.getOrderNo();
                        log.info("???????????????????????????????????? ??????,??????:{} ?????????{} :?????????{}", basketType,orderNo, lineNo);
                        String groupId = redisUniqueID.getGroupId();
                        String innerGroupId = redisUniqueID.getGroupId();
                        MaterialFrameRes frameRes = httpRequestService.getStringPalletType(innerGroupId, groupId, orderNo, lineNo, sourceno, "");
                        boolean b = chlickSignal(basketType, orderNo, lineNo, frameRes.getPalletNo());
                        log.info("???????????????????????????????????? ??????,??????:{} ?????????{} :?????????{}", basketType, orderNo, lineNo);
                    } catch (Throwable throwable) {
                        log.error("?????????????????????????????????????????????{}", throwable.getMessage(), throwable);
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
                log.info("??????????????????????????? ???????????????????????? update:{},waitRequest:{}", update, waitRequest);
                if (AgvRollBackStatus.Starting_Point_OK.equals(order_status)) {
                    String ishand = redisUtil.get(reqId + ":" + order_status);
                    if (ishand != null) {
                        log.error("AGV?????????????????? ??????:{}", JSONObject.toJSONString(vehicle));
                    } else {
                        boolean ok = redisUtil.set(reqId + ":" + order_status, "OK", 24 * 60 * 60);
                        if (!ok) {
                            log.error("??????????????????: ??????????????????: {}", JSONObject.toJSONString(vehicle));
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
            log.error("????????? AGV??????????????????:{} ??????????????? ???????????????{}", json, throwable.getMessage(), throwable);
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
                    log.info("?????????????????? ????????? ?????? ?????? ,??????:{} ?????????{} :?????????{}", orderNo, lineNo, basketType);
                    updatePointStatus(sourceno, "???????????????");
                    String groupId = redisUniqueID.getGroupId();
                    String innerGroupId = redisUniqueID.getGroupId();
                    MaterialFrameRes frameRes = httpRequestService.getStringPalletType(innerGroupId, groupId, orderNo, lineNo, sourceno, request.getProductNo());
                    String palletNo = frameRes.getPalletNo();
                    request.setPalletno(palletNo);
                    boolean b = distributionWaitRequestService.updateById(request);
                    log.info("??????????????????????????????:??????ID: {},????????????: {}", request.getWorkpieceDistributionId(), palletNo);
                    chlickSignal(basketType, orderNo, lineNo, palletNo);
                    log.info("?????????????????? ????????? ?????? ?????? ,??????:{} ?????????{} :?????????{}", orderNo, lineNo, basketType);
                }
                if (MomReqContent.CALL_AGV_REQ_TYPE_4.equals(request.getReqtype())) {
                    if (PointType.SLXL.equals(isUpMach)) {
                        updatePointStatus(sourceno, "?????????????????????");
//                                    ??????????????????
                        EmptyFrameMovesDzdc frameMoves = new EmptyFrameMovesDzdc();
                        frameMoves.setBasketType(basketType);
                        frameMoves.setPalletType(AgvPalletType.DNU);
                        frameMoves.setOrderCode(orderNo);
                        frameMoves.setLineNo(lineNo);
                        frameMoves.setDeviceType("DZDC");
                        frameMoves.setGroupId(redisUniqueID.getGroupId());
                        frameMoves.setInnerGroupId(redisUniqueID.getGroupId());
                        Result result = callAgvService.callAgv(frameMoves);
                        log.info("????????????????????????:{}", JSONObject.toJSONString(result));
                    } else  {
                        try {
//                          ?????????????????????  ?????????????????????
                            updatePointStatus(sourceno, "?????????????????????");
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
                            log.info("?????????????????????:{}", result);
                        } catch (Throwable throwable) {
                            log.error("????????????????????????:{}", throwable.getMessage(), throwable);
                        }
                    }
                }
                if (MomReqContent.CALL_AGV_REQ_TYPE_2.equals(request.getReqtype())) {
//                            ?????????????????????
                    if (PointType.UP.equals(isUpMach)) {
                        updatePointStatus(sourceno, "?????????????????????");
                        EmptyFrameMovesDzdc frameMoves = new EmptyFrameMovesDzdc();
                        frameMoves.setBasketType(basketType);
                        frameMoves.setPalletType(AgvPalletType.DNU);
                        frameMoves.setOrderCode(orderNo);
                        frameMoves.setLineNo(lineNo);
                        frameMoves.setDeviceType("DZDC");
                        frameMoves.setGroupId(redisUniqueID.getGroupId());
                        frameMoves.setInnerGroupId(redisUniqueID.getGroupId());
                        Result result = callAgvService.callAgv(frameMoves);
                        log.info("????????????????????????:{}", JSONObject.toJSONString(result));
                    }
                }
            } else {
                log.error("??????????????????????????????{}", taskType);
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR941);
            }

        } catch (Throwable throwable) {
            log.error("AGV ???????????????????????? reqId:{},?????????{}", reqId, throwable.getMessage(), throwable);
        }
    }

    private void updatePointStatus(String sourceno, String pointStatus) {
        try {
            if (!StringUtils.isEmpty(sourceno)) {
                redisUtil.set(RedisKey.MATERIAL_POINT_STATUS + sourceno, pointStatus);
            }
        } catch (Throwable throwable) {
            log.error("????????????????????????:{}", throwable.getMessage(), throwable);
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
        log.info("??????AGV???????????? url: {} ?????????:{} ??????????????????{}", url, JSONObject.toJSONString(clickSignal), JSONObject.toJSONString(resultResponseEntity));
        return true;
    }

}
