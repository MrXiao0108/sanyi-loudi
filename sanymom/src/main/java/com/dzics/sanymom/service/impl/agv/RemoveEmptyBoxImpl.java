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
import com.dzics.sanymom.service.impl.http.SendEmptyFrameMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Date;

/**
 * @Classname RemoveEmptyBoxImpl
 * @Description 描述
 * @Date 2022/5/12 17:28
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class RemoveEmptyBoxImpl implements AgvService<String> {
    @Autowired
    private MomHttpRequestService httpRequestService;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private MomDistributionWaitRequestService requestService;
    @Autowired
    private MomDistributionWaitRequestLogService requestLogService;
    @Autowired
    private SendEmptyFrameMomHttpImpl sendEmptyFrameMomHttp;
    /**
     * 移动AGV
     * 空料框回缓存区
     *
     * @param frameMoves
     * @return
     */
    @OperLogCallAgv(operModul = "空料框",operDesc = "空料框移出")
    @Override
    public Result<String> moveAgv(EmptyFrameMovesDzdc frameMoves) {
        String deviceType = frameMoves.getDeviceType();
        String pointModel = frameMoves.getPointModel();
        String sourceNo = frameMoves.getExternalCode();
        log.info("------------------------------------------------------");
        log.info("空料框移出: {} ", deviceType);
        log.info("空料框移出  请求参数: {} ", frameMoves);
        String palletNo = frameMoves.getPalletNo();
        String orderCode = frameMoves.getOrderCode();
        String lineNo = frameMoves.getLineNo();
        String basketType = frameMoves.getBasketType();
        String pointType = "";
        if("DZICS-Manual".equals(frameMoves.getWiporderno())){
            log.error("订单：{} 产线：{} 当前物料{}绑定为手动订单，不允许进行自动物流 ", orderCode, lineNo);
            throw new RobRequestException(CustomResponseCode.ERR97);
        }
        //            空料框回缓存区，或者上下同料点移出满料 清空料框
        if (StringUtils.isEmpty(palletNo)) {
//                查询MOM料框编码
            log.warn("订单：{} 产线：{} 移出满料框｜拖出空料框 料框编码为空，执行查询MOM料框编码: palletNo :{} ", orderCode, lineNo, palletNo);
            MaterialFrameRes stringPalletType = httpRequestService.getStringPalletType(frameMoves.getInnerGroupId(), frameMoves.getGroupId(), orderCode, lineNo, sourceNo, "");
            if (StringUtils.isEmpty(stringPalletType.getPalletNo())) {
                log.error("订单：{} 产线：{} 移出满料框｜拖出空料框 查询MOM料框编码不存在: palletNo :{} ", orderCode, lineNo, palletNo);
                throw new RobRequestException(CustomResponseCode.ERR73);
            }
            palletNo = stringPalletType.getPalletNo();
            pointType = stringPalletType.getPalletType();
        }
//            更新料点信息
        boolean b = httpRequestService.updatePointPallet(frameMoves.getInnerGroupId(),frameMoves.getGroupId(), lineNo, orderCode, sourceNo, palletNo);
        String requireTime = dateUtil.dateFormatToStingYmdHmsMom(new Date());
        AgvParmsDto parmsDto = new AgvParmsDto();
        parmsDto.setReqId(redisUniqueID.getkey());
        parmsDto.setReqSys(MomReqContent.REQ_SYS);
        parmsDto.setReqType(MomReqContent.CALL_AGV_REQ_TYPE_2);
        parmsDto.setPalletType(pointType);
        parmsDto.setPalletNo(palletNo);
        parmsDto.setSourceNo(sourceNo);
        parmsDto.setDestNo("");
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
        MomDistributionWaitRequestLog requestLog = httpRequestService.getMomDistributionWaitRequestLog(parmsDto);
        Gson gson = new Gson();
        String reqJson = gson.toJson(requestHeaderVo);
        try {
            redisUtil.set(RedisKey.momHttpRequestService_getMyReqTypeId + parmsDto.getReqId(), MyReqMomType.DISTRIBUTION, 24 * 3600);
            log.info("请求MOM 空料框移出 接口地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
            ResultVo body = sendEmptyFrameMomHttp.sendPost(frameMoves.getInnerGroupId(), orderCode, lineNo, frameMoves.getGroupId(), momRequestPath.ipPortPath, requestHeaderVo, ResultVo.class);
            log.info("请求MOM 空料框移出 接口地址：{} ,响应信息：{}", momRequestPath.ipPortPath, gson.toJson(body));
            String code = body.getCode();
            try {
                MomDistributionWaitRequest request = httpRequestService.getMomDistributionWaitRequest(parmsDto,code, orderCode, lineNo, basketType, pointModel, MomTaskType.CALL_MATERIAL);
                requestService.save(request);
            } catch (Throwable e) {
                log.error("发送 空料框移出 请求错误，在保存记录请求记录用于二次任务发送时，保存失败：{}", e.getMessage(), e);
            }
            requestLog.setStatusCode(body.getStatusCode());
            if (MomReqContent.MOM_CODE_OK.equals(code) || MomReqContent.MOM_CALL_WAIT.equals(code)) {
//               请求正常
                requestLog.setResMomCode(true);
                requestLog.setResMsg(body.getMsg());
                return Result.ok("OKOK");
            } else {
                log.error("订单：{},产线：{} ,请求参数: reqJson：{} ,空料框移出异常:{}", orderCode, lineNo, reqJson, body);
                requestLog.setResMomCode(false);
                throw new RobRequestException(body.getMsg());
            }
        } catch (ResourceAccessException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 空料框移出请求参数: reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } catch (RobRequestException throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 空料框移出 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            throw throwable;
        } catch (Throwable throwable) {
            requestLog.setResMsg(throwable.getMessage());
            log.error("订单：{},产线：{} ,发送 空料框移出 请求参数 reqJson：{} , 异常：{}", orderCode, lineNo, reqJson, throwable.getMessage(), throwable);
            requestLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw throwable;
        } finally {
            requestLogService.save(requestLog);
            log.info("请求空料框移出 结束 : {} ", deviceType);
            log.info("------------------------------------------------------");
        }
    }
}
