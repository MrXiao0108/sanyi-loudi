package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWorkReportHistoryMapper;
import com.dzics.common.dao.DzWorkReportSortMapper;
import com.dzics.common.dao.MomOrderCompletedMapper;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.QrCode;
import com.dzics.common.model.constant.StartReportingStatus;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.custom.CallMaterial;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.mom.response.*;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.framework.OperLogReportWork;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.WorkReportService;
import com.dzics.sanymom.service.impl.http.WorkReportMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Classname WorkReportServiceOkImpl
 * @Description 描述
 * @Date 2022/5/20 17:37
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class WorkReportServiceOkImpl implements WorkReportService {
    @Autowired
    private MomOrderCompletedMapper completedMapper;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomWaitCallMaterialService momWaitCallMaterialService;
    @Autowired
    private MomUserService userService;
    @Value("${order.code}")
    private String orderCodeSys;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private WorkReportMomHttpImpl workReportMomHttp;
    @Autowired
    private MomProgressFeedbackLogService feedbackLogService;
    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;
    @Autowired
    private MomProgressFeedbackService feedbackService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private DzWorkReportSortMapper sortMapper;
    @Autowired
    private DzWorkReportHistoryMapper workReportHistoryMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MomOrderQrCodeService qrCodeService;

    @OperLogReportWork(operModul = "报工",operDesc = "报工")
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public MomOrderCompleted sendWorkReport(WorkReportDto workReportDto) {
        MomOrderCompleted dzst = null;
        String qrCode = workReportDto.getQrCode();
        Long lineId = workReportDto.getLineId();
        Long orderId = workReportDto.getOrderId();
        MomOrderQrCode orderQrCode = qrCodeService.getOne(new QueryWrapper<MomOrderQrCode>()
                .eq("order_no",workReportDto.getOrderNo())
                .eq("line_no",workReportDto.getLineNo())
                .eq("product_code",workReportDto.getQrCode()));
        if(orderQrCode==null){
            return dzst;
        }
        MonOrder monOrder = momOrderService.getById(orderQrCode.getProTaskOrderId());
        if("DZICS-Manual".equals(monOrder.getWiporderno())){
            return dzst;
        }
        List<MomOrderCompleted> completeds = completedMapper.selQrCodeAndLine(orderId, lineId, qrCode);
        if (CollectionUtils.isNotEmpty(completeds)) {
            DzWorkReportSort sort = sortMapper.getByOrderId(orderId, lineId);
            if (sort == null) {
                dzst = new MomOrderCompleted();
                dzst.setLogs("没有报工的订单,检查订单是否切换成功,是否有在生产中的订单");
                return dzst;
            }
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            String groupId = workReportDto.getGroupId();
            String orderNo = line.getOrderNo();
            String lineNo = line.getLineNo();
            boolean falg = true;
            boolean nextFalg = false;
            MomUser loginOk = userService.getLineIsLogin(orderNo, lineNo, orderCodeSys);
            String emo = "";
            if (loginOk != null) {
                emo = loginOk.getEmployeeNo();
            }
            MonOrder order = momOrderService.getById(sort.getProTaskOrderId());
            if(order==null){
                log.error("报工：数据出现异常，【dz_work_report_sort】表中存在报工记录，【mom_order】表中数据不存在");
                sortMapper.deleteById(sort.getId());
                log.info("报工：数据出现异常，【dz_work_report_sort】表中存在报工记录，【mom_order】表中数据不存在，自动删除【dz_work_report_sort】待报工记录");
                return sendWorkReport(workReportDto);
            }
            for (MomOrderCompleted qrC : completeds) {
                try {
                    String stationCode = qrC.getDzStationCode();
                    if (StringUtils.isEmpty(stationCode)) {
                        completedMapper.deleteById(qrC.getId());
                        continue;
                    }
                    String dzStationCodeSpare = qrC.getDzStationCodeSpare();
                    String proTaskId = qrC.getProTaskId();
                    if(StringUtil.isEmpty(stationCode) && StringUtil.isEmpty(dzStationCodeSpare)){
                        log.error("Mom订单号：{}，工位编号不存在 dzStationCodeSpare：{}, stationCode：{}",qrC.getWipOrderNo(),dzStationCodeSpare,stationCode);
                        dzst = new MomOrderCompleted();
                        dzst.setLogs("Mom订单号："+qrC.getWipOrderNo()+",工位编号不存在 dzStationCodeSpare："+dzStationCodeSpare+", stationCode："+stationCode);
                        return dzst;
                    }
                    List<CallMaterial> callMaterial = momWaitCallMaterialService.getWorkStation(proTaskId, stationCode);
                    if (CollectionUtils.isEmpty(callMaterial)) {
                        callMaterial = momWaitCallMaterialService.getWorkStation(proTaskId, dzStationCodeSpare);
                        if (CollectionUtils.isEmpty(callMaterial)) {
                            log.error("根据工位获取 工序 信息不存在：proTaskId: {},Mom订单号:{},stationCode: {},dzStationCodeSpare: {}", proTaskId,qrC.getWipOrderNo(),stationCode,dzStationCodeSpare);
                            dzst = new MomOrderCompleted();
                            dzst.setLogs("根据工位获取 工序 信息不存在：proTaskId: "+proTaskId+",Mom订单号:"+qrC.getWipOrderNo()+", stationCode："+stationCode+",dzStationCodeSpare: "+dzStationCodeSpare);
                            return dzst;
                        }
                    }
                    if(!order.getProductNo().equals(qrC.getProductNo())){
                        log.info("检测到报工异常，信号变更：4");
                        redisUtil.set(RedisKey.Work_Report_Status+orderNo+lineNo,"4");
                        sortMapper.deleteById(sort.getId());
                        log.warn("系统开始自动废除当前报工订单:{},当前进行中的报工订单产品物料号:{}",order.getWiporderno(),order.getProductNo());
                        log.warn("检测到当前产线:{},当前物料二维码产品物料号:{},当前进行中的报工订单:{},当前进行中的报工订单产品物料号:{},物料不一致，无法报工",orderNo,qrC.getProductNo(),order.getWiporderno(),order.getProductNo());
                        return sendWorkReport(workReportDto);
                    }
                    if (dzst == null) {
                        dzst = new MomOrderCompleted();
                        BeanUtils.copyProperties(qrC, dzst);
                    }
                    String oprSequenceNo = callMaterial.get(0).getOprSequenceNo();
                    String innerGroupId = redisUniqueID.getGroupId();
                    MomResultBg<List<GeneralControlModel>> momResultVo = postReportWork(groupId, innerGroupId, qrC, oprSequenceNo, stationCode, emo, orderNo, lineNo, sort.getWipOrderNo(),order.getProductNo());
                    MomProgressFeedbackLog requestLog = momResultVo.getRequestLog();
//                  保存请求日志
                    boolean save = feedbackLogService.save(requestLog);
                    String outInputType = qrC.getOutinputType();
                    String processFlowId = qrC.getProcessFlowId();
                    ResultVo resultVo = momResultVo.getResultVo();
                    if (MomReqContent.MOM_CODE_OK.equals(resultVo.getCode())) {
                        redisUtil.set(RedisKey.Work_Report_Status+orderNo+lineNo,"1");
//        报工完成后更新 工件制作流程记录 为已报工
                        falg = true;
                        completedMapper.deleteById(qrC.getId());
                        boolean type = dzWorkingFlowService.updateQrcodeOutInptType(outInputType, processFlowId, StartReportingStatus.OK);
                    } else if (MomReqContent.MOM_CODE_NEXT_REPORT.equals(resultVo.getCode())) {
                        log.info("报工失败，识别到切换订单code：{}", JSONObject.toJSONString(resultVo));
                        nextFalg = true;
                        break;
                    } else {
//          报工失败存储本次报工记录到 待报工记录表中 由后续任务触发继续报工
                        boolean type = dzWorkingFlowService.updateQrcodeOutInptType(outInputType, processFlowId, StartReportingStatus.ERR);
                        GeneralControlModel model = momResultVo.getRequestVo().getReported().get(0);
                        MomProgressFeedback feedback = getMomProgressFeedback(qrC, model);
                        boolean save1 = feedbackService.save(feedback);
                        log.error("报工失败，MOM返回结果：{}", JSONObject.toJSONString(resultVo));
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    log.error("报工失败：{}",throwable.getMessage(), throwable);
                }
            }
            if (nextFalg) {
                sortMapper.deleteById(sort.getId());
                return sendWorkReport(workReportDto);
            } else {
                if (falg) {
//                查询报工中的订单 ,增加报工数量
                    if (order != null) {
//                    更新报工订单数量
                        Integer okQ = order.getOkReportQuantity();
                        if (okQ == null) {
                            okQ = 0;
                        }
                        okQ = okQ + 1;
                        Integer quantity = order.getQuantity();
                        MonOrder upOrd = new MonOrder();
                        if (okQ.intValue() >= quantity.intValue()) {
                            upOrd.setReportStatus(true);
                            upOrd.setRealityCompleteDate(new Date());
                            sortMapper.deleteById(sort.getId());
                        }
                        upOrd.setProTaskOrderId(order.getProTaskOrderId());
                        upOrd.setOkReportQuantity(okQ);
                        momOrderService.updateById(upOrd);
//                    插入报工绑定订单记录
                        DzWorkReportHistory workReportHistory = new DzWorkReportHistory();
                        workReportHistory.setProTaskOrderId(sort.getProTaskOrderId());
                        workReportHistory.setWipOrderNo(sort.getWipOrderNo());
                        workReportHistory.setQrcode(qrCode);
                        workReportHistory.setOrderId(sort.getOrderId());
                        workReportHistory.setLineId(sort.getLineId());
                        workReportHistory.setOkNg("OK");
                        workReportHistoryMapper.insert(workReportHistory);
                        dzst.setWipOrderNoReportWork(sort.getWipOrderNo());
                    } else {
                        log.info("报工订单 proTaskOrderId:{}  无法查询到订单 order：{}", order.getProTaskOrderId(), order);
                    }
                }
            }
        } else {
            log.error("根据订单ID：{},产线ID：{},二维码：{} ,没有查询到报工信息", orderId, lineId, qrCode);
        }
        return dzst;
    }

    private MomProgressFeedback getMomProgressFeedback(MomOrderCompleted qrc, GeneralControlModel model) {
        MomProgressFeedback feedback = new MomProgressFeedback();
        feedback.setTaskType(MomTaskType.REPORT_WORK_OK);
        feedback.setReqid(model.getReqId());
        feedback.setProcessFlowId(qrc.getProcessFlowId());
        feedback.setReqsys(model.getReqSys());
        feedback.setFacility(model.getFacility());
        feedback.setWiporderno(model.getWipOrderNo());
        feedback.setSequenceno(model.getSequenceNo());
        feedback.setOprsequenceno(model.getOprSequenceNo());
        feedback.setEmployeeNo(model.getEmployeeNo());
        feedback.setWorkstation(model.getWorkStation());
        feedback.setActualstartdate(qrc.getStartTime());
        feedback.setActualcompletedate(qrc.getCompleteTime());
        feedback.setProgresstype(model.getProgressType());
        feedback.setProductno(model.getProductNo());
        feedback.setSerialno(model.getSerialNo());
        feedback.setQuantity(model.getQuantity());
        feedback.setNgquantity(model.getNGQuantity());
        feedback.setOrgCode("MOM");
        feedback.setCreateBy("MOM");
        return feedback;
    }

    private MomResultBg<List<GeneralControlModel>> postReportWork(String groupId, String innerGroupId, MomOrderCompleted qrC, String oprSequenceNo, String stationCode, String emo, String orderNo, String lineNo, String wipOrderNo, String productNo) {
        MomResultBg<List<GeneralControlModel>> result = new MomResultBg<>();
        try {
            Date startTime = qrC.getStartTime();
            Date completeTime = qrC.getCompleteTime();
            String startTimeStr = dateUtil.dateFormatToStingYmdHms(startTime);
            String completeTimeStr = dateUtil.dateFormatToStingYmdHms(completeTime);
            //头信息
            RequestHeaderVo<List<GeneralControlModel>> requestParms = new RequestHeaderVo<>();
            requestParms.setTaskId(redisUniqueID.getUUID());
            requestParms.setTaskType(MomTaskType.REPORT_WORK_OK);
            requestParms.setVersion(MomVersion.VERSION);
            //发送内容
            String qrCode = qrC.getQrCode();
            if (StringUtils.isNotEmpty(qrCode) && qrCode.contains("SANY-")) {
                qrCode = "";
            }
            GeneralControlModel gcm = new GeneralControlModel();
            gcm.setReqId(redisUniqueID.getkey());
            gcm.setReqSys(MomReqContent.REQ_SYS);
            gcm.setFacility(MomReqContent.FACILITY);
            gcm.setWipOrderNo(wipOrderNo);
            gcm.setSequenceNo(MomReqContent.SEQUENCENO);
            gcm.setOprSequenceNo(oprSequenceNo);
            gcm.setEmployeeNo(emo);
            gcm.setWorkStation(stationCode);
            gcm.setActualStartDate(startTimeStr);
            gcm.setActualCompleteDate(completeTimeStr);
            gcm.setProgressType(qrC.getOutinputType().equals(QrCode.QR_CODE_IN) ? QrCode.QR_CODE_IN_MOM : QrCode.QR_CODE_OUT_MOM);
            gcm.setProductNo(productNo);
            gcm.setSerialNo(qrCode);
            gcm.setQuantity(new BigDecimal("1.0"));
            gcm.setNGQuantity(new BigDecimal("0.0"));
            gcm.setKeyaccessoryList(new ArrayList<KeyAccessoryModel>());
            List<GeneralControlModel> list = new ArrayList<>();
            list.add(gcm);
            requestParms.setReported(list);
            result.setRequestVo(requestParms);
            Gson gson = new Gson();
            String reqJson = gson.toJson(requestParms);
            log.info("向总控发送报工请求 reqJson:{}", reqJson);
            try {
                MomProgressFeedbackLog getfeedbacklog = getfeedbacklog(qrC, gcm, stationCode);
                result.setRequestLog(getfeedbacklog);
                ResultVo body = workReportMomHttp.sendPost(innerGroupId, orderNo, lineNo, groupId, momRequestPath.ipPortPath, requestParms, ResultVo.class);
                if (body != null && body.getCode() != null && body.getCode().equals(MomReqContent.MOM_CODE_OK)) {
                    int statusCode = body.getStatusCode();
                    getfeedbacklog.setStatusCode(statusCode);
//            报工正常
                    getfeedbacklog.setResMomCode(true);
                } else {
//            报工错误
                    getfeedbacklog.setResMomCode(false);
                }
                result.setRequestLog(getfeedbacklog);
                result.setResultVo(body);
                log.info("收到总控响应信息 resp: {}", JSONObject.toJSONString(body));
            } catch (Throwable e) {
//               返回转存本次失败，保存数据待二次发送
                log.error("发送报工请求数据：{} ,错误信息：{}", reqJson, e.getMessage(), e);
            }
        } catch (Throwable throwable) {
//            报工数据封装错误：存储
            log.error("封装报工数据->：{} ,错误 error:{}", qrC, throwable.getMessage(), throwable);
        }
        return result;
    }

    private MomProgressFeedbackLog getfeedbacklog(MomOrderCompleted qrc, GeneralControlModel gmc, String stationCode) {
        MomProgressFeedbackLog feedbackLog = new MomProgressFeedbackLog();
        feedbackLog.setReqid(gmc.getReqId());
        feedbackLog.setProcessFlowId(qrc.getProcessFlowId());
        feedbackLog.setReqsys(gmc.getReqSys());
        feedbackLog.setFacility(gmc.getFacility());
        feedbackLog.setWiporderno(gmc.getWipOrderNo());
        feedbackLog.setSequenceno(gmc.getSequenceNo());
        feedbackLog.setOprsequenceno(gmc.getOprSequenceNo());
        feedbackLog.setWorkstation(stationCode);
        feedbackLog.setActualstartdate(qrc.getStartTime());
        feedbackLog.setActualcompletedate(qrc.getCompleteTime());
        feedbackLog.setProgresstype(gmc.getProgressType());
        feedbackLog.setProductno(gmc.getProductNo());
        feedbackLog.setSerialno(gmc.getSerialNo());
        feedbackLog.setQuantity(gmc.getQuantity());
        feedbackLog.setNgquantity(gmc.getNGQuantity());
        feedbackLog.setSendTime(new Date());
        feedbackLog.setOrgCode("");
        feedbackLog.setDelFlag(false);
        feedbackLog.setCreateBy("MOM");
        return feedbackLog;
    }

}
