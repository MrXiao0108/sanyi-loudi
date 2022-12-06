package com.dzics.sanymom.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.dzics.sanymom.service.CachingApi;
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
 * 质量参数上传队列
 */
@Configuration
@Data
@Slf4j
public class CheckProductCustomer {

    @Value("${mom.upload.quality.param.queue}")
    private String queueQualityParam;
    @Autowired
    private SendMomService sendMomService;


    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private CachingApi cachingApi;

    /**
     * {"createTime":1629342976000,"date":"2021-08-19","detect01":0.000,"detect02":0.000,"detect03":54.960,"detect04":54.842,"detect05":54.884,"detect06":0.000,"detect07":0.000,"detect08":9999.999,"detect09":9999.999,"detect10":9999.999,"detect11":9999.999,"detect12":9999.999,"detect13":9999.999,"detect14":9999.999,"detect15":9999.999,"detect16":9999.999,"detect17":9999.999,"detect18":9999.999,"detect19":9999.999,"detect20":9999.999,"detect21":9999.999,"detect22":9999.999,"detect23":9999.999,"detect24":0.000,"detect25":0.000,"detect26":9999.999,"detect27":9999.999,"detect28":9999.999,"detectorTime":1629343046000,"equipmentNo":"01","equipmentType":3,"id":"1428194072583360513","lineNo":"1","machineNumber":"0","name":"W73C","orderNo":"DZ-1875","outOk":0,"outOk01":0,"outOk02":0,"outOk03":0,"outOk04":1,"outOk05":1,"outOk06":0,"outOk07":0,"outOk08":0,"outOk09":0,"outOk10":0,"outOk11":0,"outOk12":0,"outOk13":0,"outOk14":0,"outOk15":0,"outOk16":0,"outOk17":0,"outOk18":0,"outOk19":0,"outOk20":0,"outOk21":0,"outOk22":0,"outOk23":0,"outOk24":0,"outOk25":0,"producBarcode":"SANY-19-AUG-21 10:08 68","productId":"18","productNo":"1018","workNumber":"0"}
     * <p>
     * [{"colName":"长","is_show":0,"colData":"detect01"},{"colName":"宽","is_show":0,"colData":"detect02"},{"colName":"直径左","is_show":0,"colData":"detect03"},{"colName":"直径中","is_show":0,"colData":"detect04"},{"colName":"直径右","is_show":0,"colData":"detect05"},{"colName":"CC","is_show":1,"colData":"detect06"},{"colName":"DD","is_show":1,"colData":"detect07"},{"colName":"头径","is_show":0,"colData":"detect08"},{"colName":"槽径","is_show":0,"colData":"detect09"},{"colName":"螺纹大径","is_show":0,"colData":"detect10"},{"colName":"螺纹小径","is_show":0,"colData":"detect11"},{"colName":"螺间距","is_show":0,"colData":"detect12"},{"colName":"外径","is_show":0,"colData":"detect13"},{"colName":"KK","is_show":1,"colData":"detect14"},{"colName":"螺纹角度","is_show":0,"colData":"detect15"},{"colName":"MM","is_show":1,"colData":"detect16"},{"colName":"NN","is_show":1,"colData":"detect17"},{"colName":"OO","is_show":1,"colData":"detect18"},{"colName":"PP","is_show":1,"colData":"detect19"},{"colName":"RR","is_show":1,"colData":"detect20"},{"colName":"SS","is_show":1,"colData":"detect21"},{"colName":"TT","is_show":1,"colData":"detect22"},{"colName":"UU","is_show":1,"colData":"detect23"},{"colName":"VV","is_show":1,"colData":"detect24"},{"colName":"WW","is_show":1,"colData":"detect25"},{"colName":"XX","is_show":1,"colData":"detect26"},{"colName":"YY","is_show":1,"colData":"detect27"},{"colName":"ZZ","is_show":1,"colData":"detect28"}]
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${mom.upload.quality.param.queue}")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费队列:{},信息:{}", queueQualityParam, msg);
            Map<String, Object> mpl = JSONObject.parseObject(msg);
            DzProductionLine line = cachingApi.getOrderIdAndLineId();
//            发送报工
            MomOrderCompleted staCode = null;
            String groupId = redisUniqueID.getGroupId();
            String orderNo = String.valueOf(mpl.get("orderNo"));
            String lineNo = String.valueOf(mpl.get("lineNo"));
            try {
                int outOk = Integer.parseInt(mpl.get("outOk").toString());
                WorkReportDto dto = new WorkReportDto();
                String qrCode = String.valueOf(mpl.get("producBarcode"));
                dto.setOutOk(outOk);
                dto.setQrCode(qrCode);
                dto.setOrderNo(orderNo);
                dto.setLineNo(lineNo);
                dto.setLineId(line.getId());
                dto.setOrderId(line.getOrderId());
                dto.setGroupId(groupId);
                staCode = this.workReport(dto);
            } catch (Throwable throwable) {
                log.error("执行上传检测->MOM流程完成后->发送报工信号失败:{}", throwable.getMessage(), throwable);
            }
//            发送检测
            if (staCode != null && StringUtils.isEmpty(staCode.getLogs())) {
                try {
                    staCode.setGroupId(redisUniqueID.getGroupId());
                    staCode.setMap(mpl);
                    staCode.setOrderNo(orderNo);
                    staCode.setLineNo(lineNo);
                    boolean b = sendMomService.uploadCheckData(staCode);
                    if (b) {
                        log.info("上传检测->MOM成功");
                    } else {
                        log.error("上传检测->MOM失败:{}", msg);
                    }
                } catch (Throwable throwable) {
                    log.error("上传检测->MOM失败:{}", throwable.getMessage(), throwable);
                }
            }else {
                log.error("没有报工的订单,检查订单是否切换成功,是否有在生产中的订单");
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", queueQualityParam, msg);
            channel.basicReject(deliveryTag, false);
        }
    }

    @Autowired
    private WorkReportServiceNgImpl workReportServiceNg;
    @Autowired
    private WorkReportServiceOkImpl workReportServiceOk;

    private MomOrderCompleted workReport(WorkReportDto qrCode) {
        log.info("报工开始");
        int outOk = qrCode.getOutOk();
        MomOrderCompleted dzst;
        if (outOk == 1) {
            log.info("OK报工");
            dzst = workReportServiceOk.sendWorkReport(qrCode);
        } else {
            log.info("NG报工");
            dzst = workReportServiceNg.sendWorkReport(qrCode);
        }
        log.info("报工结束");
        return dzst;
    }
}

