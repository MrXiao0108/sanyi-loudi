package com.dzics.business.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.config.MapConfig;
import com.dzics.business.model.vo.udp.SendPlcModel;
import com.dzics.business.service.WorkpieceDataService;
import com.dzics.business.service.mq.RabbitmqService;

import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.DzProductMapper;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.DetectionDetailsDo;
import com.dzics.common.model.request.UploadProductDetectionVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class WorkpieceDataServiceImpl implements WorkpieceDataService {

    @Value("${dzics.huapei.orderNo}")
    private String orderNo;
    @Value("${dzics.huapei.lineNo}")
    private String lineNo;


    @Value("${dzdc.udp.client.qr.port}")
    private Integer plcPort;

    @Autowired
    private DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private DzProductMapper dzProductMapper;

    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public Result uploadProductDetectionVo(UploadProductDetectionVo uploadProductDetectionVo) {
        if (uploadProductDetectionVo == null) {
            log.error("??????????????????????????????????????????{}", uploadProductDetectionVo);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        log.info("??????????????????:" + uploadProductDetectionVo.toString());
        //????????????
        if (uploadProductDetectionVo.getProductNo() == null) {
            log.error("????????????????????????,??????????????????:{}", uploadProductDetectionVo.getProductNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //???????????????
        if (uploadProductDetectionVo.getProductCode() == null) {
            log.error("????????????????????????,????????????????????????:{}", uploadProductDetectionVo.getProductCode());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //????????????
        if (uploadProductDetectionVo.getStationNo() == null) {
            log.error("????????????????????????,??????????????????:{}", uploadProductDetectionVo.getStationNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //????????????
        if (uploadProductDetectionVo.getMachineNo() == null) {
            log.error("????????????????????????,??????????????????:{}", uploadProductDetectionVo.getMachineNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //???????????????
        if (uploadProductDetectionVo.getDetectionFlag() == null) {
            log.error("????????????????????????,?????????????????????:{}", uploadProductDetectionVo.getDetectionFlag());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //??????????????????
        if (uploadProductDetectionVo.getDetectionNum() == null) {
            log.error("????????????????????????,?????????????????????:{}", uploadProductDetectionVo.getDetectionNum());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //?????????????????????
        if (uploadProductDetectionVo.getDetectionDetails() == null
                || uploadProductDetectionVo.getDetectionDetails().size() != uploadProductDetectionVo.getDetectionNum().intValue()) {
            log.error("????????????????????????,??????????????????????????????????????????????????????:{} ,?????????:{}", uploadProductDetectionVo.getDetectionDetails().size(), uploadProductDetectionVo.getDetectionNum());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_115);
        }
        //????????????
        DzProduct dzProduct = dzProductMapper.selectOne(new QueryWrapper<DzProduct>().eq("product_no", uploadProductDetectionVo.getProductNo()));
        if (dzProduct == null) {
            log.error("????????????????????????,?????????????????????:{}", uploadProductDetectionVo.getProductNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_113);
        }
        //?????????????????? OK?????? ?????????????????? 12  12  12??????
        Object o = redisUtil.get(RedisKey.WORKPIECE_EXCHANGE_RESET);
        String flag = o != null ? o.toString() : null;
        //???????????????????????????
        Object last = redisUtil.get(RedisKey.Last_Work_Number);
        String lastNo = last != null ? last.toString() : "2";
        //???????????????
        if (flag != null) {
            if (flag.equals("OK")) {
                //?????????OK ????????????????????????????????????1
                lastNo = "1";
                redisUtil.set(RedisKey.WORKPIECE_EXCHANGE_RESET, "NO");
            } else {
                //????????????OK ???????????? ?????????????????????????????????????????????
                lastNo = lastNo.equals("1") ? "2" : "1";
            }
        } else {
            //??????????????????
            lastNo = "1";
            redisUtil.set(RedisKey.WORKPIECE_EXCHANGE_RESET, "NO");
        }
        redisUtil.set(RedisKey.Last_Work_Number, lastNo);
        DzWorkpieceData dzWorkpieceData = new DzWorkpieceData();
        dzWorkpieceData.setEquipmentType(2);
        dzWorkpieceData.setProductNo(uploadProductDetectionVo.getProductNo());//????????????
        String code = uploadProductDetectionVo.getProductCode().replace("{", "").replace("}", "");
        dzWorkpieceData.setProducBarcode(code);//???????????????
        dzWorkpieceData.setName(dzProduct.getProductName());//????????????
        dzWorkpieceData.setWorkNumber(lastNo);//????????????
        dzWorkpieceData.setMachineNumber(uploadProductDetectionVo.getMachineNo());//????????????
        dzWorkpieceData.setOrderNo(orderNo);
        dzWorkpieceData.setLineNo(lineNo);
        LocalDate now = LocalDate.now();
        dzWorkpieceData.setDate(now);
        dzWorkpieceData.setCheckMonth(now.toString().substring(0, 7));
        dzWorkpieceData.setQrCode(FinalCode.UN_BOUND_QR_CODE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dzWorkpieceData.setDetectorTime(format.parse(uploadProductDetectionVo.getDetectionTime()));//????????????
        } catch (Exception e) {
            log.error("????????????????????????,????????????????????????:{}", uploadProductDetectionVo.getDetectionTime());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_114);
        }
        dzWorkpieceData.setCreateTime(new Date());
        dzWorkpieceData.setOutOk(uploadProductDetectionVo.getDetectionFlag());//???????????????

        List<DetectionDetailsDo> detectionDetails = uploadProductDetectionVo.getDetectionDetails();//????????????

        try {
            dzWorkpieceData.setDetect01(detectionDetails.get(0).getValue());
            dzWorkpieceData.setOutOk01(detectionDetails.get(0).getFlag());

            dzWorkpieceData.setDetect02(detectionDetails.get(1).getValue());
            dzWorkpieceData.setOutOk02(detectionDetails.get(1).getFlag());

            dzWorkpieceData.setDetect03(detectionDetails.get(2).getValue());
            dzWorkpieceData.setOutOk03(detectionDetails.get(2).getFlag());

            dzWorkpieceData.setDetect04(detectionDetails.get(3).getValue());
            dzWorkpieceData.setOutOk04(detectionDetails.get(3).getFlag());

            dzWorkpieceData.setDetect05(detectionDetails.get(4).getValue());
            dzWorkpieceData.setOutOk05(detectionDetails.get(4).getFlag());

            dzWorkpieceData.setDetect06(detectionDetails.get(5).getValue());
            dzWorkpieceData.setOutOk06(detectionDetails.get(5).getFlag());

            dzWorkpieceData.setDetect07(detectionDetails.get(6).getValue());
            dzWorkpieceData.setOutOk07(detectionDetails.get(6).getFlag());

            dzWorkpieceData.setDetect08(detectionDetails.get(7).getValue());
            dzWorkpieceData.setOutOk08(detectionDetails.get(7).getFlag());

            dzWorkpieceData.setDetect09(detectionDetails.get(8).getValue());
            dzWorkpieceData.setOutOk09(detectionDetails.get(8).getFlag());

            dzWorkpieceData.setDetect10(detectionDetails.get(9).getValue());
            dzWorkpieceData.setOutOk10(detectionDetails.get(9).getFlag());

            dzWorkpieceData.setDetect11(detectionDetails.get(10).getValue());
            dzWorkpieceData.setOutOk11(detectionDetails.get(10).getFlag());

            dzWorkpieceData.setDetect12(detectionDetails.get(11).getValue());
            dzWorkpieceData.setOutOk12(detectionDetails.get(11).getFlag());

            dzWorkpieceData.setDetect13(detectionDetails.get(12).getValue());
            dzWorkpieceData.setOutOk13(detectionDetails.get(12).getFlag());

            int insert = dzWorkpieceDataMapper.insert(dzWorkpieceData);
            if (insert > 0) {
                //???????????????????????????
                sendQrCodeMqUdp(dzWorkpieceData);
                return new Result(CustomExceptionType.OK, Message.OK_5);
            }
            return new Result(CustomExceptionType.SYSTEM_ERROR);
        } catch (Exception e) {
            log.error("????????????????????????:{}", uploadProductDetectionVo.getDetectionDetails());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_116);
        }

    }


    /**
     * ??????????????????????????????,?????????
     */
    @Override
    public void sendQrCodeMqUdp(DzWorkpieceData dzWorkpieceData) {
        Map<String, String> mapIps = mapConfig.getMaps();
        if (CollectionUtils.isNotEmpty(mapIps)) {
            String plcIp = mapIps.get(dzWorkpieceData.getOrderNo() + dzWorkpieceData.getLineNo());
            SendPlcModel sendPlcModel = new SendPlcModel();
            sendPlcModel.setIp(plcIp);
            sendPlcModel.setPort(plcPort);
//        ??????????????????????????????,?????????
            sendPlcModel.setMessage("Q,1," + dzWorkpieceData.getWorkNumber() + "," + dzWorkpieceData.getId());
            rabbitmqService.sendQrCodeMqUdp(JSONObject.toJSONString(sendPlcModel));
        } else {
            log.error("???????????????UDP???????????????IP ?????????mapIps: {} ", mapIps);
        }
    }

}
