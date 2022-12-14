package com.dzics.sanymom.service.impl.agv;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.MomMaterialPointMapper;
import com.dzics.common.dao.MomMaterialWarehouseMapper;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.agv.MomUpPoint;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.custom.CallMaterial;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzWorkStationManagementService;
import com.dzics.common.service.MomWaitCallMaterialReqLogService;
import com.dzics.common.service.MomWaitCallMaterialReqService;
import com.dzics.common.service.MomWaitCallMaterialService;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.exception.CustomMomExceptionReq;
import com.dzics.sanymom.framework.OperLogCallAgv;
import com.dzics.sanymom.model.common.MyReqMomType;
import com.dzics.sanymom.model.request.agv.AgvParmsDto;
import com.dzics.sanymom.model.request.agv.MaterialParmsDto;
import com.dzics.sanymom.service.AgvService;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.impl.http.CallMaterialMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Classname CallBoxMaterialImpl
 * @Description ????????????
 * @Date 2022/5/12 16:56
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class CallMaterialBoxImpl implements AgvService<String> {
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private RedisUtil<Object> redisUtil;
    @Autowired
    private MomRequestPath momRequestPath;
    /**
     * ??????dao
     */
    @Autowired
    private MomMaterialPointMapper materialPointMapper;
    @Autowired
    private CallMaterialMomHttpImpl callMaterialMomHttp;
    /**
     * ??????dao
     */
    @Autowired
    private MomWaitCallMaterialService callMaterialService;
    @Autowired
    private MomMaterialWarehouseMapper warehouseMapper;
    @Autowired
    private MomWaitCallMaterialReqLogService waitCallMaterialReqLogService;
    @Autowired
    private MomWaitCallMaterialReqService waitCallMaterialReqService;
    @Autowired
    private MomOrderMapper orderMapper;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private DzWorkStationManagementService workStationManagementService;

    @OperLogCallAgv(operModul = "??????", operDesc = "????????????")
    @Override
    public Result<String> moveAgv(EmptyFrameMovesDzdc frameMoves) {
        String deviceType = frameMoves.getDeviceType();
        String sourceNo = frameMoves.getExternalCode();
        String lineNo = frameMoves.getLineNo();
        String orderCode = frameMoves.getOrderCode();
        String basketType = frameMoves.getBasketType();
        log.info("------------------------------------------------------");
        log.info("????????????: {} ", deviceType);
        log.info("????????????  ????????????: {} ", frameMoves);
        String proTaskOrderId = frameMoves.getProTaskOrderId();
        String wiporderno = frameMoves.getWiporderno();
        if (StringUtils.isEmpty(proTaskOrderId) || StringUtils.isEmpty(wiporderno)) {
            log.error("???????????????????????????????????????, proTaskOrderId: {} wiporderno: {} ", proTaskOrderId, wiporderno);
            throw new RobRequestException(CustomResponseCode.ERR69);
        }
        if("DZICS-Manual".equals(wiporderno)){
            log.error("?????????{} ?????????{} ????????????{}??????????????????????????????????????????????????? ", orderCode, lineNo);
            throw new RobRequestException(CustomResponseCode.ERR97);
        }
        MomUpPoint momUpPoint = (MomUpPoint) redisUtil.get(RedisKey.Rob_Call_Material + orderCode + lineNo + basketType);
        if (StringUtils.isEmpty(momUpPoint)) {
            momUpPoint = materialPointMapper.getStationCode(basketType, orderCode, lineNo);
            if (StringUtils.isEmpty(momUpPoint)) {
                log.warn("????????????{}??????{}??????{}????????????????????????????????????????????????????????????", basketType, orderCode, lineNo);
                throw new RobRequestException(CustomResponseCode.ERR68);
            }
            redisUtil.set(RedisKey.Rob_Call_Material + orderCode + lineNo + basketType, momUpPoint);
        }
        log.info("?????????????????? ??????: {} , ????????????: {}", basketType, JSONObject.toJSONString(momUpPoint));
//        ???????????????????????????
        String stationCode = momUpPoint.getStationCode();
        List<CallMaterial> materials = callMaterialService.getWorkStation(proTaskOrderId, stationCode);
        if (CollectionUtils.isEmpty(materials)) {
            log.error("??????????????????????????????proTaskId: {},stationCode: {}", proTaskOrderId, stationCode);
            throw new RobRequestException(CustomResponseCode.ERR70);
        }
        CallMaterial materialDef = new CallMaterial();
        if("DZ-1955".equals(orderCode) || "DZ-1956".equals(orderCode)){
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
//            ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            List<DzWorkStationManagement> list = workStationManagementService.list(new QueryWrapper<DzWorkStationManagement>()
                    .eq("order_id", line.getOrderId())
                    .eq("line_id", line.getId())
                    .isNotNull("dz_station_code")
                    .orderByAsc("dz_station_code"));
            if(list.get(0).getDzStationCode().equals(stationCode)){
                MonOrder monOrder = orderMapper.selectById(proTaskOrderId);
                materialDef.setMaterialNo(monOrder.getProductNo());
                materialDef.setMaterialType(String.valueOf(1));
            }else{
                materialDef = callMaterialService.getCallMaterial(lineNo, orderCode, materials);
            }
        }else{
            materialDef = callMaterialService.getCallMaterial(lineNo, orderCode, materials);
        }
//        ??????????????????
        MomMaterialWarehouse orderAndMaterial = warehouseMapper.getOrderAndMaterial(orderCode, lineNo, materialDef.getMaterialNo());
        if (orderAndMaterial == null) {
            log.error("????????????????????????????????????????????????orderCode: {},lineNo: {},materialNo: {}", orderCode, lineNo, materialDef.getMaterialNo());
            throw new RobRequestException(CustomResponseCode.ERR701);
        }
        Long quantity = orderAndMaterial.getQuantity();
        if (quantity <= 0) {
            log.error("?????????????????????????????????orderCode: {},lineNo: {},materialNo: {},quantity: {}", orderCode, lineNo, materialDef.getMaterialNo(), quantity);
            throw new RobRequestException(CustomResponseCode.ERR702);
        }
//       ??????????????????
        String reqId = redisUniqueID.getkey();
        AgvParmsDto dto = new AgvParmsDto();
        dto.setReqId(reqId);
        dto.setReqSys(MomReqContent.REQ_SYS);
        dto.setReqType(MomReqContent.CALL_AGV_REQ_TYPE_1);
        dto.setPalletType("");
        dto.setPalletNo("");
        dto.setSourceNo("");
        dto.setDestNo(sourceNo);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        Date date = new Date();
        String time = format.format(date);
        dto.setRequireTime(time);
        dto.setSendTime(time);
        dto.setParamRsrv1("");
        dto.setParamRsrv2("");
        dto.setParamRsrv3("");
        MaterialParmsDto parmsDto = new MaterialParmsDto();
        parmsDto.setMaterialNo(materialDef.getMaterialNo());
        parmsDto.setQuantity(BigDecimal.ZERO);
        parmsDto.setSerialNo("");
        List<MaterialParmsDto> parmsDtos = new ArrayList<>();
        parmsDtos.add(parmsDto);
        dto.setMaterialList(parmsDtos);
        RequestHeaderVo<AgvParmsDto> expected = new RequestHeaderVo<>();
        expected.setTaskId(redisUniqueID.getUUID());
        expected.setTaskType(MomTaskType.CALL_MATERIAL);
        expected.setVersion(MomVersion.VERSION);
        expected.setReported(dto);
        Gson gson = new Gson();
        String reqJson = gson.toJson(expected);
        MomWaitCallMaterialReqLog reqLog = new MomWaitCallMaterialReqLog();
        reqLog.setReqId(reqId);
        reqLog.setSendMsg(reqJson);
        try {
            reqLog.setMaterialType(materialDef.getMaterialType());
            reqLog.setSendTime(new Date());
            reqLog.setOrgCode("ROB");
            reqLog.setDelFlag(false);
            reqLog.setCreateBy("ROB");
            reqLog.setCreateTime(new Date());
            reqLog.setUpdateBy("ROB");
            redisUtil.set(RedisKey.momHttpRequestService_getMyReqTypeId + reqId, MyReqMomType.CALL_MATERIAL, 24 * 3600);
            log.info("??????MOM???????????? ?????????{} ,?????????{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = callMaterialMomHttp.sendPost(frameMoves.getInnerGroupId(), orderCode, lineNo, frameMoves.getGroupId(), momRequestPath.ipPortPath, expected, ResultVo.class);
            String gsn = gson.toJson(body);
            log.info("??????MOM???????????? ?????????{} ,???????????????{}", momRequestPath.ipPortPath, gsn);
            String code = body.getCode();
            reqLog.setResMomCode(code);
            reqLog.setResMsg(gsn);
            reqLog.setStatus(body.getStatusCode());
            saveWaitCallMateReq(date, reqId, MomReqContent.REQ_SYS, "materialType", "sequenceNo", "oprSequenceNo",
                    sourceNo, wiporderno, MomReqContent.CALL_AGV_REQ_TYPE_1, parmsDtos, gson, body, orderAndMaterial.getId(), basketType, orderAndMaterial.getMaterialNo());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
                return Result.ok("OKOK");
            }
            Long quantity1 = orderAndMaterial.getQuantity();
//            TODO  ????????????
            log.error("?????????{} ?????????{},??????MOM?????????????????? body:{}", orderCode, lineNo, body);
            throw new RobRequestException(CustomResponseCode.ERR72);
        } catch (CustomMomExceptionReq ex) {
            reqLog.setResMsg(ex.getMessage());
            log.error("???????????????????????????{}", ex.getMessage(), ex);
            throw ex;
        } catch (ResourceAccessException throwable) {
            reqLog.setResMsg(throwable.getMessage());
            log.error("???????????????????????????{}", throwable.getMessage(), throwable);
            throw throwable;
        } catch (RobRequestException throwable) {
            reqLog.setResMsg(throwable.getMessage());
            log.error("???????????????????????????{}", throwable.getMessage(), throwable);
            throw throwable;
        } finally {
            waitCallMaterialReqLogService.save(reqLog);
        }
    }


    public void saveWaitCallMateReq(Date date, String reqId, String reqSys, String materialType, String sequenceNo, String oprSequenceNo, String sourceNo, String wipOrderNo,
                                    String reqType, List<MaterialParmsDto> materialLists, Gson gson, ResultVo body, String waitMaterialId, String basketType, String productNo) {
        MomWaitCallMaterialReq callMaterialReq = new MomWaitCallMaterialReq();
        callMaterialReq.setWaitMaterialId(waitMaterialId);
        callMaterialReq.setReqid(reqId);
        callMaterialReq.setReqsys(reqSys);
        callMaterialReq.setProductNo(productNo);
        callMaterialReq.setMaterialType(materialType);
        callMaterialReq.setSequenceno(sequenceNo);
        callMaterialReq.setBasketType(basketType);
        callMaterialReq.setOprsequenceno(oprSequenceNo);
        callMaterialReq.setReqtype(reqType);
        callMaterialReq.setSourceno(sourceNo);
        callMaterialReq.setWiporderno(wipOrderNo);
        callMaterialReq.setRequiretime(date);
        callMaterialReq.setSendtime(date);
        callMaterialReq.setMateriallist(gson.toJson(materialLists));
        callMaterialReq.setReqStatus(body.getCode());
        callMaterialReq.setOrgCode("ROB");
        callMaterialReq.setDelFlag(false);
        callMaterialReq.setCreateBy("ROB");
        callMaterialReq.setCreateTime(new Date());
        waitCallMaterialReqService.save(callMaterialReq);
    }


}
