package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWorkReportHistoryMapper;
import com.dzics.common.dao.DzWorkReportSortMapper;
import com.dzics.common.dao.MomOrderCompletedMapper;
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
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.WorkReportService;
import com.dzics.sanymom.service.impl.http.WorkReportMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Classname WorkReportServiceNgImpl
 * @Description 描述
 * @Date 2022/5/20 17:38
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class WorkReportServiceNgImpl implements WorkReportService {
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
                log.error("没有报工的订单,检查订单是否切换成功,是否有在生产中的订单");
                return dzst;
            }
            MonOrder order = momOrderService.getById(sort.getProTaskOrderId());
            //TODO 数据库未知原因出现异常脏数据，报工表中存在记录，Mom订单表数据却不存在，此处添加处理，防止异常出现
            if(order==null){
                log.error("报工：数据出现异常，【dz_work_report_sort】表中存在报工记录，【mom_order】表中数据不存在");
                sortMapper.deleteById(sort.getId());
                log.info("报工：数据出现异常，【dz_work_report_sort】表中存在报工记录，【mom_order】表中数据不存在，自动删除【dz_work_report_sort】待报工记录");
                return sendWorkReport(workReportDto);
            }
            //查询报工中的订单
            MonOrder monOrderSel = momOrderService.getById(sort.getProTaskOrderId());
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
            String groupId = workReportDto.getGroupId();
            String orderNo = line.getOrderNo();
            String lineNo = line.getLineNo();
            boolean falg = true;
            for (MomOrderCompleted qrC : completeds) {
                try {
                    String stationCode = qrC.getDzStationCode();
                    String proTaskId = qrC.getProTaskId();
                    List<CallMaterial> callMaterial = momWaitCallMaterialService.getWorkStation(proTaskId, stationCode);
                    if (CollectionUtils.isEmpty(callMaterial)) {
                        String dzStationCodeSpare = qrC.getDzStationCodeSpare();
                        if (StringUtils.isEmpty(dzStationCodeSpare)) {
                            log.error("Mom订单号：{}，工位编号不存在 dzStationCodeSpare：{}", qrC.getWipOrderNo(),dzStationCodeSpare);
                            dzst = new MomOrderCompleted();
                            dzst.setLogs("Mom订单号："+qrC.getWipOrderNo()+",工位编号不存在 dzStationCodeSpare："+dzStationCodeSpare+", stationCode："+stationCode);
                            return dzst;
//                            throw new RobRequestException(CustomResponseCode.ERR391);
                        }
                        stationCode = dzStationCodeSpare;
                        callMaterial = momWaitCallMaterialService.getWorkStation(proTaskId, dzStationCodeSpare);
                        if (CollectionUtils.isEmpty(callMaterial)) {
                            log.error("根据工位获取 工序 信息不存在：proTaskId: {},Mom订单号:{},stationCode: {}", proTaskId,qrC.getWipOrderNo(), dzStationCodeSpare);
//                            throw new RobRequestException(CustomResponseCode.ERR70);
                            dzst = new MomOrderCompleted();
                            dzst.setLogs("根据工位获取 工序 信息不存在：proTaskId："+proTaskId+",Mom订单号:"+qrC.getWipOrderNo()+", stationCode："+dzStationCodeSpare);
                            return dzst;
                        }
                        if(!monOrderSel.getProductNo().equals(qrC.getProductNo())){
                            log.info("检测到报工异常，信号变更：4");
                            redisUtil.set(RedisKey.Work_Report_Status+orderNo+lineNo,"4");
                            //废弃掉 当前数据库中的 待报工订单
                            log.warn("检测到当前产线:{},当前物料二维码产品物料号:{},当前进行中的报工订单:{},当前进行中的报工订单产品物料号:{},物料不一致，无法报工",orderNo,qrC.getProductNo(),order.getWiporderno(),order.getProductNo());
                            sortMapper.deleteById(sort.getId());
                            log.warn("系统开始自动废除当前报工订单:{},当前进行中的报工订单产品物料号:{}",order.getWiporderno(),order.getProductNo());
                            return sendWorkReport(workReportDto);
                        }
                    }
                    CallMaterial materialDef = momWaitCallMaterialService.getCallMaterial(lineNo, orderNo, callMaterial);
                    MomUser loginOk = userService.getLineIsLogin(orderNo, lineNo, orderCodeSys);
                    String emo = "";
                    if (loginOk != null) {
                        emo = loginOk.getEmployeeNo();
                    }
                    if (dzst == null) {
                        dzst = new MomOrderCompleted();
                        BeanUtils.copyProperties(qrC,dzst);
                    }
                    String oprSequenceNo = materialDef.getOprSequenceNo();
                    String innerGroupId = redisUniqueID.getGroupId();
                    MomResultBg<List<GeneralControlModel>> momResultVo = postReportWork(groupId, innerGroupId, qrC, oprSequenceNo, stationCode, emo, orderNo, lineNo,sort.getWipOrderNo());
//                  保存请求日志
                    MomProgressFeedbackLog requestLog = momResultVo.getRequestLog();
                    boolean save = feedbackLogService.save(requestLog);
                    String outInputType = qrC.getOutinputType();
                    String processFlowId = qrC.getProcessFlowId();
                    ResultVo resultVo = momResultVo.getResultVo();
                    if (resultVo != null && resultVo.getCode() != null && resultVo.getCode().equals(MomReqContent.MOM_CODE_OK)) {
                        redisUtil.set(RedisKey.Work_Report_Status+orderNo+lineNo,"1");
//        报工完成后更新 工件制作流程记录 为已报工
                        falg = true;
                        completedMapper.deleteById(qrC.getId());
                        boolean type = dzWorkingFlowService.updateQrcodeOutInptType(outInputType, processFlowId, StartReportingStatus.OK);
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
                    log.error("报工失败：{}", throwable.getMessage(), throwable);
                }
            }
            if (falg) {
                if (monOrderSel != null) {
//                    更新报工订单数量
                    Integer ng = monOrderSel.getNgReportQuantity();
                    if (ng == null) {
                        ng = 0;
                    }
                    MonOrder upOrd = new MonOrder();
                    upOrd.setProTaskOrderId(monOrderSel.getProTaskOrderId());
                    upOrd.setNgReportQuantity(ng + 1);
                    momOrderService.updateById(upOrd);

                    //                    插入报工绑定订单记录
                    DzWorkReportHistory workReportHistory = new DzWorkReportHistory();
                    workReportHistory.setProTaskOrderId(sort.getProTaskOrderId());
                    workReportHistory.setWipOrderNo(sort.getWipOrderNo());
                    workReportHistory.setQrcode(qrCode);
                    workReportHistory.setOrderId(sort.getOrderId());
                    workReportHistory.setLineId(sort.getLineId());
                    workReportHistory.setOkNg("NG");
                    workReportHistoryMapper.insert(workReportHistory);
                    dzst.setWipOrderNoReportWork(sort.getWipOrderNo());
                }
            }
        }
        return dzst;
    }


    private MomProgressFeedback getMomProgressFeedback(MomOrderCompleted qrc,GeneralControlModel model) {
        MomProgressFeedback feedback = new MomProgressFeedback();
        feedback.setTaskType(MomTaskType.REPORT_WORK_NG);
        feedback.setReqid(model.getReqId());
        feedback.setProcessFlowId(qrc.getProcessFlowId());
        feedback.setReqsys(model.getReqSys());
        feedback.setFacility(model.getFacility());
        feedback.setWiporderno(model.getWipOrderNo());
        feedback.setSequenceno(model.getSequenceNo());
        feedback.setOprsequenceno(model.getOprSequenceNo());
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

    private MomResultBg<List<GeneralControlModel>> postReportWork(String groupId, String innerGroupId, MomOrderCompleted qrC, String oprSequenceNo, String stationCode, String emo, String orderNo, String lineNo, String wipOrderNo) {
        MomResultBg<List<GeneralControlModel>> result = new MomResultBg<>();
        try {
            Date startTime = qrC.getStartTime();
            Date completeTime = qrC.getCompleteTime();
            String startTimeStr = dateUtil.dateFormatToStingYmdHms(startTime);
            String completeTimeStr = dateUtil.dateFormatToStingYmdHms(completeTime);
            //头信息
            RequestHeaderVo<List<GeneralControlModel>> requestHeaderVo = new RequestHeaderVo<>();
            requestHeaderVo.setTaskId(redisUniqueID.getUUID());
            requestHeaderVo.setTaskType(MomTaskType.REPORT_WORK_NG);
            requestHeaderVo.setVersion(MomVersion.VERSION);
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
            gcm.setProductNo(qrC.getProductNo());
            gcm.setSerialNo(qrCode);
            gcm.setQuantity(new BigDecimal("1.0"));
            gcm.setNGQuantity(new BigDecimal("0.0"));
            gcm.setKeyaccessoryList(new ArrayList<KeyAccessoryModel>());
            List<GeneralControlModel> list = new ArrayList<>();
            list.add(gcm);
            requestHeaderVo.setReported(list);
            result.setRequestVo(requestHeaderVo);
            Gson gson = new Gson();
            String reqJson = gson.toJson(requestHeaderVo);
            log.info("向总控发送报工请求 reqJson:{}", reqJson);
            try {
                MomProgressFeedbackLog getfeedbacklog = getfeedbacklog(qrC, gcm, stationCode);
                ResultVo body = workReportMomHttp.sendPost(innerGroupId, orderNo, lineNo, groupId, momRequestPath.ipPortPath, requestHeaderVo, ResultVo.class);
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
