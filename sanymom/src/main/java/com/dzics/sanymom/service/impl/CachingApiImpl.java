package com.dzics.sanymom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.service.DzProductionLineService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.service.SysDictItemService;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.common.RobotReturnType;
import com.dzics.sanymom.model.request.sany.IssueOrderInformation;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.SaveMomOrderService;
import com.dzics.sanymom.util.RedisUtil;
import com.google.gson.Gson;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class CachingApiImpl implements CachingApi {

    @Value("${order.code}")
    private String orderCode;
    @Value("${business.robot.ip}")
    private String busIpPort;
    @Value("${mom.run.model.key}")
    private String momRunModelKey;
    @Autowired
    SysDictItemService sysDictItemService;
    @Autowired
    DzProductionLineService dzProductionLineService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SaveMomOrderService saveMomOrderService;
    @Autowired
    private RedisUtil redisUtil;



    @Override
    public DzProductionLine getOrderIdAndLineId() {
        try {

            List<DzProductionLine> data = dzProductionLineService.list(new QueryWrapper<DzProductionLine>().eq("order_no", orderCode).orderByDesc("create_time"));
            if (data.size() == 0) {
                return null;
            }
            DzProductionLine obj = data.get(0);
            return obj;
        } catch (Exception e) {
            log.error("?????????????????????ID?????????ID??????:{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DzProductionLine updateOrderIdAndLineId() {
        try {
            List<DzProductionLine> data = dzProductionLineService.list(new QueryWrapper<DzProductionLine>().eq("order_no", orderCode).orderByDesc("create_time"));
            if (data.size() == 0) {
                return null;
            }
            DzProductionLine obj = data.get(0);
            return obj;
        } catch (Exception e) {
            log.error("?????????????????????ID?????????ID??????:{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getMomRunModel() {
        String model = sysDictItemService.getMomRunModel(momRunModelKey, orderCode);
        return model;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String updateAgvRunModel(Integer rm,DzProductionLine line) {
        String s = sysDictItemService.updateAgvRunModel(momRunModelKey, orderCode, rm);
        //??????????????????????????????????????????????????????1?????????  0?????????
        if(StringUtil.isNotEmpty(s)){
            if("manual".equals(s)){
                //????????????????????????????????????
                redisUtil.set(RedisKey.Work_Report_Status+line.getOrderNo()+line.getLineNo(),"1");
            }
        }
        return s;
    }

    @Override
    public String updateModelWhere(Integer rm,DzProductionLine line) {
        String model = rm == 1 ? "auto" : "manual";
        try {
            if(!StringUtil.isEmpty(model)){
                MonOrder monOrder = new MonOrder();
                //??????????????????
                if("manual".equals(model)){
                    //?????????????????????????????????
                    QueryWrapper<MonOrder> wp = new QueryWrapper<>();
                    wp.eq("line_id", line.getId());
                    wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING)
                            .or().eq("ProgressStatus", MomProgressStatus.STOP)
                            .or().eq("order_operation_result", 1));
                    List<MonOrder> list = momOrderService.list(wp);
                    if (CollectionUtils.isNotEmpty(list)) {
                        return "ExitOrder";
                    }
                    monOrder = momOrderService.getOne(new QueryWrapper<MonOrder>().eq("order_id", line.getOrderId()).eq("line_id", line.getId()).eq("WipOrderNo", "DZICS-Manual"));
                    //??????????????????????????????????????????????????????
                    if(monOrder==null){
                        Gson gson = new Gson();
                        String s = generateOrder(line);
                        IssueOrderInformation requestHeaderVo = gson.fromJson(s, IssueOrderInformation.class);
                        ResultDto resultDto = saveMomOrderService.saveMomOrderService(requestHeaderVo,s);
                        if("0".equals(resultDto.getCode())){
                            log.info("CachingApiImpl [updateModelWhere] ???????????????????????????????????????");
                        }
                        monOrder = momOrderService.getOne(new QueryWrapper<MonOrder>().eq("order_id", line.getOrderId()).eq("WipOrderNo", "DZICS-Manual"));
                        monOrder.setProgressStatus(MomProgressStatus.LOADING);
                        monOrder.setOrderOperationResult(2);
                    }else{
                        monOrder.setProgressStatus(String.valueOf(120));
                        monOrder.setOrderOperationResult(2);
                    }
                    boolean b = momOrderService.updateById(monOrder);
                    if(b){
                        log.info("CachingApiImpl [updateModelWhere ] ??????????????????????????????????????????");
                    }
                }else {
                    //???????????????????????????????????????
                    monOrder = momOrderService.getOne(new QueryWrapper<MonOrder>().eq("order_id", line.getOrderId()).eq("line_id", line.getId()).eq("WipOrderNo", "DZICS-Manual"));
                    if(monOrder!=null){
                        monOrder.setProgressStatus(String.valueOf(110));
                        monOrder.setOrderOperationResult(2);
                        monOrder.setQuantity(1000);
                        boolean b = momOrderService.updateById(monOrder);
                        if(b){
                            log.info("CachingApiImpl [updateModelWhere ] ??????????????????????????????????????????");
                        }

                    }
                }
            }
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }
        return "ok";
    }


    public String generateOrder(DzProductionLine line){
        String order = "";
        //???????????????
        if("1".equals(line.getId().toString()) || "2".equals(line.getId().toString()) || "3".equals(line.getId().toString()) || "4".equals(line.getId().toString()) || "5".equals(line.getId().toString()) || "6".equals(line.getId().toString()) || "7".equals(line.getId().toString())){
            order = "{\n" +
                    "\t\"version\": 1,\n" +
                    "\t\"taskId\": \"\",\n" +
                    "\t\"taskType\": \"51\",\n" +
                    "\t\"SysCode\": \"DZ2MHSGJJGD\",\n" +
                    "\t\"task\": {\n" +
                    "\t\t\"WipOrderNo\": \"DZICS-Manual\",\n" +
                    "\t\t\"WipOrderType\": \"1\",\n" +
                    "\t\t\"Facility\": \"1820\",\n" +
                    "\t\t\"ProductionLine\": \"1820WJ13\",\n" +
                    "\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\"WipOrderGroup\": \"32\",\n" +
                    "\t\t\"GroupCount\": \"01\",\n" +
                    "\t\t\"ProductNo\": \"SYG004975099\",\n" +
                    "\t\t\"ProductName\": \"?????????ZX55.3.1B.1\",\n" +
                    "\t\t\"ProductAlias\": \"W61B\",\n" +
                    "\t\t\"SerialNo\": [],\n" +
                    "\t\t\"Quantity\": 1000.0,\n" +
                    "\t\t\"ScheduledStartDate\": \"2021-06-11 10:01:37\",\n" +
                    "\t\t\"ScheduledCompleteDate\": \"2021-06-14 11:21:57\",\n" +
                    "\t\t\"ProgressStatus\": \"110\",\n" +
                    "\t\t\"SerialNumber\": \"\",\n" +
                    "\t\t\"SteelCode\": \"\",\n" +
                    "\t\t\"MRPController\": \"606\",\n" +
                    "\t\t\"WipOrderBatchNo\": \"00000000000000\",\n" +
                    "\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\"OprSequenceList\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0010\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:01:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0010\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0003\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-2\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0004\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098H\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"???????????????ZX55.3.1B.1.2HZX55.3.1B.1.2H\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0020\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-29 09:30:15\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-29 09:55:15\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0030\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512003\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0020\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2???????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0040\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512004\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2??????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0001\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"11698243\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1A.1-3\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61A\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0002\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975093\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-1\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0050\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512005\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0040\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0060\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 13:51:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"543001\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ09A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0070\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:03:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512023\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ10A0071\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2???/3??????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0080\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512006\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0063\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0090\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512007\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0073\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0100\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512008\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0083\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0110\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512009\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0090\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0120\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512010\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0100\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0130\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-14 11:21:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512011\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0110\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t}\n" +
                    "\t\t]\n" +
                    "\t}\n" +
                    "}";
        }
        //???????????????
        if("8".equals(line.getId().toString()) ||"9".equals(line.getId().toString()) ||"10".equals(line.getId().toString())){
            order = "{\n" +
                    "  \"version\": 1,\n" +
                    "  \"taskId\": \"\",\n" +
                    "  \"taskType\": \"51\",\n" +
                    "  \"SysCode\": \"DZ3MHSGJJGD\",\n" +
                    "  \"task\": {\n" +
                    "    \"WipOrderNo\": \"DZICS-Manual\",\n" +
                    "    \"WipOrderType\": \"1\",\n" +
                    "    \"Facility\": \"1820\",\n" +
                    "    \"ProductionLine\": \"1820WJ10\",\n" +
                    "    \"WorkCenter\": \"512015\",\n" +
                    "    \"WipOrderGroup\": \"32\",\n" +
                    "    \"GroupCount\": \"01\",\n" +
                    "    \"ProductNo\": \"11350289\",\n" +
                    "    \"ProductName\": \"?????????ZX200.3.3B.1\",\n" +
                    "    \"ProductAlias\": \"W203B\",\n" +
                    "    \"SerialNo\": [],\n" +
                    "    \"Quantity\": 1000.0,\n" +
                    "    \"ScheduledStartDate\": \"2022-07-24 07:17:36\",\n" +
                    "    \"ScheduledCompleteDate\": \"2022-08-03 01:24:38\",\n" +
                    "    \"ProgressStatus\": \"110\",\n" +
                    "    \"SerialNumber\": \"\",\n" +
                    "    \"SteelCode\": \"\",\n" +
                    "    \"MRPController\": \"606\",\n" +
                    "    \"WipOrderBatchNo\": \"00000000000000\",\n" +
                    "    \"paramRsrv1\": \"\",\n" +
                    "    \"paramRsrv2\": \"\",\n" +
                    "    \"paramRsrv3\": \"\",\n" +
                    "    \"paramRsrv4\": \"\",\n" +
                    "    \"paramRsrv5\": \"\",\n" +
                    "    \"OprSequenceList\": [\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0010\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"??????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-24 07:17:36\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-24 08:06:48\",\n" +
                    "        \"WorkCenter\": \"512015\",\n" +
                    "        \"WorkCenterName\": \"??????2.5?????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0020\",\n" +
                    "        \"WorkStationName\": \"????????????3?????????????????????????????????\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"1599271258\",\n" +
                    "            \"ReserveLineNo\": \"0004\",\n" +
                    "            \"MaterialNo\": \"11350285L\",\n" +
                    "            \"MaterialName\": \"????????????ZX200.3.3B.1-2(L)ZX200.3.3B.1-2L\",\n" +
                    "            \"MaterialAlias\": \"NHD203\",\n" +
                    "            \"Quantity\": 4.0,\n" +
                    "            \"Unit\": \"PC\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"1599271258\",\n" +
                    "            \"ReserveLineNo\": \"0003\",\n" +
                    "            \"MaterialNo\": \"11350285\",\n" +
                    "            \"MaterialName\": \"??????ZX200.3.3B.1-2\",\n" +
                    "            \"MaterialAlias\": \"NHD203\",\n" +
                    "            \"Quantity\": 4.0,\n" +
                    "            \"Unit\": \"PC\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0020\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"??????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-24 08:06:48\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-24 08:06:52\",\n" +
                    "        \"WorkCenter\": \"541002\",\n" +
                    "        \"WorkCenterName\": \"??????2???/3?????????????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0011\",\n" +
                    "        \"WorkStationName\": \"??????3?????????????????????????????????\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0030\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"??????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-24 08:06:52\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-24 08:06:56\",\n" +
                    "        \"WorkCenter\": \"512016\",\n" +
                    "        \"WorkCenterName\": \"??????2.5?????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0030\",\n" +
                    "        \"WorkStationName\": \"????????????3???????????????????????????\\n\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0040\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"?????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-24 08:06:56\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-24 08:07:00\",\n" +
                    "        \"WorkCenter\": \"512017\",\n" +
                    "        \"WorkCenterName\": \"??????2.5????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0040\",\n" +
                    "        \"WorkStationName\": \"????????????3??????????????????????????????\\n\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"1599271258\",\n" +
                    "            \"ReserveLineNo\": \"0002\",\n" +
                    "            \"MaterialNo\": \"11258271\",\n" +
                    "            \"MaterialName\": \"?????????ZX65.3.2A.1-3\",\n" +
                    "            \"MaterialAlias\": \"W373\",\n" +
                    "            \"Quantity\": 4.0,\n" +
                    "            \"Unit\": \"PC\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"1599271258\",\n" +
                    "            \"ReserveLineNo\": \"0001\",\n" +
                    "            \"MaterialNo\": \"10356940\",\n" +
                    "            \"MaterialName\": \"??????ZX200.3.3.1-1A\",\n" +
                    "            \"MaterialAlias\": \"W203B\",\n" +
                    "            \"Quantity\": 4.0,\n" +
                    "            \"Unit\": \"PC\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0050\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"????????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-24 08:07:00\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-24 08:07:04\",\n" +
                    "        \"WorkCenter\": \"512018\",\n" +
                    "        \"WorkCenterName\": \"??????2.5???????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0050\",\n" +
                    "        \"WorkStationName\": \"????????????3?????????????????????????????????\\n\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0060\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"?????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-25 10:26:30\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-25 10:26:30\",\n" +
                    "        \"WorkCenter\": \"543001\",\n" +
                    "        \"WorkCenterName\": \"????????????????????????\",\n" +
                    "        \"WorkStation\": \"\",\n" +
                    "        \"WorkStationName\": \"\",\n" +
                    "        \"ProgressStatus\": 110.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0070\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"??????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-24 20:00:00\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-24 20:08:00\",\n" +
                    "        \"WorkCenter\": \"541004\",\n" +
                    "        \"WorkCenterName\": \"??????2???/3??????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0070\",\n" +
                    "        \"WorkStationName\": \"??????2???/3??????????????????\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0080\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"?????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-29 20:13:14\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-29 21:49:14\",\n" +
                    "        \"WorkCenter\": \"512019\",\n" +
                    "        \"WorkCenterName\": \"??????2.5????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0081\",\n" +
                    "        \"WorkStationName\": \"????????????3????????????????????????1?????????\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0090\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"?????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-29 21:49:14\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-29 21:49:18\",\n" +
                    "        \"WorkCenter\": \"512020\",\n" +
                    "        \"WorkCenterName\": \"??????2.5????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0090\",\n" +
                    "        \"WorkStationName\": \"????????????3????????????????????????1?????????\\n\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0100\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"????????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-29 21:49:18\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-29 21:49:22\",\n" +
                    "        \"WorkCenter\": \"512021\",\n" +
                    "        \"WorkCenterName\": \"??????2.5???????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0100\",\n" +
                    "        \"WorkStationName\": \"????????????3????????????????????????1????????????\\n\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0110\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"????????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-29 21:49:21\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-29 22:26:33\",\n" +
                    "        \"WorkCenter\": \"512022\",\n" +
                    "        \"WorkCenterName\": \"??????2.5???????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ10A0110\",\n" +
                    "        \"WorkStationName\": \"????????????3????????????????????????1\\n\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0120\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"??????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-07-29 21:58:43\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-07-29 22:42:43\",\n" +
                    "        \"WorkCenter\": \"512010\",\n" +
                    "        \"WorkCenterName\": \"??????1.5/2.5?????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ13A0100\",\n" +
                    "        \"WorkStationName\": \"??????2?????????\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"OprSequenceNo\": \"0130\",\n" +
                    "        \"SequenceNo\": \"000000\",\n" +
                    "        \"OprSequenceName\": \"????????????\",\n" +
                    "        \"OprSequenceType\": \"??????\",\n" +
                    "        \"ScheduledStartDate\": \"2022-08-01 00:50:14\",\n" +
                    "        \"ScheduledCompleteDate\": \"2022-08-03 01:24:38\",\n" +
                    "        \"WorkCenter\": \"512024\",\n" +
                    "        \"WorkCenterName\": \"??????2.5???????????????????????????\",\n" +
                    "        \"WorkStation\": \"1820WJ02A0110\",\n" +
                    "        \"WorkStationName\": \"??????2.5???????????????????????????1???\",\n" +
                    "        \"ProgressStatus\": 120.0,\n" +
                    "        \"Quantity\": 4.0,\n" +
                    "        \"paramRsrv1\": \"\",\n" +
                    "        \"paramRsrv2\": \"\",\n" +
                    "        \"paramRsrv3\": \"\",\n" +
                    "        \"paramRsrv4\": \"\",\n" +
                    "        \"paramRsrv5\": \"\",\n" +
                    "        \"ComponentList\": [\n" +
                    "          {\n" +
                    "            \"ReserveNo\": \"\",\n" +
                    "            \"ReserveLineNo\": \"\",\n" +
                    "            \"MaterialNo\": \"\",\n" +
                    "            \"MaterialName\": \"\",\n" +
                    "            \"MaterialAlias\": \"\",\n" +
                    "            \"Quantity\": 0.0,\n" +
                    "            \"Unit\": \"\",\n" +
                    "            \"paramRsrv1\": \"\",\n" +
                    "            \"paramRsrv2\": \"\",\n" +
                    "            \"paramRsrv3\": \"\",\n" +
                    "            \"paramRsrv4\": \"\",\n" +
                    "            \"paramRsrv5\": \"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";
        }
        //????????????
        if("11".equals(line.getId().toString()) || "12".equals(line.getId().toString()) || "13".equals(line.getId().toString())){
            order = "{\n" +
                    "\t\"version\": 1,\n" +
                    "\t\"taskId\": \"\",\n" +
                    "\t\"taskType\": \"51\",\n" +
                    "\t\"SysCode\": \"DZ2MGTJJGD\",\n" +
                    "\t\"task\": {\n" +
                    "\t\t\"WipOrderNo\": \"DZICS-Manual\",\n" +
                    "\t\t\"WipOrderType\": \"1\",\n" +
                    "\t\t\"Facility\": \"1820\",\n" +
                    "\t\t\"ProductionLine\": \"1820WJ13\",\n" +
                    "\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\"WipOrderGroup\": \"32\",\n" +
                    "\t\t\"GroupCount\": \"01\",\n" +
                    "\t\t\"ProductNo\": \"SYG004975099\",\n" +
                    "\t\t\"ProductName\": \"?????????ZX55.3.1B.1\",\n" +
                    "\t\t\"ProductAlias\": \"W61B\",\n" +
                    "\t\t\"SerialNo\": [],\n" +
                    "\t\t\"Quantity\": 1000.0,\n" +
                    "\t\t\"ScheduledStartDate\": \"2021-06-11 10:01:37\",\n" +
                    "\t\t\"ScheduledCompleteDate\": \"2021-06-14 11:21:57\",\n" +
                    "\t\t\"ProgressStatus\": \"110\",\n" +
                    "\t\t\"SerialNumber\": \"\",\n" +
                    "\t\t\"SteelCode\": \"\",\n" +
                    "\t\t\"MRPController\": \"606\",\n" +
                    "\t\t\"WipOrderBatchNo\": \"00000000000000\",\n" +
                    "\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\"OprSequenceList\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0010\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:01:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0010\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0003\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-2\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0004\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098H\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"???????????????ZX55.3.1B.1.2HZX55.3.1B.1.2H\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0020\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-29 09:30:15\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-29 09:55:15\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0030\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512003\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0020\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2???????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0040\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512004\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2??????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0001\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"11698243\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1A.1-3\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61A\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0002\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975093\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-1\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0050\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512005\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0040\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0060\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 13:51:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"543001\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ09A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0070\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:03:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512023\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ10A0071\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2???/3??????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0080\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512006\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0063\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0090\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512007\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0073\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0100\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512008\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0083\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0110\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512009\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0090\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0120\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512010\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0100\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0130\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-14 11:21:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512011\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0110\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t}\n" +
                    "\t\t]\n" +
                    "\t}\n" +
                    "}";
        }
        //????????????
        if("14".equals(line.getId().toString()) || "15".equals(line.getId().toString())){
            order = "{\n" +
                    "\t\"version\": 1,\n" +
                    "\t\"taskId\": \"\",\n" +
                    "\t\"taskType\": \"51\",\n" +
                    "\t\"SysCode\": \"DZ3MGTJJGD\",\n" +
                    "\t\"task\": {\n" +
                    "\t\t\"WipOrderNo\": \"DZICS-Manual\",\n" +
                    "\t\t\"WipOrderType\": \"1\",\n" +
                    "\t\t\"Facility\": \"1820\",\n" +
                    "\t\t\"ProductionLine\": \"1820WJ13\",\n" +
                    "\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\"WipOrderGroup\": \"32\",\n" +
                    "\t\t\"GroupCount\": \"01\",\n" +
                    "\t\t\"ProductNo\": \"SYG004975099\",\n" +
                    "\t\t\"ProductName\": \"?????????ZX55.3.1B.1\",\n" +
                    "\t\t\"ProductAlias\": \"W201B\",\n" +
                    "\t\t\"SerialNo\": [],\n" +
                    "\t\t\"Quantity\": 1000.0,\n" +
                    "\t\t\"ScheduledStartDate\": \"2021-06-11 10:01:37\",\n" +
                    "\t\t\"ScheduledCompleteDate\": \"2021-06-14 11:21:57\",\n" +
                    "\t\t\"ProgressStatus\": \"110\",\n" +
                    "\t\t\"SerialNumber\": \"\",\n" +
                    "\t\t\"SteelCode\": \"\",\n" +
                    "\t\t\"MRPController\": \"606\",\n" +
                    "\t\t\"WipOrderBatchNo\": \"00000000000000\",\n" +
                    "\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\"OprSequenceList\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0010\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:01:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0010\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0003\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-2\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0004\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098H\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"???????????????ZX55.3.1B.1.2HZX55.3.1B.1.2H\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0020\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-29 09:30:15\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-29 09:55:15\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0030\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512003\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0020\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2???????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0040\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512004\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2??????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0001\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"11698243\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1A.1-3\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61A\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0002\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975093\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-1\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0050\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512005\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0040\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0060\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 13:51:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"543001\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ09A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0070\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:03:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512023\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ10A0071\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2???/3??????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0080\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512006\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0063\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0090\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512007\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0073\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0100\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512008\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0083\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0110\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512009\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0090\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0120\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512010\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0100\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0130\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-14 11:21:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512011\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0110\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t}\n" +
                    "\t\t]\n" +
                    "\t}\n" +
                    "}";
        }
        //???????????????
        if("16".equals(line.getId().toString())){
            order = "{\n" +
                    "\t\"version\": 1,\n" +
                    "\t\"taskId\": \"\",\n" +
                    "\t\"taskType\": \"51\",\n" +
                    "\t\"SysCode\": \"DZ2MHSGCJGD\",\n" +
                    "\t\"task\": {\n" +
                    "\t\t\"WipOrderNo\": \"DZICS-Manual\",\n" +
                    "\t\t\"WipOrderType\": \"1\",\n" +
                    "\t\t\"Facility\": \"1820\",\n" +
                    "\t\t\"ProductionLine\": \"1820WJ13\",\n" +
                    "\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\"WipOrderGroup\": \"32\",\n" +
                    "\t\t\"GroupCount\": \"01\",\n" +
                    "\t\t\"ProductNo\": \"SYG004975099\",\n" +
                    "\t\t\"ProductName\": \"?????????ZX55.3.1B.1\",\n" +
                    "\t\t\"ProductAlias\": \"W61B\",\n" +
                    "\t\t\"SerialNo\": [],\n" +
                    "\t\t\"Quantity\": 1000.0,\n" +
                    "\t\t\"ScheduledStartDate\": \"2021-06-11 10:01:37\",\n" +
                    "\t\t\"ScheduledCompleteDate\": \"2021-06-14 11:21:57\",\n" +
                    "\t\t\"ProgressStatus\": \"110\",\n" +
                    "\t\t\"SerialNumber\": \"\",\n" +
                    "\t\t\"SteelCode\": \"\",\n" +
                    "\t\t\"MRPController\": \"606\",\n" +
                    "\t\t\"WipOrderBatchNo\": \"00000000000000\",\n" +
                    "\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\"OprSequenceList\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0010\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:01:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0010\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0003\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-2\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0004\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098H\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"???????????????ZX55.3.1B.1.2HZX55.3.1B.1.2H\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0020\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-29 09:30:15\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-29 09:55:15\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0030\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512003\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0020\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2???????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0040\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512004\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2??????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0001\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"11698243\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1A.1-3\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61A\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0002\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975093\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-1\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0050\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512005\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0040\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0060\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 13:51:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"543001\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ09A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0070\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:03:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512023\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ10A0071\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2???/3??????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0080\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512006\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0063\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0090\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512007\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0073\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0100\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512008\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0083\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0110\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512009\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0090\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0120\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512010\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0100\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0130\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-14 11:21:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512011\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0110\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t}\n" +
                    "\t\t]\n" +
                    "\t}\n" +
                    "}";
        }
        //???????????????
        if("17".equals(line.getId().toString())){
            order = "{\n" +
                    "\t\"version\": 1,\n" +
                    "\t\"taskId\": \"\",\n" +
                    "\t\"taskType\": \"51\",\n" +
                    "\t\"SysCode\": \"DZ3MHSGCJGD\",\n" +
                    "\t\"task\": {\n" +
                    "\t\t\"WipOrderNo\": \"DZICS-Manual\",\n" +
                    "\t\t\"WipOrderType\": \"1\",\n" +
                    "\t\t\"Facility\": \"1820\",\n" +
                    "\t\t\"ProductionLine\": \"1820WJ13\",\n" +
                    "\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\"WipOrderGroup\": \"32\",\n" +
                    "\t\t\"GroupCount\": \"01\",\n" +
                    "\t\t\"ProductNo\": \"SYG004975099\",\n" +
                    "\t\t\"ProductName\": \"?????????ZX55.3.1B.1\",\n" +
                    "\t\t\"ProductAlias\": \"W201B\",\n" +
                    "\t\t\"SerialNo\": [],\n" +
                    "\t\t\"Quantity\": 1000.0,\n" +
                    "\t\t\"ScheduledStartDate\": \"2021-06-11 10:01:37\",\n" +
                    "\t\t\"ScheduledCompleteDate\": \"2021-06-14 11:21:57\",\n" +
                    "\t\t\"ProgressStatus\": \"110\",\n" +
                    "\t\t\"SerialNumber\": \"\",\n" +
                    "\t\t\"SteelCode\": \"\",\n" +
                    "\t\t\"MRPController\": \"606\",\n" +
                    "\t\t\"WipOrderBatchNo\": \"00000000000000\",\n" +
                    "\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\"OprSequenceList\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0010\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:01:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0010\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0003\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-2\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0004\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975098H\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"???????????????ZX55.3.1B.1.2HZX55.3.1B.1.2H\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0020\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-29 09:30:15\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-29 09:55:15\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512002\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0030\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:37\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512003\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0020\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2???????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0040\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:42\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512004\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2??????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0001\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"11698243\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1A.1-3\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61A\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t},\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"1535189004\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"0002\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"SYG004975093\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"??????ZX55.3.1B.1-1\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"W61B\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"PC\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0050\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:47\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512005\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0040\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2?????????????????????????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 140.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0060\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-11 10:21:52\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-11 13:51:52\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"543001\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ09A0030\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0070\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:03:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512023\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ10A0071\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2???/3??????????????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0080\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:23:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512006\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0063\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0090\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"?????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:43:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512007\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0073\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0100\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:02\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512008\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0083\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????4????????????\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0110\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 09:44:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512009\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0090\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0120\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"??????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 10:04:07\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512010\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5/2.5?????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0100\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"??????2?????????\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t},\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"OprSequenceNo\": \"0130\",\n" +
                    "\t\t\t\t\"SequenceNo\": \"000000\",\n" +
                    "\t\t\t\t\"OprSequenceName\": \"????????????\",\n" +
                    "\t\t\t\t\"OprSequenceType\": \"??????\",\n" +
                    "\t\t\t\t\"ScheduledStartDate\": \"2022-06-12 11:01:57\",\n" +
                    "\t\t\t\t\"ScheduledCompleteDate\": \"2022-06-14 11:21:57\",\n" +
                    "\t\t\t\t\"WorkCenter\": \"512011\",\n" +
                    "\t\t\t\t\"WorkCenterName\": \"??????1.5???????????????????????????\",\n" +
                    "\t\t\t\t\"WorkStation\": \"1820WJ13A0110\",\n" +
                    "\t\t\t\t\"WorkStationName\": \"????????????2????????????????????????1\\n\",\n" +
                    "\t\t\t\t\"ProgressStatus\": 120.0,\n" +
                    "\t\t\t\t\"Quantity\": 5.0,\n" +
                    "\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\"paramRsrv5\": \"\",\n" +
                    "\t\t\t\t\"ComponentList\": [\n" +
                    "\t\t\t\t\t{\n" +
                    "\t\t\t\t\t\t\"ReserveNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"ReserveLineNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialNo\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialName\": \"\",\n" +
                    "\t\t\t\t\t\t\"MaterialAlias\": \"\",\n" +
                    "\t\t\t\t\t\t\"Quantity\": 0.0,\n" +
                    "\t\t\t\t\t\t\"Unit\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv1\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv2\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv3\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv4\": \"\",\n" +
                    "\t\t\t\t\t\t\"paramRsrv5\": \"\"\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t]\n" +
                    "\t\t\t}\n" +
                    "\t\t]\n" +
                    "\t}\n" +
                    "}";
        }
        return order;
    }
}
