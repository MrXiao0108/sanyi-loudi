package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.*;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.mom.response.*;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.model.base.MomResult;
import com.dzics.sanymom.model.common.MyReqMomType;
import com.dzics.sanymom.model.request.EmptyFrameMoves;
import com.dzics.sanymom.model.request.PutFeedingPoint;
import com.dzics.sanymom.model.request.agv.AgvParmsDto;
import com.dzics.sanymom.model.request.distribution.MaterialList;
import com.dzics.sanymom.model.request.searchframe.MaterialFrame;
import com.dzics.sanymom.model.response.searchframe.MaterialFrameRes;
import com.dzics.sanymom.service.MomHttpRequestService;
import com.dzics.sanymom.service.impl.http.SearchFrameMomHttpImpl;
import com.dzics.sanymom.service.impl.http.UpdateMaterialPointMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/6/11.
 * @since
 */
@Slf4j
@Service
public class MonHttpRequestServiceImpl implements MomHttpRequestService {
    @Autowired
    private SearchFrameMomHttpImpl searchFrameMomHttp;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomWaitCallMaterialReqLogService waitCallMaterialReqLogService;
    @Autowired
    private RedisUtil<String> redisUtil;
    @Autowired
    private UpdateMaterialPointMomHttpImpl pointMomHttp;


    /**
     * 待报工记录报工
     *
     * @param infoList
     * @return
     */
    @Override
    public List<String> reportWorkMom(List<MomProgressFeedback> infoList) {
        List<String> ids = new ArrayList<>();
        for (MomProgressFeedback feedback : infoList) {
            String processFlowId = feedback.getProcessFlowId();
            String progresstype = feedback.getProgresstype();
            String outInputType = QrCode.QR_CODE_IN_MOM.equals(progresstype) ? QrCode.QR_CODE_IN : QrCode.QR_CODE_OUT;
            try {
                log.debug("待报工记录重新报工 feedback : {}", feedback);
                boolean b = reportWorkFeedback(feedback);
                if (b) {
                    ids.add(feedback.getFeedbackId());
                } else {
                    log.error("待报工记录重新报工错误 feedback : {}", feedback);
                }
//                更新报工记录
                boolean type = dzWorkingFlowService.updateQrcodeOutInptType(outInputType, processFlowId, StartReportingStatus.OK);
            } catch (Throwable e) {
                boolean type = dzWorkingFlowService.updateQrcodeOutInptType(outInputType, processFlowId, StartReportingStatus.ERR);
                log.error("待报工记录向MOM报工失败：FeedbackId：{}, error: {}", feedback.getFeedbackId(), e.getMessage(), e);
            }
        }
        return ids;
    }


    @Override
    public boolean updatePointPallet(String innerGroupId, String groupId, String lineNo, String orderCode, String externalCode, String palletNo) {
        PutFeedingPoint putFeedingPoint = new PutFeedingPoint();
        putFeedingPoint.setReqSys(MomReqContent.REQ_SYS);
        putFeedingPoint.setFacility(MomReqContent.FACILITY);
        putFeedingPoint.setPalletNo(palletNo);
        putFeedingPoint.setSourceNo(externalCode);
        putFeedingPoint.setPointState("1");
        putFeedingPoint.setPalletState("0");
        putFeedingPoint.setPointFlag("0");
        RequestHeaderVo<PutFeedingPoint> headerVo = new RequestHeaderVo();
        headerVo.setTaskId(redisUniqueID.getUUID());
        headerVo.setTaskType(MomTaskType.UPDATE_FEEDING_POINT_INFORMATION);
        headerVo.setVersion(MomVersion.VERSION);
        headerVo.setReported(putFeedingPoint);
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        log.info("订单：{},产线：{},请求MOM更新 料点状态 | 料框状态 接口 地址：{} ,参数：{}", orderCode, lineNo, momRequestPath.ipPortPath, reqJson);
        ResultVo body = pointMomHttp.sendPost(innerGroupId, orderCode, lineNo, groupId, momRequestPath.ipPortPath, headerVo, ResultVo.class);
        log.info("订单：{},产线：{},请求MOM更新 料点状态 | 料框状态 接口 地址：{} ,响应信息：{}", orderCode, lineNo, momRequestPath.ipPortPath, gson.toJson(body));
        if (body == null || !MomReqContent.MOM_CODE_OK.equals(body.getCode())) {
            log.error("订单：{},产线：{} 调用MOM更新投料点信息异常 resBody:{}", orderCode, lineNo, body);
            throw new RobRequestException(CustomResponseCode.ERR75);
        }
        return true;
    }

    @Override
    public List<String> interProcessDistribution(List<MomDistributionWaitRequest> list) {
        List<String> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (MomDistributionWaitRequest request : list) {
                try {
                    EmptyFrameMoves emptyFrameMoves = getEmptyFrameMoves(request);
                    //头信息
                    RequestHeaderVo requestHeaderVo = new RequestHeaderVo();
                    requestHeaderVo.setTaskId(redisUniqueID.getUUID());
                    requestHeaderVo.setTaskType(MomTaskType.INTER_PROCESS_DISTRIBUTION);
                    requestHeaderVo.setVersion(MomVersion.VERSION);
                    requestHeaderVo.setReported(emptyFrameMoves);
                    Gson gson = new Gson();
                    String reqJson = gson.toJson(requestHeaderVo);
                    ResponseEntity<ResultVo> resultVoResponseEntity = restTemplate.postForEntity(momRequestPath.ipPortPath, reqJson, ResultVo.class);
                    ResultVo body = resultVoResponseEntity.getBody();
                    if (body != null && body.getCode() != null && body.getCode().equals(MomReqContent.MOM_CODE_OK)) {
//               请求正常
                        ids.add(request.getWorkpieceDistributionId());
                    }
                } catch (Throwable throwable) {
                    log.error("重新发送工序间配送错误：MomDistributionWaitRequest：{}", request, throwable);
                }

            }
        }
        return ids;
    }

    @Override
    public String getMyReqTypeId(String reqId) {
        String type = redisUtil.get(RedisKey.momHttpRequestService_getMyReqTypeId + reqId);
        if (type != null) {
            return type;
        }
        QueryWrapper<MomWaitCallMaterialReqLog> wp = new QueryWrapper<>();
        wp.eq("reqId", reqId);
        MomWaitCallMaterialReqLog one = waitCallMaterialReqLogService.getOne(wp);
        if (one != null) {
            return MyReqMomType.CALL_MATERIAL;
        }
        return MyReqMomType.DISTRIBUTION;
    }

    private EmptyFrameMoves getEmptyFrameMoves(MomDistributionWaitRequest request) {
        EmptyFrameMoves emptyFrameMoves = new EmptyFrameMoves();
        emptyFrameMoves.setReqId(request.getReqid());
        emptyFrameMoves.setReqSys(request.getReqsys());
        emptyFrameMoves.setFacility(request.getFacility());
        emptyFrameMoves.setReqType(request.getReqtype());
        emptyFrameMoves.setPalletType(request.getPallettype());
        emptyFrameMoves.setPalletNo(request.getPalletno());
        emptyFrameMoves.setSourceNo(request.getSourceno());
        emptyFrameMoves.setRequireTime(dateUtil.dateFormatToStingYmdHms(request.getRequiretime()));
        emptyFrameMoves.setSendTime(dateUtil.dateFormatToStingYmdHms(request.getSendtime()));
        emptyFrameMoves.setMaterialList(JSONObject.parseObject(request.getMateriallist(), new ArrayList<MaterialList>().getClass()));
        return emptyFrameMoves;
    }

    /**
     * 获取料框类型
     *
     * @param innerGroupId
     * @param groupId
     * @param orderNo
     * @param lineNo
     * @param sourceNo     料点编码
     * @param paramRsrv1   物料编号
     * @return
     */
    @Override
    public MaterialFrameRes getStringPalletType(String innerGroupId, String groupId, String orderNo, String lineNo, String sourceNo, String paramRsrv1) {
        RequestHeaderVo<MaterialFrame> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(MomTaskType.QUERY_MATERIAL);
        requestHeaderVo.setVersion(MomVersion.VERSION);
        MaterialFrame materialFrame = new MaterialFrame();
        materialFrame.setReqSys(MomReqContent.REQ_SYS);
        materialFrame.setFacility(MomReqContent.FACILITY);
        materialFrame.setPointNo(sourceNo);
        materialFrame.setParamRsrv1(paramRsrv1);
        requestHeaderVo.setReported(materialFrame);
        Gson gsonSearch = new Gson();
        String reqJson = gsonSearch.toJson(requestHeaderVo);
        log.info("请求MOM 查询料框参数接口 地址：{} ,参数：{}", momRequestPath.ipPortPath, reqJson);
        MomResult body = searchFrameMomHttp.sendPost(innerGroupId, orderNo, lineNo, groupId, momRequestPath.ipPortPath, requestHeaderVo, MomResult.class);
        log.info("请求MOM 查询料框参数接口 地址：{}  , 返回信息：{}", momRequestPath.ipPortPath, JSONObject.toJSONString(body));
        if (body == null) {
            throw new RobRequestException(CustomResponseCode.ERR60);
        }
        if (!MomReqContent.MOM_CODE_OK.equals(body.getCode())) {
            log.error("请求MOM查询料框接口错误 body: {}", body);
            throw new RobRequestException(body.getMsg());
        }
        MaterialFrameRes returnData = body.getReturnData();
        if (returnData == null) {
            log.error("返回结果集 returnData 为空 body :{}", body);
            throw new RobRequestException(CustomResponseCode.ERR61);
        }
        String palletType = returnData.getPalletType();
        if (StringUtils.isEmpty(palletType)) {
            log.error("查询料框接口：MOM返回料框类型为空：body ：{}", body);
            throw new RobRequestException(CustomResponseCode.ERR62);
        }
        return returnData;
    }


    @Override
    public MomDistributionWaitRequestLog getMomDistributionWaitRequestLog(AgvParmsDto parmsDto) {
        //            保存请求日志
        MomDistributionWaitRequestLog requestLog = new MomDistributionWaitRequestLog();
        requestLog.setReqid(parmsDto.getReqId());
        requestLog.setReqsys(parmsDto.getReqSys());
        requestLog.setReqtype(parmsDto.getReqType());
        requestLog.setPallettype(parmsDto.getPalletType());
        requestLog.setPalletno(parmsDto.getPalletNo());
        requestLog.setSourceno(parmsDto.getSourceNo());
        requestLog.setRequiretime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getRequireTime()));
        requestLog.setSendtime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getSendTime()));
        requestLog.setOrgCode("MOM");
        requestLog.setDelFlag(false);
        return requestLog;
    }

    @Override
    public MomDistributionWaitRequest getMomDistributionWaitRequest(AgvParmsDto parmsDto, String code, String orderCode, String lineNo, String basketType, String pointModel, String taskType) {
        MomDistributionWaitRequest request = new MomDistributionWaitRequest();
        request.setReqid(parmsDto.getReqId());
        request.setOrderNo(orderCode);
        request.setTaskType(taskType);
        request.setLineNo(lineNo);
        request.setBasketType(basketType);
        request.setProductNo(parmsDto.getParamRsrv1());
        request.setIsUpMach(pointModel);
        request.setReqsys(parmsDto.getReqSys());
        request.setReqtype(parmsDto.getReqType());
        request.setPallettype(parmsDto.getPalletType());
        request.setPalletno(parmsDto.getPalletNo());
        request.setSourceno(parmsDto.getSourceNo());
        request.setRequiretime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getRequireTime()));
        request.setSendtime(dateUtil.stringDateToformatDateYmdHmsMom(parmsDto.getSendTime()));
        request.setOrgCode("MOM");
        request.setDelFlag(false);
        request.setReqStatus(code);
        request.setCreateBy("MOM");
        return request;
    }

    private boolean reportWorkFeedback(MomProgressFeedback feedback) {
        //头信息
        RequestHeaderVo<List<GeneralControlModel>> requestHeaderVo = new RequestHeaderVo<>();
        requestHeaderVo.setTaskId(redisUniqueID.getUUID());
        requestHeaderVo.setTaskType(feedback.getTaskType());
        requestHeaderVo.setVersion(MomVersion.VERSION);
        //发送内容
        GeneralControlModel gcm = new GeneralControlModel();
        gcm.setReqId(feedback.getReqid());
        gcm.setReqSys(feedback.getReqsys());
        gcm.setFacility(feedback.getFacility());
        gcm.setWipOrderNo(feedback.getWiporderno());
        gcm.setSequenceNo(feedback.getSequenceno());
        gcm.setOprSequenceNo(feedback.getOprsequenceno());
        gcm.setEmployeeNo(feedback.getEmployeeNo());
        gcm.setWorkStation(feedback.getWorkstation());
        gcm.setActualStartDate(dateUtil.dateFormatToStingYmdHms(feedback.getActualcompletedate()));
        gcm.setActualCompleteDate(dateUtil.dateFormatToStingYmdHms(feedback.getActualcompletedate()));
        gcm.setProgressType(feedback.getProgresstype());
        gcm.setProductNo(feedback.getProductno());
        gcm.setSerialNo(feedback.getSerialno());
        gcm.setQuantity(feedback.getQuantity());
        gcm.setNGQuantity(feedback.getNgquantity());
        gcm.setKeyaccessoryList(new ArrayList<KeyAccessoryModel>());
        List<GeneralControlModel> list = new ArrayList<>();
        list.add(gcm);
        requestHeaderVo.setReported(list);
        Gson gson = new Gson();
        String reqJson = gson.toJson(requestHeaderVo);
        log.debug("向总控发送报工请求 reqJson:{}", reqJson);
        try {
            ResponseEntity<ResultVo> resultVoResponseEntity = restTemplate.postForEntity(momRequestPath.ipPortPath, reqJson, ResultVo.class);
            ResultVo body = resultVoResponseEntity.getBody();
            if (body != null && body.getCode() != null && body.getCode().equals(MomReqContent.MOM_CODE_OK)) {
//            报工正常
                return true;
            }
            log.debug("收到总控响应信息 resp: {}", resultVoResponseEntity);
        } catch (Throwable e) {
//               返回转存本次失败，保存数据待二次发送
            log.error("发送报工请求数据：{} ,错误信息：{}", reqJson, e.getMessage(), e);
        }
        return false;
    }

}
