package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionOneDo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.ProDetection;
import com.dzics.common.service.DzWorkpieceDataService;
import com.dzics.common.service.MomOrderService;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.service.AccDetectorDataService;
import com.dzics.data.acquisition.service.AccqDzProductService;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Classname CheckHistorySubscribe
 * @Description 检测订阅
 * @Date 2022/6/24 8:48
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class CheckHistorySubscribe {
    private final SocketIoHandler socketIoHandler;
    private final AccDetectorDataService accDetectorDataService;
    private final DzWorkpieceDataService dzWorkpieceDataService;
    private final AccqDzProductService dzProductService;

    private DzWorkpieceDataMapper dzWorkpieceDataMapper;

    public CheckHistorySubscribe(SocketIoHandler socketIoHandler, AccDetectorDataService accDetectorDataService, DzWorkpieceDataService dzWorkpieceDataService, AccqDzProductService dzProductService, DzWorkpieceDataService workpieceDataService, MomOrderService momOrderService,DzWorkpieceDataMapper dzWorkpieceDataMapper) {
        this.socketIoHandler = socketIoHandler;
        this.accDetectorDataService = accDetectorDataService;
        this.dzWorkpieceDataService = dzWorkpieceDataService;
        this.dzProductService = dzProductService;
        this.dzWorkpieceDataMapper = dzWorkpieceDataMapper;
    }

    /**
     * 订阅检测项记录
     */
    @OnEvent(value = SocketMessageType.TEST_ITEM_RECORD)
    public void getTestItemRecord(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 检测项记录 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            //        检测数据
            Result<JCEquimentBase<ProDetection<List<Map<String, Object>>>>> detectorData = accDetectorDataService.getDetectorData(orderNo, lineNo);
            client.sendEvent(SocketMessageType.TEST_ITEM_RECORD, detectorData);
            socketIoHandler.addEvent(client, SocketMessageType.TEST_ITEM_RECORD + orderNo + lineNo, "检测项记录");
            long b = System.currentTimeMillis();
            log.info("订阅检测项记录,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * 取消订阅检测项记录
     */
    @OnEvent(value = SocketMessageType.UN_TEST_ITEM_RECORD)
    public void unGetTestItemRecord(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅检测项 ] 记录:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.TEST_ITEM_RECORD + orderNo + lineNo, "检测项记录");
    }


    /**
     * 三米缸筒 产品检测单项订阅
     */
    @OnEvent(value = SocketMessageType.GET_DETECTION_ONE)
    public void getDetectionOne(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            log.info("IP:{},开始订阅 [ 检测项记录单项 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
            List<GetDetectionOneDo> getDetectionOneDos = accDetectorDataService.getDetectionOne(data);
            JCEquimentBase<List<GetDetectionOneDo>> jcEquimentBase = new JCEquimentBase<>();
            jcEquimentBase.setData(getDetectionOneDos);
            jcEquimentBase.setType(DeviceSocketSendStatus.GET_DETECTION_ONE.getInfo());
            client.sendEvent(SocketMessageType.GET_DETECTION_ONE, Result.ok(jcEquimentBase));
            socketIoHandler.addEvent(client, SocketMessageType.GET_DETECTION_ONE + data.getOrderNo() + data.getLineNo(), "检测项记录单项");
            long b = System.currentTimeMillis();
            log.info("三米缸筒 产品检测单项订阅,订单号:{},产线号:{},耗时:{}毫秒", data.getOrderNo(), data.getLineNo(), (b - a));
        }
    }

    /**
     * 三米缸筒 产品检测单项订阅  取消
     */
    @OnEvent(value = SocketMessageType.UN_GET_DETECTION_ONE)
    public void unGetSanYiDetectionOne(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 检测项记录单项 ]:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        socketIoHandler.unEvent(client, SocketMessageType.GET_DETECTION_ONE, "检测项记录单项");
    }


    /**
     * 智能检测系统推送  订阅
     */
    @OnEvent(value = SocketMessageType.get_intelligent_detection)
    public void getIntelligentDetection(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) throws Exception {
        long a = System.currentTimeMillis();
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            log.info("IP:{},开始订阅 [ 智能检测系统推送 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
            JCEquimentBase<Map<String, Object>> jcEquimentBase = new JCEquimentBase<>();
            DzWorkpieceData dzWorkpieceData = dzWorkpieceDataService.getLastDzWorkpieceData(data.getOrderNo(), data.getLineNo(), LocalDate.now().toString());
            if (dzWorkpieceData != null) {
                Map<String, Object> result = dzProductService.getIntelligentDetection(dzWorkpieceData);
                jcEquimentBase.setData(result);
            } else {
                log.warn("智能检测系统推送,该订单当日暂无检测数据，日期:{}", LocalDate.now());
            }
            jcEquimentBase.setType(DeviceSocketSendStatus.Get_Intelligent_Detection.getInfo());
            client.sendEvent(SocketMessageType.get_intelligent_detection, Result.ok(jcEquimentBase));
            socketIoHandler.addEvent(client, SocketMessageType.get_intelligent_detection + data.getOrderNo() + data.getLineNo(), "智能检测系统推送");
            long b = System.currentTimeMillis();
            log.info("智能检测系统推送,订单号:{},产线号:{},耗时:{}毫秒", data.getOrderNo(), data.getLineNo(), (b - a));
        }
    }

    /**
     * 智能检测系统推送  取消
     */
    @OnEvent(value = SocketMessageType.un_get_intelligent_detection)
    public void unGetIntelligentDetection(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 智能检测系统推送 ]:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        socketIoHandler.unEvent(client, SocketMessageType.get_intelligent_detection + data.getOrderNo() + data.getLineNo(), "智能检测系统推送");
    }


    /**
     * 缸筒线检测数据有MOM订单号和物料的检测记录MOM 订阅
     */
    @OnEvent(value = SocketMessageType.get_detection_record)
    public void getDetectionRecord(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 检测记录MOM ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            //获取最新25条检测数据的ID
            List<String> ids = dzWorkpieceDataService.getWorkPieceData(orderNo, lineNo, 25);
            Result detector = accDetectorDataService.getDetectionRecordMom(ids, orderNo, lineNo);
            detector.setRef(true);
            client.sendEvent(SocketMessageType.get_detection_record, detector);
            socketIoHandler.addEvent(client, SocketMessageType.get_detection_record + orderNo + lineNo, "检测记录MOM");
            long b = System.currentTimeMillis();
            log.info("订阅检测记录MOM,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消  缸筒线检测数据有MOM订单号和物料的检测记录MOM 订阅
     */
    @OnEvent(value = SocketMessageType.un_get_detection_record)
    public void unGetDetectionRecord(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 检测记录MOM ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.get_detection_record + orderNo + lineNo, "检测记录MOM");
    }

    /**
     * 人工打磨马尔表数据追踪
     */
    @OnEvent(value = SocketMessageType.get_maerbiao_record)
    public void getMaErBiaoDetectionMonitor(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 人工打磨检测数据追踪 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            //初始化获取当前产线最新的一条检测数据
            List<DzWorkpieceData> list = dzWorkpieceDataMapper.selectList(new QueryWrapper<DzWorkpieceData>()
                    .eq("order_no", data.getOrderNo()).eq("line_no", data.getLineNo()).orderByDesc("detector_time"));
            Result maErBiaoDetectionMonitor = dzWorkpieceDataService.getMaErBiaoDetectionMonitor(data.getOrderNo(), data.getLineNo(), list.get(0).getProducBarcode());
            maErBiaoDetectionMonitor.setRef(true);
            client.sendEvent(SocketMessageType.get_maerbiao_record, maErBiaoDetectionMonitor);
            socketIoHandler.addEvent(client, SocketMessageType.get_maerbiao_record + orderNo + lineNo, "人工打磨检测数据追踪");
            long b = System.currentTimeMillis();
            log.info("订阅 人工打磨检测数据追踪,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消  人工打磨马尔表数据追踪
     */
    @OnEvent(value = SocketMessageType.un_get_maerbiao_record)
    public void unGetMaErBiaoDetectionMonitor(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 人工打磨检测数据追踪 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.get_maerbiao_record + orderNo + lineNo, "人工打磨检测数据追踪");
    }
}
