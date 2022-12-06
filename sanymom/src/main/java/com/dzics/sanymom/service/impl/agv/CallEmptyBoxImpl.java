package com.dzics.sanymom.service.impl.agv;

import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.entity.MomDistributionWaitRequest;
import com.dzics.common.model.entity.MomDistributionWaitRequestLog;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomDistributionWaitRequestLogService;
import com.dzics.common.service.MomDistributionWaitRequestService;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.framework.OperLogCallAgv;
import com.dzics.sanymom.model.common.MyReqMomType;
import com.dzics.sanymom.model.request.agv.AgvParmsDto;
import com.dzics.sanymom.model.response.searchframe.MaterialFrameRes;
import com.dzics.sanymom.service.AgvService;
import com.dzics.sanymom.service.MomHttpRequestService;
import com.dzics.sanymom.service.impl.http.CallFrameMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Date;

/**
 * @Classname CallEmptyBoxImpl
 * @Description 描述
 * @Date 2022/5/12 16:42
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class CallEmptyBoxImpl implements AgvService<String> {
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomHttpRequestService momHttpRequestService;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private MomDistributionWaitRequestService requestService;
    @Autowired
    private MomDistributionWaitRequestLogService requestLogService;

    @Autowired
    private CallFrameMomHttpImpl callFrameMomHttp;

    @OperLogCallAgv(operModul = "空料框", operDesc = "请求空料框")
    @Override
    public Result<String> moveAgv(EmptyFrameMovesDzdc frameMoves) {
        String deviceType = frameMoves.getDeviceType();
        String pointModel = frameMoves.getPointModel();
        String sourceNo = frameMoves.getExternalCode();
        log.info("------------------------------------------------------");
        log.info("请求空料框: {} ", deviceType);
        log.info("请求空料框  请求参数: {} ", frameMoves);
        String orderCode = frameMoves.getOrderCode();
        String lineNo = frameMoves.getLineNo();
        String basketType = frameMoves.getBasketType();
        String proTaskOrderId = frameMoves.getProTaskOrderId();
        String wiporderno = frameMoves.getWiporderno();
        if("DZICS-Manual".equals(wiporderno)){
            log.error("订单：{} 产线：{} 当前物料{}绑定为手动订单，不允许进行自动物流 ", orderCode, lineNo);
            throw new RobRequestException(CustomResponseCode.ERR97);
        }
        if (StringUtils.isEmpty(proTaskOrderId)) {
            log.warn("订单：{} 产线：{} 当前没有订单在生产 proTaskOrderId :{}, wiporderno: {}", orderCode, lineNo, proTaskOrderId,wiporderno);
            throw new RobRequestException(CustomResponseCode.ERR65);
        }
        String productNo = frameMoves.getSyProductNo();
        //           请求空料框时需要  获取料框类型
        MaterialFrameRes materialFrameRes = momHttpRequestService.getStringPalletType(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), orderCode, lineNo, sourceNo, productNo);
        String requireTime = dateUtil.dateFormatToStingYmdHmsMom(new Date());
        AgvParmsDto parmsDto = new AgvParmsDto();
        parmsDto.setReqId(redisUniqueID.getkey());
        parmsDto.setReqSys(MomReqContent.REQ_SYS);
        parmsDto.setReqType(MomReqContent.CALL_AGV_REQ_TYPE_3);
        parmsDto.setPalletType(materialFrameRes.getPalletType());
        parmsDto.setPalletNo("");
        parmsDto.setSourceNo("");
        parmsDto.setDestNo(sourceNo);
        parmsDto.setRequireTime(requireTime);
        parmsDto.setSendTime(requireTime);
        parmsDto.setParamRsrv1("");
        parmsDto.setParamRsrv2("");
        parmsDto.setParamRsrv3("");
        parmsDto.setMaterialList(new ArrayList<>());
        //头信息
        RequestHeaderVo<AgvParmsDto> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(MomTaskType.CALL_MATERIAL);
        requestHeaderVo.setVersion(MomVersion.VERSION);
        requestHeaderVo.setReported(parmsDto);
        MomDistributionWaitRequestLog requestLog = momHttpRequestService.getMomDistributionWaitRequestLog(parmsDto);
        requestLog.setSourceno(sourceNo);
        Gson gson = new Gson();
        String reqJson = gson.toJson(requestHeaderVo);
        try {
            redisUtil.set(RedisKey.momHttpRequestService_getMyReqTypeId + parmsDto.getReqId(), MyReqMomType.DISTRIBUTION, 24 * 3600);
            log.info("请求MOM 空料框接口地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = callFrameMomHttp.sendPost(frameMoves.getInnerGroupId(), orderCode, lineNo, frameMoves.getGroupId(), momRequestPath.ipPortPath, requestHeaderVo, ResultVo.class);
            String code = body.getCode();
            log.info("请求MOM 空料框接口 地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
            try {
                MomDistributionWaitRequest request = momHttpRequestService.getMomDistributionWaitRequest(parmsDto, code, orderCode, lineNo, basketType, pointModel, MomTaskType.CALL_MATERIAL);
                request.setProductNo(productNo);
                request.setSourceno(sourceNo);
                requestService.save(request);
            } catch (Throwable e) {
                log.error("请求MOM 空料框请求错误，在保存记录请求记录用于二次任务发送时，保存失败：{}", e.getMessage(), e);
            }
            requestLog.setStatusCode(body.getStatusCode());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
//               请求正常
                requestLog.setResMomCode(true);
                requestLog.setResMsg(body.getMsg());
                Result<String> okok = Result.ok("OKOK");
                return okok;
            } else {
                log.error("订单：{},产线：{} ,请求参数: reqJson：{} ,请求MOM 空料框异常:{}", orderCode, lineNo, reqJson, body);
                requestLog.setResMomCode(false);
                throw new RobRequestException(body.getMsg());
            }
        } catch (ResourceAccessException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,请求MOM 空料框请求参数: reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } catch (RobRequestException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,请求MOM 空料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            throw throwable;
        } catch (Throwable throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,请求MOM 空料框 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } finally {
            requestLogService.save(requestLog);
            log.info("请求空料框 结束 : {} ", deviceType);
            log.info("------------------------------------------------------");
        }
    }
}
