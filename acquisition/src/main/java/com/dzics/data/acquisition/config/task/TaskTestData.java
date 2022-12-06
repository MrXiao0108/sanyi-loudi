package com.dzics.data.acquisition.config.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWorkingFlowMapper;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.stationbg.ResponseWorkStationBg;
import com.dzics.common.model.response.productiontask.stationbg.WorkingFlowResBg;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.common.service.DzWorkingFlowService;
import com.dzics.common.service.DzWorkpieceDataService;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.SocketServerTemplate;
import com.dzics.data.acquisition.service.AccStorageLocationService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.DeviceStatusPush;
import com.dzics.data.acquisition.util.TestLocalData;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;

//@RestController
@Component
@Slf4j
public class TaskTestData {
    @Autowired
    private SocketServerTemplate socketServerTemplate;
    @Autowired
    private DzWorkingFlowMapper dzWorkingFlowMapper;
    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;
    @Autowired
    private MomOrderMapper momOrderMapper;
    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Autowired
    private CacheService cacheService;
    @Autowired
    DzEquipmentService dzEquipmentService;
    @Autowired
    private AccStorageLocationService accqDzEquipmentService;
    @Autowired
    private DzWorkpieceDataService dzWorkpieceDataService;
    //    @Scheduled(fixedRate = 2000)
    public void aa() {
        MonOrder monOrder = new MonOrder();
        monOrder.setOrderOperationResult(2);
        momOrderMapper.update(monOrder, new QueryWrapper<MonOrder>());
    }

    //    @Scheduled(fixedRate = 2000)
    public void dd() throws ParseException {
        String[] ss = {"110", "120", "130", "140", "150", "160"};
        try {
            MonOrder monOrder = new MonOrder();
            monOrder.setProTaskOrderId("1419577958979772417");
            monOrder.setOrderOutput(new Random().nextInt(666));
            monOrder.setOrderOperationResult(2);
            monOrder.setProgressStatus(ss[new Random().nextInt(6)]);
            String a = ss[new Random().nextInt(6)];
            if (new Random().nextInt(6) >= 3) {
                monOrder.setRealityStartDate(new Date());
                monOrder.setRealityCompleteDate(new Date());
            }
            String eventKey = getEvent(SocketMessageType.GET_MOM_ORDER_STATE, "", "", "");
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(monOrder);
            int i = new Random().nextInt(2);
            if (i == 0) {
                jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_STATE.getInfo());
            } else {
                jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_QUANTITY.getInfo());
            }
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.GET_MOM_ORDER_STATE, eventKey, ok);
        } catch (Exception e) {
            log.error("asd");
        }
    }

    private String getEvent(String event, String orderCode, String lineNo, String deviceType) {
        if (orderCode.equals("-")) {
            orderCode = "";
        }
        if (lineNo.equals("-")) {
            lineNo = "";
        }
        if (deviceType.equals("-")) {
            deviceType = "";
        }
        return event + orderCode + lineNo + deviceType;
    }

    //    @Scheduled(fixedDelay = 7000, initialDelay = 7000)
    public void sendBaoGong() {
        Date date = new Date();
        String dateStr = DateUtil.getDateStr(date);
        Map<String, List<ResponseWorkStationBg>> listMap = TestLocalData.listMap;
        if (CollectionUtils.isNotEmpty(listMap)) {
            List<ResponseWorkStationBg> kb = listMap.get("KB");
            ResponseWorkStationBg responseWorkStationBg = kb.get(0);
            WorkingFlowResBg workingFlowRes = responseWorkStationBg.getWorkingFlowRes();
            workingFlowRes.setQrCode(UUID.randomUUID().toString().replaceAll("-", ""));
            workingFlowRes.setWorkpieceCode(UUID.randomUUID().toString().substring(0, 8));
            workingFlowRes.setUpdateTime(date);
            workingFlowRes.setUpdateTimeUse(dateStr);

            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_WORK_REPORT_INFORMATION_SINGLE.getInfo());
            jcEquimentBase.setData(responseWorkStationBg);
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            String eventKey = SocketMessageType.WORKPIECE_POSITION + "DZ-1875" + "1";
            socketServerTemplate.sendMessage(SocketMessageType.WORKPIECE_POSITION, eventKey, ok);
        } else {
            OrderIdLineId orderIdLineId = cacheService.getOrderNoLineNoId("DZ-1875", "1");
            Long lineId = orderIdLineId.getLineId();
            Long orderId = orderIdLineId.getOrderId();
            PageHelper.startPage(1, 7);
            List<String> qrCodeX = dzWorkingFlowMapper.getWorkingFlowBigQrCode(orderId, lineId, null, null);
            PageInfo<String> stringPageInfo = new PageInfo<>(qrCodeX);
            List<String> qrCode = stringPageInfo.getList();
            List<ResponseWorkStationBg> workpiecePosition = dzWorkingFlowService.getPosition(qrCode, orderId, lineId);
            Map<String, List<ResponseWorkStationBg>> map = new HashMap<>();
            map.put("KB", workpiecePosition);
            TestLocalData.listMap = map;
        }

    }

    //    @Scheduled(fixedDelay = 3000, initialDelay = 3000)
    public void sendFrid() {
//        Q,2,1004,A,MOM订单号,物料号,工序号,托盘编号,数量
        String[] A = "Q,2,1004,A,384753845732,8937495743,8447,897,98".split(",");
        String[] B = "Q,2,1004,B,384753845732,8937495743,8447,897,98".split(",");
        String[] C = "Q,2,1004,C,384753845732,8937495743,8447,897,98".split(",");
        String[] xa = "Q,2,1005,A,89e4758348934758923748936588902398346shukdfhjukhkfsdjfhskfsdfbjsfjhsjdhskjdh".split(",");
        String[] xb = "Q,2,1005,B,89e4758348934758923748936588902398346shukdfhjukhkfsdjfhskfsdfbjsfjhsjdhskjdh".split(",");
        String[] xc = "Q,2,1005,C,89e4758348934758923748936588902398346shukdfhjukhkfsdjfhskfsdfbjsfjhsjdhskjdh".split(",");
        int a = (int) (Math.random() * 10 + 1);
        if (a < 4) {
            deviceStatusPush.pushFrdiJson(A);
//        Q,2,1005,A,原始信息
            deviceStatusPush.pushFridOld(xa);
        }
        if (a >= 4 && a < 7) {
            deviceStatusPush.pushFrdiJson(B);
//        Q,2,1005,A,原始信息
            deviceStatusPush.pushFridOld(xb);
        }
        if (a >= 7) {
            deviceStatusPush.pushFrdiJson(C);
//        Q,2,1005,A,原始信息
            deviceStatusPush.pushFridOld(xc);
        }
    }
//    测试FIRD 扫描数据 发送

    //        @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void sendEqState() {
        DzEquipment byId = dzEquipmentService.getById(5);
        int i = new Random().nextInt(10);
        byId.setAlarmStatus(i > 5 ? "其他" : "报警");
        deviceStatusPush.sendStateEquiment(byId);
    }

    public static final List<Integer> objects = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
            51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
            61, 62, 63, 64, 65, 66, 67, 68, 69, 70,
            71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
            81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
            91, 92, 93, 94, 95, 96, 97, 98, 99);
    public static Integer index = 0;


//    @Scheduled(fixedDelay = 1000)
    public void sendState() {
        index = index + 1;
        if (index > 98) {
            index = 0;
        }

        String orderNo = "DZ-1871";
        if (Math.random() * 10 < 5) {
            send("123.000,222.000,333.000,444.000,555.000,0.000", orderNo, 5L, "[108,9]", "[19064,18]", "机器人", "自动", "脱机",
                    "暂停", "急停", "告警", String.valueOf(objects.get(index)), "2000", "2",
                    "14", "2000", "223,2231", "01");
        } else {
            send("124.000,333.000,444.000,222.000,0.000,0.000", orderNo, 5L, "[104,7]", "[29064,21]", "机器人", "手动", "联机",
                    "生产", "未急停", "其他", String.valueOf(objects.get(index)), "1000", "1",
                    "10", "1000", "123,1231", "01");
        }


//        --------------------------------------
        if (Math.random() * 10 < 5) {
            send("125.000,444.000,555.000,333.000,0.000,0.000", orderNo, 6L, "[94,5]", "[23064,15]", "车床", "自动", "脱机",
                    "待机", "急停", "告警", String.valueOf(objects.get(index)), "1300", "4",
                    "13", "1400", "323,4231", "A1");
        } else {
            send("126.000,555.000,666.000,444.000,0.000,0.000", orderNo, 6L, "[55,7]", "[29100,32]", "车床", "手动", "联机",
                    "生产", "未急停", "其他", String.valueOf(objects.get(index)), "1200", "4",
                    "12", "1200", "111,3231", "A1");

        }


//        --------------------------------------
        if (Math.random() * 10 < 5) {
            send("127.000,666.000,777.000,555.000,0.000,0.000", orderNo, 7L, "[99,6]", "[22064,71]", "磨床", "手动", "联机",
                    "生产", "未急停", "其他", String.valueOf(objects.get(index)), "800", "2",
                    "13", "1120", "67,2231", "A2");
        } else {
            send("128.000,777.000,888.000,666.000,0.000,0.000", orderNo, 7L, "[90,5]", "[23064,62]", "磨床", "自动", "脱机",
                    "暂停", "急停", "告警", String.valueOf(objects.get(index)), "810", "3",
                    "12", "1230", "77,1331", "A2");
        }


//        --------------------------------------
        if (Math.random() * 10 < 5) {
            send("129.000,888.000,999.000,777.000,777.000,0.000", orderNo, 8L, "[85,4]", "[24064,63]", "检测台", "手动", "联机",
                    "生产", "未急停", "其他", String.valueOf(objects.get(index)), "820", "4",
                    "11", "1340", "87,1431", "A3");
        } else {
            send("130.000,999.000,121.000,888.000,888.000,0.000", orderNo, 8L, "[80,3]", "[25064,64]", "检测台", "自动", "脱机",
                    "暂停", "急停", "告警", String.valueOf(objects.get(index)), "830", "5",
                    "9", "1450", "98,1631", "A3");
        }


    }

    public void send(String no, String orderNo, Long id, String b527, String b526, String enqName, String operMode, String connectState, String runStatus, String emergencyStatus, String alarmStatus, String speedRatio, String a541, String b809, String a812, String machiningTime, String cleanTime, String equipmentNo) {
        DzEquipment dzEquipment = new DzEquipment();
        dzEquipment.setCurrentLocation(no);
        dzEquipment.setB527(b527);
        dzEquipment.setB526(b526);
        dzEquipment.setEquipmentName(enqName);
        dzEquipment.setId(id);
        dzEquipment.setOperatorMode(operMode);
        dzEquipment.setConnectState(connectState);
        dzEquipment.setRunStatus(runStatus);
        dzEquipment.setEmergencyStatus(emergencyStatus);
        dzEquipment.setAlarmStatus(alarmStatus);
        dzEquipment.setSpeedRatio(speedRatio);
        dzEquipment.setA541(a541);
        dzEquipment.setB809(b809);
        dzEquipment.setA812(a812);
        dzEquipment.setMachiningTime(machiningTime);
        dzEquipment.setCleanTime(cleanTime);
        dzEquipment.setOrderNo(orderNo);
        dzEquipment.setEquipmentNo(equipmentNo);
        dzEquipment.setLineNo("1");
        deviceStatusPush.sendStateEquiment(dzEquipment);
    }

//      @GetMapping("/znjc/test")
    public Object ss() throws Exception {
          DzWorkpieceData dzWorkpieceData=dzWorkpieceDataService.getById("1449993843283226626");
          boolean b = deviceStatusPush.sendIntelligentDetection(dzWorkpieceData);
          return b;
      }
}
