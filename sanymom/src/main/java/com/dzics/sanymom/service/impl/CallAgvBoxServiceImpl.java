package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.agv.MomUpPoint;
import com.dzics.common.model.constant.AgvPalletType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.CallAgvService;
import com.dzics.sanymom.service.TaskMomMaterialPointService;
import com.dzics.sanymom.service.impl.agv.*;
import com.dzics.sanymom.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CallAgvBoxServiceImpl implements CallAgvService {
    @Autowired
    private CallEmptyBoxImpl callEmptyBox;
    @Autowired
    private ExportFullBoxImpl exportFullBox;
    @Autowired
    private RedisUtil<MomUpPoint> redisUtil;
    @Autowired
    private TaskMomMaterialPointService pointService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private CallMaterialBoxImpl callBoxMaterial;
    @Autowired
    private RemoveEmptyBoxImpl removeEmptyBox;

    @Override
    public Result<String> callAgv(EmptyFrameMovesDzdc movesDzdc) {
        String runModel = cachingApi.getMomRunModel();
        if (StringUtils.isEmpty(runModel)) {
            throw new CustomException(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR95);
        }
        if (!"auto".equals(runModel.trim())) {
            log.warn("?????????{}, ???????????????????????????", JSONObject.toJSONString(movesDzdc));
            throw new RobRequestException(CustomResponseCode.ERR951);
        }
        String deviceType = movesDzdc.getDeviceType();
        log.info("??????AGV :{} ?????? ++++++++++++++++++++++++++", deviceType);
//        ????????????
        String basketType = movesDzdc.getBasketType();
//        ??????????????????
        String palletType = movesDzdc.getPalletType();
        try {
            String orderCode = movesDzdc.getOrderCode();
            String lineNo = movesDzdc.getLineNo();
            log.info("??????:{} ,?????? AGV ?????????{} ", deviceType, movesDzdc);
            if (StringUtils.isEmpty(basketType) || StringUtils.isEmpty(palletType) || StringUtils.isEmpty(lineNo) | StringUtils.isEmpty(orderCode)) {
                log.warn("??????:{} , ?????? AGV ?????????????????????{}", deviceType, movesDzdc);
                throw new RobRequestException(CustomResponseCode.ERR12);
            }
            MomUpPoint momUpPoint = redisUtil.get(RedisKey.Rob_Call_Material + orderCode + lineNo + basketType);
            if (StringUtils.isEmpty(momUpPoint)) {
                momUpPoint = pointService.getStationCode(basketType, orderCode, lineNo);
                if (StringUtils.isEmpty(momUpPoint)) {
                    log.warn("????????????{}??????{}??????{}????????????????????????????????????????????????????????????", basketType, orderCode, lineNo);
                    throw new RobRequestException(CustomResponseCode.ERR68);
                }
                redisUtil.set(RedisKey.Rob_Call_Material + orderCode + lineNo + basketType, momUpPoint);
            }
            String pointModel = momUpPoint.getPointModel();
            movesDzdc.setPointModel(pointModel);
            movesDzdc.setExternalCode(momUpPoint.getExternalCode());
            MonOrder momOrder = momOrderService.getMomOrder(orderCode, lineNo, MomProgressStatus.LOADING);
            if (momOrder != null) {
                movesDzdc.setWiporderno(momOrder.getWiporderno());
                movesDzdc.setProTaskOrderId(momOrder.getProTaskOrderId());
            }
//            ?????? ???????????? 4
            if (AgvPalletType.DNU.equals(palletType)) {
                Result<String> result = callBoxMaterial.moveAgv(movesDzdc);
                return result;
            }
//            ?????? ???????????? 7
            if (AgvPalletType.GNU.equals(palletType)) {
                return callEmptyBox.moveAgv(movesDzdc);
            }
//           ?????? ???????????? 2
            if (AgvPalletType.BNU.equals(palletType)) {
                return removeEmptyBox.moveAgv(movesDzdc);
            }
//            ?????? ???????????? 1
            if (AgvPalletType.ANU.equals(palletType)) {
                return exportFullBox.moveAgv(movesDzdc);
            }
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        } catch (Throwable throwable) {
            log.error("?????????????????? movesDzdc???{}????????????{}", JSONObject.toJSONString(movesDzdc), throwable.getMessage(), throwable);
            throw throwable;
        }
    }
}
