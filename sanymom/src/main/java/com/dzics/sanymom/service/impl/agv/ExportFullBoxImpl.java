package com.dzics.sanymom.service.impl.agv;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.dao.MomOrderQrCodeMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.constant.PointType;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.framework.OperLogCallAgv;
import com.dzics.sanymom.model.common.MyReqMomType;
import com.dzics.sanymom.model.request.agv.AgvParmsDto;
import com.dzics.sanymom.model.request.agv.MaterialParmsDto;
import com.dzics.sanymom.model.response.searchframe.MaterialFrameRes;
import com.dzics.sanymom.service.AgvService;
import com.dzics.sanymom.service.MomHttpRequestService;
import com.dzics.sanymom.service.impl.http.SendFullFrameMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Classname ExportFullBoxImpl
 * @Description 移出满料框
 * @Date 2022/5/13 9:39
 * @Created by NeverEnd
 */

@Slf4j
@Service
public class ExportFullBoxImpl implements AgvService<String> {
    @Autowired
    private MomMaterialPointService pointService;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomHttpRequestService momHttpRequestService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private MomDistributionWaitRequestService requestService;
    @Autowired
    private MomDistributionWaitRequestLogService requestLogService;
    @Autowired
    private SendFullFrameMomHttpImpl sendFullFrameMomHttp;
    @Autowired
    private MomOrderQrCodeMapper orderQrCodeMapper;
    @Autowired
    private MomOrderMapper orderMapper;

    /**
     * 移动AGV
     * 满料框移出
     *
     * @param frameMoves
     * @return
     */
    @OperLogCallAgv(operModul = "满料框", operDesc = "移出满料框")
    @Override
    public Result<String> moveAgv(EmptyFrameMovesDzdc frameMoves) {
        String deviceType = frameMoves.getDeviceType();
        String pointModel = frameMoves.getPointModel();
        String sourceNo = frameMoves.getExternalCode();
        log.info("------------------------------------------------------");
        log.info("请求 移出满料框: {} ", deviceType);
        log.info("请求 移出满料框 请求参数: {} ", frameMoves);
        String palletNo = frameMoves.getPalletNo();
        String orderCode = frameMoves.getOrderCode();
        String lineNo = frameMoves.getLineNo();
        Integer quantity = frameMoves.getQuantity();
        String basketType = frameMoves.getBasketType();
        List<String> serialNos = frameMoves.getSerialNos();
//        去除序列号中的前缀
        deletePrefix(serialNos);
        MonOrder momOrder = getMonOrder(orderCode, lineNo, serialNos);
        if("DZICS-Manual".equals(momOrder.getWiporderno())){
            log.error("订单：{} 产线：{} 当前物料{}绑定为手动订单，不允许进行自动物流 ", orderCode, lineNo, serialNos.get(0));
            throw new RobRequestException(CustomResponseCode.ERR97);
        }
        String productNo = momOrder.getProductNo();
        frameMoves.setWiporderno(momOrder.getWiporderno());
        frameMoves.setProTaskOrderId(momOrder.getProTaskOrderId());
        //            空料框回缓存区，或者上下同料点移出满料 清空料框
        if (StringUtils.isEmpty(palletNo)) {
//                查询MOM料框编码
            log.warn("订单：{} 产线：{} 移出满料框 料框编码为空，执行查询MOM料框编码: palletNo :{} ", orderCode, lineNo, palletNo);
            MaterialFrameRes stringPalletType = momHttpRequestService.getStringPalletType(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), orderCode, lineNo, sourceNo, "");
            if (StringUtils.isEmpty(stringPalletType.getPalletNo())) {
                log.error("订单：{} 产线：{} 移出满料框 查询MOM料框编码不存在: palletNo :{} ", orderCode, lineNo, palletNo);
                throw new RobRequestException(CustomResponseCode.ERR73);
            }
            palletNo = stringPalletType.getPalletNo();
        }
        if ("DZ-1871".equals(orderCode) || "DZ-1872".equals(orderCode) || "DZ-1873".equals(orderCode) || "DZ-1874".equals(orderCode) || "DZ-1875".equals(orderCode)
                || "DZ-1876".equals(orderCode) || "DZ-1877".equals(orderCode) || "DZ-1878".equals(orderCode) || "DZ-1879".equals(orderCode)
                || "DZ-1880".equals(orderCode)) {
            boolean b = momHttpRequestService.updatePointPallet(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), lineNo, orderCode, sourceNo, palletNo);
        }
        if (StringUtils.isEmpty(palletNo)) {
            log.error("订单：{} 产线：{} 移出满料框  料框编码不存在: palletNo :{} ", orderCode, lineNo, palletNo);
            throw new RobRequestException(CustomResponseCode.ERR73);
        }
        if (quantity <= 0) {
            log.warn("订单：{} 产线：{} ,移出满料框 物料数量为零：{}", orderCode, lineNo, quantity);
        }
//        小车绑定的工位信息
        String stationId = pointService.getNextPoint(orderCode, lineNo, basketType);
        if (StringUtils.isEmpty(stationId)) {
            log.error("订单：{},产线：{} 没有工位,小车,绑定的料点信息 MomMaterialPoint :{}", orderCode, lineNo, stationId);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
        }
        String requireTime = dateUtil.dateFormatToStingYmdHmsMom(new Date());
        AgvParmsDto parmsDto = new AgvParmsDto();
        parmsDto.setReqId(redisUniqueID.getkey());
        parmsDto.setReqSys(MomReqContent.REQ_SYS);
        if (PointType.NG.equals(pointModel)) {
            parmsDto.setReqType(MomReqContent.CALL_AGV_REQ_TYPE_6);
        } else {
            parmsDto.setReqType(MomReqContent.CALL_AGV_REQ_TYPE_4);
        }
        parmsDto.setPalletType("");
        parmsDto.setPalletNo(palletNo);
        parmsDto.setSourceNo(sourceNo);
        parmsDto.setDestNo("");
        parmsDto.setRequireTime(requireTime);
        parmsDto.setSendTime(requireTime);
        parmsDto.setParamRsrv1("");
        parmsDto.setParamRsrv2("");
        parmsDto.setParamRsrv3("");
        List<MaterialParmsDto> materialLists = new ArrayList<>();
        Object openNo = redisUtil.get(RedisKey.OPEN_SERIALNOS + orderCode);
        if (openNo != null && Integer.parseInt(openNo.toString()) == 1) {
            if (CollectionUtils.isEmpty(serialNos)) {
                throw new RobRequestException(CustomResponseCode.ERR79);
            }
            for (String serialNo : serialNos) {
                MaterialParmsDto materialList = new MaterialParmsDto();
                materialList.setMaterialNo(productNo);
                materialList.setQuantity(BigDecimal.ONE);
                materialList.setSerialNo(serialNo);
                materialLists.add(materialList);
            }
            parmsDto.setMaterialList(materialLists);
        } else {
            MaterialParmsDto materialList = new MaterialParmsDto();
            materialList.setMaterialNo(productNo);
            materialList.setQuantity(new BigDecimal(quantity));
            materialList.setSerialNo("");
            materialLists.add(materialList);
            parmsDto.setMaterialList(materialLists);
        }
        //头信息
        RequestHeaderVo<AgvParmsDto> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(MomTaskType.CALL_MATERIAL);
        requestHeaderVo.setVersion(MomVersion.VERSION);
        requestHeaderVo.setReported(parmsDto);
        MomDistributionWaitRequestLog requestLog = momHttpRequestService.getMomDistributionWaitRequestLog(parmsDto);
        Gson gson = new Gson();
        String reqJson = gson.toJson(requestHeaderVo);
        String code = "";
        try {
            redisUtil.set(RedisKey.momHttpRequestService_getMyReqTypeId + parmsDto.getReqId(), MyReqMomType.DISTRIBUTION, 24 * 3600);
            log.info("请求MOM 移出满料框 接口地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = sendFullFrameMomHttp.sendPost(frameMoves.getInnerGroupId(), orderCode, lineNo, frameMoves.getGroupId(), momRequestPath.ipPortPath, requestHeaderVo, ResultVo.class);
            code = body.getCode();
            log.info("请求MOM 移出满料框 接口 地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
            requestLog.setStatusCode(body.getStatusCode());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
//               请求正常
                requestLog.setResMomCode(true);
                requestLog.setResMsg(body.getMsg());
                Result<String> okok = Result.ok("OKOK");
                okok.setPalletNoMomRes(palletNo);
                return okok;
            } else {
                log.error("订单：{},产线：{} ,请求参数: reqJson：{} ,移出满料框 响应信息:{}", orderCode, lineNo, reqJson, body);
                requestLog.setResMomCode(false);
                throw new RobRequestException(body.getMsg());
            }
        } catch (ResourceAccessException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 移出满料框 请求参数: reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } catch (RobRequestException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 移出满料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            throw throwable;
        } catch (Throwable throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 移出满料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } finally {
            try {
                MomDistributionWaitRequest request = momHttpRequestService.getMomDistributionWaitRequest(parmsDto, code, orderCode, lineNo, basketType, pointModel, MomTaskType.CALL_MATERIAL);
                request.setProductNo(productNo);
                requestService.save(request);
            } catch (Throwable e) {
                log.error("发送 移出满料框 请求错误，在保存记录请求记录用于二次任务发送时，保存失败：{}", e.getMessage(), e);
            }
            requestLogService.save(requestLog);
            log.info("请求 移出满料框 结束: {} ", deviceType);
            log.info("------------------------------------------------------");
        }
    }

    /**
     * 去除序列号第一个下划线_前的字符
     * @param serialNos 序列号二维码
     */
    private static void deletePrefix(List<String> serialNos) {
        if (CollectionUtils.isNotEmpty(serialNos)) {
            for (int i = 0; i < serialNos.size(); i++) {
                String value = serialNos.get(i);
                if(value.contains("_")){
                    serialNos.set(i,value.substring(value.indexOf("_")+1,value.length()));
                }else{
                    serialNos.set(i,value);
                }
            }
        }
    }


    private MonOrder getMonOrder(String orderCode, String lineNo, List<String> serialNos) {
        MonOrder momOrder = null;
        if (CollectionUtils.isNotEmpty(serialNos)) {
            for (String serialNo : serialNos) {
                try {
                    MomOrderQrCode qrMomOrder = orderQrCodeMapper.getQrMomOrder(serialNo, orderCode, lineNo);
                    if (qrMomOrder != null) {
                        momOrder = orderMapper.selectById(qrMomOrder.getProTaskOrderId());
                        if("DZICS-Manual".equals(momOrder.getWiporderno())){
                            throw new RobRequestException(CustomResponseCode.ERR651);
                        }
                        break;
                    }
                } catch (Throwable throwable) {
                    log.error("根据二维码获取订单信息失败，二维码：{}", serialNo, throwable);
                }
            }
        }
        if (momOrder == null) {
            log.error("根据产品二维码也无法获取到绑定的订单信息，二维码：{}", serialNos);
            throw new RobRequestException(CustomResponseCode.ERR651);
        }
        return momOrder;
    }
}
