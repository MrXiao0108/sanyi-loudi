package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWorkReportSortMapper;
import com.dzics.common.dao.MomMaterialWarehouseMapper;
import com.dzics.common.dao.MomOrderPathMaterialMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.service.MomWaitCallMaterialService;
import com.dzics.sanymom.exception.CustomMomException;
import com.dzics.sanymom.framework.OperLogReportWork;
import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.request.sany.IssueOrderInformation;
import com.dzics.sanymom.model.request.sany.OprSequenceList;
import com.dzics.sanymom.model.request.sany.Task;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.ProTaskOrderService;
import com.dzics.sanymom.service.SaveMomOrderService;
import com.dzics.sanymom.service.TaskMomOrderPathService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class SaveMomOrderServiceImpl implements SaveMomOrderService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${business.robot.ip}")
    private String busIpPort;
    @Autowired
    private ProTaskOrderService proTaskOrderService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private TaskMomOrderPathService orderPathService;
    @Autowired
    private MomOrderPathMaterialMapper pathMaterialMapper;
    @Autowired
    private MomMaterialWarehouseMapper warehouseMapper;
    @Autowired
    private MomWaitCallMaterialService momWaitCallMaterialService;
    @Autowired
    private DzWorkReportSortMapper sortMapper;


//    @OperLogReportWork(operModul = "接收订单",operDesc = "接收订单")
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public ResultDto saveMomOrderService(IssueOrderInformation orderInformation,String momParms) {
        ResultDto resultDto = new ResultDto();
        if (orderInformation == null) {
            resultDto.setVersion(orderInformation.getVersion());
            resultDto.setTaskId(orderInformation.getTaskId());
            resultDto.setCode("500");
            resultDto.setMsg("下发json数据错误");
        }
        log.info("收到总控发来MOM订单信息:{}", new Gson().toJson(orderInformation));
        Task task = orderInformation.getTask();
        String taskId = orderInformation.getTaskId();
        int version = orderInformation.getVersion();
        List<OprSequenceList> oprSequenceList = task.getOprSequenceList();
        if (CollectionUtils.isEmpty(oprSequenceList)) {
            log.warn("MOM下发订单的工序信息不存在");
            resultDto.setVersion(orderInformation.getVersion());
            resultDto.setTaskId(orderInformation.getTaskId());
            resultDto.setCode(String.valueOf(CustomExceptionType.Parameter_Exception.getCode()));
            resultDto.setMsg("MOM下发订单的工序信息不存在");
            return resultDto;
        }
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        if (line == null) {
            log.error("根据配置文件配置的订单号,未在查询到产线 line:{}", line);
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomExceptionType.TOKEN_PERRMITRE_ERROR.getTypeDesc(), version, taskId);
        }
        String wipOrderNo = task.getWipOrderNo();
        String taskType = orderInformation.getTaskType();
        MonOrder monOrder = momOrderService.getWipOrderNoOne(wipOrderNo, line.getOrderNo(), line.getLineNo());
        if (monOrder == null){
            if(!FinalCode.MOM_ORDER_STAT_110.equals(task.getProgressStatus())){
//                log.warn("当前订单：{}，在系统中未被创建，无法执行当前Mom指定下发订单状态：{}",task.getWipOrderNo(),task.getProgressStatus());
                resultDto.setVersion(orderInformation.getVersion());
                resultDto.setTaskId(orderInformation.getTaskId());
                resultDto.setCode("1");
                resultDto.setMsg("当前订单在系统中未创建，无法执行Mom当前下发的指定订单状态");
                return resultDto;
            }
//                插入订单
            MonOrder taskOrder = proTaskOrderService.saveTaskOrder(task, wipOrderNo, taskId, version, taskType, line,momParms);
            String proTaskOrderId = taskOrder.getProTaskOrderId();
//                保存工序
            String productNo = task.getProductNo();
            proTaskOrderService.saveOrderPath(proTaskOrderId, oprSequenceList, productNo);
//              跟新保存物料信息
            proTaskOrderService.saveTaskOrderMaterial(line, task, taskOrder, version, taskId);
            resultDto.setVersion(orderInformation.getVersion());
            resultDto.setTaskId(orderInformation.getTaskId());
            resultDto.setCode("0");
            resultDto.setMsg("接收成功");
            return resultDto;
        } else {
//                跟新
            String proId = monOrder.getProTaskOrderId();
            Long lineId = monOrder.getLineId();
            String progressStatus = monOrder.getProgressStatus();
            if (FinalCode.MOM_ORDER_STAT_150.equals(task.getProgressStatus()) && FinalCode.MOM_ORDER_STAT_120.equals(progressStatus)) {
                log.info("MOM 强制关闭订单 开始-------------: {}", JSONObject.toJSONString(orderInformation));
                PutMomOrder putMomOrder = new PutMomOrder();
                putMomOrder.setProTaskOrderId(proId);
                putMomOrder.setProgressStatus(task.getProgressStatus());
                putMomOrder.setLineId(String.valueOf(lineId));
                putMomOrder.setTransPondKey("dzicsdzics");
                String url = busIpPort + "/api/mom/order/transPond/force/close";
                try {
                    ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, putMomOrder, Result.class);
                    Result body = resultResponseEntity.getBody();
                    if (body.getCode() == 0) {
                        log.info("接到MOM请求，强制关闭订单成功，订单号：{}", task.getWipOrderNo());
                    }
                } catch (Exception e) {
                    log.error("调用后台系统:mom订单强制关闭转发异常,订单号:{}", task.getWipOrderNo());
                }
                log.info("MOM 强制关闭订单 结束-------------: {}", JSONObject.toJSONString(orderInformation));
            } else if (FinalCode.MOM_ORDER_STAT_110.equals(task.getProgressStatus())) {
                //进行中、已完工、暂停中 的订单不允许重置
                if(FinalCode.MOM_ORDER_STAT_120.equals(progressStatus) || FinalCode.MOM_ORDER_STAT_130.equals(progressStatus) || FinalCode.MOM_ORDER_STAT_160.equals(progressStatus)){
                    log.warn("该订单:{},状态不允许修改",task.getWipOrderNo());
                    resultDto.setVersion(orderInformation.getVersion());
                    resultDto.setTaskId(orderInformation.getTaskId());
                    resultDto.setCode("500");
                    resultDto.setMsg("该订单状态不允许修改");
                    return resultDto;
                }
                log.info("收到总控mom请求，开始重置订单相关信息------,订单号:{}", task.getWipOrderNo());
//                获取物料信息
                List<MomOrderPathMaterial> materialList = pathMaterialMapper.selMomOrderId(proId);
//                重置物料统计数据,扣除原来增加数量,重新增加
                for (MomOrderPathMaterial pathMaterial : materialList) {
                    String materialno = pathMaterial.getMaterialno();
                    MomMaterialWarehouse warehouse = warehouseMapper.getOrderAndMaterial(line.getOrderNo(), line.getLineNo(), materialno);
                    if (warehouse != null) {
                        warehouse.setQuantity(warehouse.getQuantity() - pathMaterial.getQuantity());
                        warehouse.setTotalDeduct(warehouse.getTotalDeduct() + pathMaterial.getQuantity());
                        warehouseMapper.updateById(warehouse);
                    }
                }
                //删除待叫料信息
                momWaitCallMaterialService.removeProTaskId(proId);
                //删除物料信息
                pathMaterialMapper.delMomOrderId(proId);
                //删除工序
                orderPathService.delByOrderId(proId);
                //删除待报工订单
                sortMapper.deleteByProTaskId(proId);
                //删除mom订单
                momOrderService.removeByIdProTask(proId);
//                   重写插入订单信息 开始 -----------------
                MonOrder taskOrder = proTaskOrderService.saveTaskOrder(task, wipOrderNo, taskId, version, taskType, line,momParms);
//                保存工序
                String productNo = task.getProductNo();
                proTaskOrderService.saveOrderPath(taskOrder.getProTaskOrderId(), oprSequenceList, productNo);
//              跟新保存物料信息
                proTaskOrderService.saveTaskOrderMaterial(line, task, taskOrder, version, taskId);
//                   重写插入订单信息 结束 -----------------
                log.info("重置订单相关信息完成-----,订单号:{}", task.getWipOrderNo());
                resultDto.setVersion(orderInformation.getVersion());
                resultDto.setTaskId(orderInformation.getTaskId());
                resultDto.setCode("0");
                resultDto.setMsg("接收成功");
                return resultDto;
            }else if(FinalCode.MOM_ORDER_STAT_140.equals(task.getProgressStatus()) && MomProgressStatus.DOWN.equals(progressStatus)){
                //Mom下发订单删除状态，针对于已下发未开始订单，针对进行中的订单可强制关闭150
                log.info("收到总控mom订单作废请求，订单号:{}", task.getWipOrderNo());
                monOrder.setProgressStatus(FinalCode.MOM_ORDER_STAT_140);
                boolean b = momOrderService.updateById(monOrder);
                if(b){
                    log.info("Mom下发删除订单状态，订单{}删除成功",monOrder.getWiporderno());
                    resultDto.setVersion(orderInformation.getVersion());
                    resultDto.setTaskId(orderInformation.getTaskId());
                    resultDto.setCode("0");
                    resultDto.setMsg("接收成功");
                    return resultDto;
                }
            }
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR86.getChinese(), version, taskId);
        }
    }


}
