package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWaitCheckResMapper;
import com.dzics.common.dao.MomOrderCompletedMapper;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.DzWaitCheckRes;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.config.MapConfig;
import com.dzics.data.acquisition.service.MqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangChengJun
 * Date 2021/1/27.
 */
@Slf4j
@Service
@Scope("singleton")
public class MqServiceImpl implements MqService {

    @Qualifier("localRabbitTemplate")
    @Autowired
    private RabbitTemplate localRabbitTemplate;


    @Value("${dzics.cmd.iot.exchange}")
    private String cmdExchange;
    @Value("${dzics.cmd.iot.routing}")
    private String cmdRouting;
    @Autowired
    private MomOrderCompletedMapper completedMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private DzWaitCheckResMapper checkResMapper;

    @Override
    public void saveReportWorkHistory(ReqWorkQrCodeOrder qrCode) {
        try {
            String outFlag = qrCode.getOutFlag();

            String dzStationCode = qrCode.getDzStationCode();
            if (StringUtils.isEmpty(dzStationCode)) {
                if ("1".equals(outFlag)) {
                    extracted(qrCode);
                }
            } else {
                extracted(qrCode);
            }

        } catch (Throwable e) {
            log.error("????????????????????????????????????{},???????????????{}", e.getMessage(), qrCode, e);
        }
    }

    private void extracted(ReqWorkQrCodeOrder qrCode) {
        MomOrderCompleted completed = new MomOrderCompleted();
        completed.setProTaskId(qrCode.getProTaskId());
        completed.setWipOrderNo(qrCode.getWipOrderNo());
        completed.setOrderId(qrCode.getOrderId());
        completed.setLineId(qrCode.getLineId());
        completed.setStationId(qrCode.getStationId());
        completed.setProductNo(qrCode.getProductNo());
        completed.setOutinputType(qrCode.getOutInputType());
        completed.setDzStationCode(qrCode.getDzStationCode());
        completed.setDzStationCodeSpare(qrCode.getDzStationCodeSpare());
        completed.setProcessFlowId(qrCode.getProcessFlowId());
        completed.setStartTime(qrCode.getStartTime());
        completed.setCompleteTime(qrCode.getCompleteTime());
        completed.setQrCode(qrCode.getQrCode());
        completed.setOutFlag(qrCode.getOutFlag());
        completedMapper.insert(completed);
        String orderNo = qrCode.getOrderNo();
        String lineNo = qrCode.getLineNo();
        String url = "";
        WorkReportDto workReportDto = new WorkReportDto();
        try {
            //????????????
            if (orderNo.equals("DZ-1887") || orderNo.equals("DZ-1888") || orderNo.equals("DZ-1889") ||
                    //????????????
                    orderNo.equals("DZ-1890") || orderNo.equals("DZ-1891") ||
                    //?????????
                    "DZ-1955".equals(orderNo) || "DZ-1956".equals(orderNo) ||
                    //???????????????
                    "DZ-1871".equals(orderNo) || "DZ-1872".equals(orderNo) || "DZ-1873".equals(orderNo) || "DZ-1874".equals(orderNo) || "DZ-1875".equals(orderNo) || ("DZ-1876").equals(orderNo) || ("DZ-1877").equals(orderNo) ||
                    //???????????????
                    "DZ-1878".equals(orderNo) || "DZ-1879".equals(orderNo) || "DZ-1880".equals(orderNo)){
                if ("1".equals(qrCode.getOutFlag())) {
                    String ngCode = qrCode.getNgCode();
                    workReportDto.setOutOk("1".equals(ngCode) ? 0 : 1);
                    workReportDto.setQrCode(qrCode.getQrCode());
                    workReportDto.setOrderNo(orderNo);
                    workReportDto.setLineNo(lineNo);
                    workReportDto.setOrderId(qrCode.getOrderId());
                    workReportDto.setLineId(qrCode.getLineId());
//                ???????????????MOM
                    Map<String, String> mapIps = mapConfig.getMaps();
                    String plcIp = mapIps.get(orderNo + lineNo);
                    if (!StringUtils.isEmpty(plcIp)) {
                        url = "http://" + plcIp + ":8107/api/receive/data/work/report";
                        ResponseEntity<Result> result = restTemplate.postForEntity(url, workReportDto, Result.class);
                        Result body = result.getBody();
                        Integer code = body.getCode();
                        if (0 != code) {
                            DzWaitCheckRes checkRes = new DzWaitCheckRes();
                            checkRes.setUrl(url);
                            checkRes.setReqParms(JSONObject.toJSONString(workReportDto));
                            checkRes.setResParms(JSONObject.toJSONString(body));
                            checkResMapper.insert(checkRes);
                        }
                        log.info("??????????????? ?????? ????????? ??????:{} ,?????????{} , URL???{},????????????:{},???????????????{}", orderNo, lineNo, url, JSONObject.toJSONString(workReportDto), JSONObject.toJSONString(body));
                    } else {
                        log.error("???????????????AGV IP ???????????????orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
                    }
                }
            }
        } catch (Throwable throwable) {
            DzWaitCheckRes checkRes = new DzWaitCheckRes();
            checkRes.setUrl(url);
            checkRes.setReqParms(JSONObject.toJSONString(workReportDto));
            checkRes.setResParms(throwable.getMessage());
            checkResMapper.insert(checkRes);
            log.error("???????????????????????????orderNo:{},lineNo:{},???????????????{}", orderNo, lineNo, throwable.getMessage());
        }
    }

    @Override
    public void sendCmdUpIot(String toJSONStringMap) {
        Message message = MessageBuilder.withBody(toJSONStringMap.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding(StandardCharsets.UTF_8.name())
                .setMessageId(UUID.randomUUID().toString()).build();
        localRabbitTemplate.send(cmdExchange, cmdRouting, message);
    }

    @Override
    public void sendDataCenter(String key, String exchange, Object dzWorkpieceData) {
        try {
            Message message = MessageBuilder.withBody(JSONObject.toJSONString(dzWorkpieceData).getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            localRabbitTemplate.send(exchange, key, message);
        } catch (Throwable e) {
            log.error("????????????????????????????????????{},???????????????{}", e.getMessage(), dzWorkpieceData);
        }
    }

}
