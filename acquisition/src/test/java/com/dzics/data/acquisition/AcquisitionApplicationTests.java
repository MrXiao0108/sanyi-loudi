package com.dzics.data.acquisition;

import com.baomidou.mybatisplus.extension.api.R;
import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionLineChartDo;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.service.AccqAnalysisNumSignalService;
import com.dzics.data.acquisition.service.AccqDzProductService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobConfigurationAPI;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.ShardingStatisticsAPI;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
class AcquisitionApplicationTests {
    @Autowired
    TcpStringUtil tcpStringUtil;

    @Autowired
    CacheService cacheService;

    @Autowired
    DateUtil dateUtil;

    @Autowired
    private AccqDzProductService dzProductService;

    @Autowired
    private AccqAnalysisNumSignalService accqAnalysisNumSignalService;

    @Test
    public void cmd (){
        String x = "B810|[SY01,,,0,1,0,0,1]";
        RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
        rabbitmqMessage.setDeviceType("1");
        rabbitmqMessage.setMessage(x);
        rabbitmqMessage.setOrderCode("DZ-1871");
        rabbitmqMessage.setClientId("z");
        rabbitmqMessage.setDeviceCode("A1");
        rabbitmqMessage.setQueueName("");
        rabbitmqMessage.setTimestamp("");
        rabbitmqMessage.setLineNo("1");
        rabbitmqMessage.setMessageId("");
        rabbitmqMessage.setCheck(false);
        DzEquipmentProNumSignal dzEquipmentProNumSignal = accqAnalysisNumSignalService.queuePylseSignal(rabbitmqMessage);
        System.out.println(dzEquipmentProNumSignal);
    }
    @Test
    void contextLoads() throws ParseException {
        List<CmdTcp> cmdTcp = tcpStringUtil.getCmdTcp("A561|");
        log.info("指令解析：{} ",cmdTcp);

    }
    @Test
   public void  txt (){
        CmdTcp cmdTcp = new CmdTcp();
        cmdTcp.setTcpValue("B810");
        cmdTcp.setDeviceItemValue("[SY01,,,0,1,0,0,1]");
        cmdTcp.setTcpDescription("[SY01,,,0,1,0,0,1]");

       accqAnalysisNumSignalService.getSingValue(cmdTcp);
   }


    @Test
    public Object test(){
        GetOrderNoLineNo data=new GetOrderNoLineNo();
        data.setOrderNo("DZ-1887");
        data.setLineNo("1");
        GetDetectionLineChartDo getDetectionLineChartDo=dzProductService.getDetectionLineChart(data);
        return getDetectionLineChartDo;
    }
}
