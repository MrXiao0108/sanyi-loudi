package com.dzics.sanymom.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.dzics.sanymom.service.MomHttpRequestService;
import com.dzics.sanymom.service.SendMomService;
import com.dzics.sanymom.service.impl.WorkReportServiceNgImpl;
import com.dzics.sanymom.service.impl.WorkReportServiceOkImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 工件位置系信息处理上报
 *
 * @author ZhangChengJun
 * Date 2021/6/11.
 * @since
 */
@Configuration
@Data
@Slf4j
public class WorkReportCustomer {

    @Autowired
    private MomHttpRequestService momHttpRequestService;
    @Value("${mom.accq.product.position.query}")
    private String momPositionQuery;
    @Autowired
    private WorkReportServiceNgImpl workReportServiceNg;
    @Autowired
    private WorkReportServiceOkImpl workReportServiceOk;
    @Autowired
    private SendMomService sendMomService;
    @Autowired
    private DzWorkpieceDataMapper workpieceDataMapper;
    @Autowired
    private RedisUniqueID redisUniqueID;

    /**
     * 向MOM 报工
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */

    @RabbitListener(queues = "${mom.accq.product.position.query}")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.info("消费队列:{},信息:{}", momPositionQuery, msg);
            WorkReportDto qrCode = JSONObject.parseObject(msg, WorkReportDto.class);
            String orderNo = qrCode.getOrderNo();
            String lineNo = qrCode.getLineNo();
            log.info("报工开始:{}", orderNo);
            MomOrderCompleted dzst;
            int outOk = qrCode.getOutOk();
            if (outOk == 1) {
                log.info("OK报工");
                dzst = workReportServiceOk.sendWorkReport(qrCode);
            } else {
                log.info("NG报工");
                dzst = workReportServiceNg.sendWorkReport(qrCode);
            }
            log.info("报工结束:{}", orderNo);
            if (dzst != null && StringUtils.isEmpty(dzst.getLogs())) {
                //两米活塞杆
                if (orderNo.equals("DZ-1871") || orderNo.equals("DZ-1872") || orderNo.equals("DZ-1873") || orderNo.equals("DZ-1874") || orderNo.equals("DZ-1875") || orderNo.equals("DZ-1876") || orderNo.equals("DZ-1877") ||
                        //三米活塞杆
                        orderNo.equals("DZ-1878") || orderNo.equals("DZ-1879") || orderNo.equals("DZ-1880") ||
                        //两米缸筒
                        orderNo.equals("DZ-1887") || orderNo.equals("DZ-1888") || orderNo.equals("DZ-1889") ||
                        //三米缸筒
                        orderNo.equals("DZ-1890") || orderNo.equals("DZ-1891")){
                    log.info("检测数据上传开始：{}", orderNo);
//            发送检测
                    try {
                        String qrC = dzst.getQrCode();
                        DzWorkpieceData dzWorkpieceData = workpieceDataMapper.getOrderNoLineNoQrcode(orderNo, qrC);
                        if (dzWorkpieceData != null) {
//                        转map
                            String msssg = JSONObject.toJSONString(dzWorkpieceData);
                            Map<String, Object> mpl = JSONObject.parseObject(msssg);
                            dzst.setGroupId(redisUniqueID.getGroupId());
                            dzst.setMap(mpl);
                            dzst.setOrderNo(orderNo);
                            dzst.setLineNo(lineNo);
                            boolean b = sendMomService.uploadCheckData(dzst);
                            if (b) {
                                log.info("上传检测->MOM成功");
                            } else {
                                log.error("上传检测->MOM失败:{}", msg);
                            }
                        } else {
                            log.error("根据二维码查询检测记录不存在 orderNo:{}, qrC:{}, dzWorkpieceData:{}", orderNo, qrC, dzWorkpieceData);
                        }

                    } catch (Throwable throwable) {
                        log.error("上传检测->MOM失败:{}", throwable.getMessage(), throwable);
                    }
                    log.info("检测数据上传上传结束：{}", orderNo);
                }
            } else {
                log.error("没有报工的订单,检查订单是否切换成功,是否有在生产中的订单");
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", momPositionQuery, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }


}
