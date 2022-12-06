package com.dzics.udp.mq.impl;

import com.dzics.udp.mq.RabbitmqService;
import com.dzics.udp.netty.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author ZhangChengJun
 * Date 2020/7/20.
 */
@Slf4j
@Component
@Scope("singleton")
public class RabbitmqServiceImpl implements RabbitmqService {
    @Autowired
    @Qualifier(value = "rabbitTemplate1")
    public RabbitTemplate rabbitTemplate1;
    @Autowired
    @Qualifier(value = "rabbitTemplate2")
    public RabbitTemplate rabbitTemplate2;
    @Autowired
    @Qualifier(value = "rabbitTemplate3")
    public RabbitTemplate rabbitTemplate3;
    @Autowired
    @Qualifier(value = "rabbitTemplate4")
    public RabbitTemplate rabbitTemplate4;
    @Autowired
    @Qualifier(value = "rabbitTemplate5")
    public RabbitTemplate rabbitTemplate5;
    @Autowired
    @Qualifier(value = "rabbitTemplate6")
    public RabbitTemplate rabbitTemplate6;
    @Autowired
    @Qualifier(value = "rabbitTemplate7")
    public RabbitTemplate rabbitTemplate7;
    @Autowired
    @Qualifier(value = "rabbitTemplate8")
    public RabbitTemplate rabbitTemplate8;
    @Autowired
    @Qualifier(value = "rabbitTemplate9")
    public RabbitTemplate rabbitTemplate9;
    @Autowired
    @Qualifier(value = "rabbitTemplate10")
    public RabbitTemplate rabbitTemplate10;
    @Autowired
    @Qualifier(value = "rabbitTemplate11")
    public RabbitTemplate rabbitTemplate11;
    @Autowired
    @Qualifier(value = "rabbitTemplate12")
    public RabbitTemplate rabbitTemplate12;
    @Autowired
    @Qualifier(value = "rabbitTemplate13")
    public RabbitTemplate rabbitTemplate13;
    @Autowired
    @Qualifier(value = "rabbitTemplate14")
    public RabbitTemplate rabbitTemplate14;
    @Autowired
    @Qualifier(value = "rabbitTemplate15")
    public RabbitTemplate rabbitTemplate15;
    @Autowired
    @Qualifier(value = "rabbitTemplate16")
    public RabbitTemplate rabbitTemplate16;
    @Autowired
    @Qualifier(value = "rabbitTemplate17")
    public RabbitTemplate rabbitTemplate17;
    @Autowired
    @Qualifier(value = "rabbitTemplate18")
    public RabbitTemplate rabbitTemplate18;
    @Autowired
    @Qualifier(value = "rabbitTemplate19")
    public RabbitTemplate rabbitTemplate19;
    @Autowired
    @Qualifier(value = "rabbitTemplate20")
    public RabbitTemplate rabbitTemplate20;
    @Autowired
    @Qualifier(value = "rabbitTemplate21")
    public RabbitTemplate rabbitTemplate21;
    @Autowired
    @Qualifier(value = "rabbitTemplate22")
    public RabbitTemplate rabbitTemplate22;
    @Autowired
    @Qualifier(value = "rabbitTemplate23")
    public RabbitTemplate rabbitTemplate23;
    @Autowired
    @Qualifier(value = "rabbitTemplate24")
    public RabbitTemplate rabbitTemplate24;
    @Autowired
    @Qualifier(value = "rabbitTemplate25")
    public RabbitTemplate rabbitTemplate25;
    @Autowired
    @Qualifier(value = "rabbitTemplate26")
    public RabbitTemplate rabbitTemplate26;
    @Autowired
    @Qualifier(value = "rabbitTemplate27")
    public RabbitTemplate rabbitTemplate27;
    @Autowired
    @Qualifier(value = "rabbitTemplate28")
    public RabbitTemplate rabbitTemplate28;
    @Autowired
    @Qualifier(value = "rabbitTemplate29")
    public RabbitTemplate rabbitTemplate29;
    @Autowired
    @Qualifier(value = "rabbitTemplate30")
    public RabbitTemplate rabbitTemplate30;
    @Autowired
    @Qualifier(value = "rabbitTemplate31")
    public RabbitTemplate rabbitTemplate31;
    @Autowired
    @Qualifier(value = "rabbitTemplate32")
    public RabbitTemplate rabbitTemplate32;
    @Autowired
    @Qualifier(value = "rabbitTemplate33")
    public RabbitTemplate rabbitTemplate33;
    @Autowired
    @Qualifier(value = "rabbitTemplate34")
    public RabbitTemplate rabbitTemplate34;
    @Autowired
    @Qualifier(value = "rabbitTemplate35")
    public RabbitTemplate rabbitTemplate35;
    @Autowired
    @Qualifier(value = "rabbitTemplate36")
    public RabbitTemplate rabbitTemplate36;
    @Autowired
    @Qualifier(value = "rabbitTemplate37")
    public RabbitTemplate rabbitTemplate37;
    @Autowired
    @Qualifier(value = "rabbitTemplate38")
    public RabbitTemplate rabbitTemplate38;
    @Autowired
    @Qualifier(value = "rabbitTemplate39")
    public RabbitTemplate rabbitTemplate39;
    @Autowired
    @Qualifier(value = "rabbitTemplate40")
    public RabbitTemplate rabbitTemplate40;
    @Autowired
    @Qualifier(value = "rabbitTemplate41")
    public RabbitTemplate rabbitTemplate41;
    @Autowired
    @Qualifier(value = "rabbitTemplate42")
    public RabbitTemplate rabbitTemplate42;
    @Autowired
    @Qualifier(value = "rabbitTemplate43")
    public RabbitTemplate rabbitTemplate43;
    @Autowired
    @Qualifier(value = "rabbitTemplate44")
    public RabbitTemplate rabbitTemplate44;
    @Autowired
    @Qualifier(value = "rabbitTemplate45")
    public RabbitTemplate rabbitTemplate45;
    @Autowired
    @Qualifier(value = "rabbitTemplate46")
    public RabbitTemplate rabbitTemplate46;
    @Autowired
    @Qualifier(value = "rabbitTemplate47")
    public RabbitTemplate rabbitTemplate47;
    @Autowired
    @Qualifier(value = "rabbitTemplate48")
    public RabbitTemplate rabbitTemplate48;
    @Autowired
    @Qualifier(value = "rabbitTemplate49")
    public RabbitTemplate rabbitTemplate49;
    @Autowired
    @Qualifier(value = "rabbitTemplate50")
    public RabbitTemplate rabbitTemplate50;
    @Autowired
    @Qualifier(value = "rabbitTemplate51")
    public RabbitTemplate rabbitTemplate51;
    @Autowired
    @Qualifier(value = "rabbitTemplate52")
    public RabbitTemplate rabbitTemplate52;
    @Autowired
    @Qualifier(value = "rabbitTemplate53")
    public RabbitTemplate rabbitTemplate53;
    @Autowired
    @Qualifier(value = "rabbitTemplate54")
    public RabbitTemplate rabbitTemplate54;
    @Autowired
    @Qualifier(value = "rabbitTemplate55")
    public RabbitTemplate rabbitTemplate55;
    @Autowired
    @Qualifier(value = "rabbitTemplate56")
    public RabbitTemplate rabbitTemplate56;
    @Autowired
    @Qualifier(value = "rabbitTemplate57")
    public RabbitTemplate rabbitTemplate57;
    @Autowired
    @Qualifier(value = "rabbitTemplate58")
    public RabbitTemplate rabbitTemplate58;
    @Autowired
    @Qualifier(value = "rabbitTemplate59")
    public RabbitTemplate rabbitTemplate59;
    @Autowired
    @Qualifier(value = "rabbitTemplate60")
    public RabbitTemplate rabbitTemplate60;
    @Autowired
    @Qualifier(value = "rabbitTemplate61")
    public RabbitTemplate rabbitTemplate61;
    @Autowired
    @Qualifier(value = "rabbitTemplate62")
    public RabbitTemplate rabbitTemplate62;
    @Autowired
    @Qualifier(value = "rabbitTemplate63")
    public RabbitTemplate rabbitTemplate63;
    @Autowired
    @Qualifier(value = "rabbitTemplate64")
    public RabbitTemplate rabbitTemplate64;
    @Autowired
    @Qualifier(value = "rabbitTemplate65")
    public RabbitTemplate rabbitTemplate65;
    @Autowired
    @Qualifier(value = "rabbitTemplate66")
    public RabbitTemplate rabbitTemplate66;
    @Autowired
    @Qualifier(value = "rabbitTemplate67")
    public RabbitTemplate rabbitTemplate67;
    @Autowired
    @Qualifier(value = "rabbitTemplate68")
    public RabbitTemplate rabbitTemplate68;
    @Autowired
    @Qualifier(value = "rabbitTemplate69")
    public RabbitTemplate rabbitTemplate69;
    @Autowired
    @Qualifier(value = "rabbitTemplate70")
    public RabbitTemplate rabbitTemplate70;
    @Autowired
    @Qualifier(value = "rabbitTemplate71")
    public RabbitTemplate rabbitTemplate71;
    @Autowired
    @Qualifier(value = "rabbitTemplate72")
    public RabbitTemplate rabbitTemplate72;
    @Autowired
    @Qualifier(value = "rabbitTemplate73")
    public RabbitTemplate rabbitTemplate73;
    @Autowired
    @Qualifier(value = "rabbitTemplate74")
    public RabbitTemplate rabbitTemplate74;
    @Autowired
    @Qualifier(value = "rabbitTemplate75")
    public RabbitTemplate rabbitTemplate75;
    @Autowired
    @Qualifier(value = "rabbitTemplate76")
    public RabbitTemplate rabbitTemplate76;
    @Autowired
    @Qualifier(value = "rabbitTemplate77")
    public RabbitTemplate rabbitTemplate77;
    @Autowired
    @Qualifier(value = "rabbitTemplate78")
    public RabbitTemplate rabbitTemplate78;
    @Autowired
    @Qualifier(value = "rabbitTemplate79")
    public RabbitTemplate rabbitTemplate79;
    @Autowired
    @Qualifier(value = "rabbitTemplate80")
    public RabbitTemplate rabbitTemplate80;
    @Autowired
    @Qualifier(value = "rabbitTemplate81")
    public RabbitTemplate rabbitTemplate81;
    @Autowired
    @Qualifier(value = "rabbitTemplate82")
    public RabbitTemplate rabbitTemplate82;
    @Autowired
    @Qualifier(value = "rabbitTemplate83")
    public RabbitTemplate rabbitTemplate83;
    @Autowired
    @Qualifier(value = "rabbitTemplate84")
    public RabbitTemplate rabbitTemplate84;
    @Autowired
    @Qualifier(value = "rabbitTemplate85")
    public RabbitTemplate rabbitTemplate85;
    @Autowired
    @Qualifier(value = "rabbitTemplate86")
    public RabbitTemplate rabbitTemplate86;
    @Autowired
    @Qualifier(value = "rabbitTemplate87")
    public RabbitTemplate rabbitTemplate87;
    @Autowired
    @Qualifier(value = "rabbitTemplate88")
    public RabbitTemplate rabbitTemplate88;
    @Autowired
    @Qualifier(value = "rabbitTemplate89")
    public RabbitTemplate rabbitTemplate89;
    @Autowired
    @Qualifier(value = "rabbitTemplate90")
    public RabbitTemplate rabbitTemplate90;
    @Autowired
    @Qualifier(value = "rabbitTemplate91")
    public RabbitTemplate rabbitTemplate91;
    @Autowired
    @Qualifier(value = "rabbitTemplate92")
    public RabbitTemplate rabbitTemplate92;
    @Autowired
    @Qualifier(value = "rabbitTemplate93")
    public RabbitTemplate rabbitTemplate93;
    @Autowired
    @Qualifier(value = "rabbitTemplate94")
    public RabbitTemplate rabbitTemplate94;
    @Autowired
    @Qualifier(value = "rabbitTemplate95")
    public RabbitTemplate rabbitTemplate95;
    @Autowired
    @Qualifier(value = "rabbitTemplate96")
    public RabbitTemplate rabbitTemplate96;
    @Autowired
    @Qualifier(value = "rabbitTemplate97")
    public RabbitTemplate rabbitTemplate97;
    @Autowired
    @Qualifier(value = "rabbitTemplate98")
    public RabbitTemplate rabbitTemplate98;
    @Autowired
    @Qualifier(value = "rabbitTemplate99")
    public RabbitTemplate rabbitTemplate99;
    @Autowired
    @Qualifier(value = "rabbitTemplate100")
    public RabbitTemplate rabbitTemplate100;

    @Autowired
    public SnowflakeUtil snowflakeUtil;

    @Value("${accq.product.qrode.up.udp.routing}")
    private String qrodeupRouting;
    @Value("${accq.product.qrode.up.udp.exchange}")
    private String qrodeupExchange;

    @Override
    public void sendJsonString(String cmdStr) {
        try {
            cmdStr = cmdStr.replaceAll("\u0000","");
            long start = System.currentTimeMillis();
            CorrelationData correlationDataNumber = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messagePropertiesNumber = new MessageProperties();
            Message messageNumber = new Message(cmdStr.getBytes("UTF-8"), messagePropertiesNumber);
            correlationDataNumber.setReturnedMessage(messageNumber);
            int hashTempNumber = hashTemp();
            sendMqcarStatusInfo(cmdStr, correlationDataNumber, hashTempNumber, qrodeupExchange, qrodeupRouting);
            long end = System.currentTimeMillis();
            log.info("UDP接收数据:{},转发耗时:{}", cmdStr, (end - start));
        } catch (Throwable throwable) {
            log.error("接收底层数据上发到队列错误：{}", throwable.getMessage(), throwable);
        }

    }

    public void sendMqcarStatusInfo(String jsonString, CorrelationData correlationData, int hashTemp, String directExchange, String directRouting) {
        if (hashTemp == 1) {
            rabbitTemplate1.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 2) {
            rabbitTemplate2.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 3) {
            rabbitTemplate3.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 4) {
            rabbitTemplate4.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 5) {
            rabbitTemplate5.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 6) {
            rabbitTemplate6.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 7) {
            rabbitTemplate7.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 8) {
            rabbitTemplate8.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 9) {
            rabbitTemplate9.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 10) {
            rabbitTemplate10.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 11) {
            rabbitTemplate11.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 12) {
            rabbitTemplate12.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 13) {
            rabbitTemplate13.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 14) {
            rabbitTemplate14.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 15) {
            rabbitTemplate15.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 16) {
            rabbitTemplate16.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 17) {
            rabbitTemplate17.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 18) {
            rabbitTemplate18.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 19) {
            rabbitTemplate19.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 20) {
            rabbitTemplate20.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 21) {
            rabbitTemplate21.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 22) {
            rabbitTemplate22.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 23) {
            rabbitTemplate23.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 24) {
            rabbitTemplate24.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 25) {
            rabbitTemplate25.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 26) {
            rabbitTemplate26.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 27) {
            rabbitTemplate27.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 28) {
            rabbitTemplate28.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 29) {
            rabbitTemplate29.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 30) {
            rabbitTemplate30.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 31) {
            rabbitTemplate31.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 32) {
            rabbitTemplate32.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 33) {
            rabbitTemplate33.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 34) {
            rabbitTemplate34.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 35) {
            rabbitTemplate35.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 36) {
            rabbitTemplate36.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 37) {
            rabbitTemplate37.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 38) {
            rabbitTemplate38.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 39) {
            rabbitTemplate39.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 40) {
            rabbitTemplate40.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 41) {
            rabbitTemplate41.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 42) {
            rabbitTemplate42.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 43) {
            rabbitTemplate43.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 44) {
            rabbitTemplate44.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 45) {
            rabbitTemplate45.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 46) {
            rabbitTemplate46.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 47) {
            rabbitTemplate47.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 48) {
            rabbitTemplate48.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 49) {
            rabbitTemplate49.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 50) {
            rabbitTemplate50.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 51) {
            rabbitTemplate51.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 52) {
            rabbitTemplate52.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 53) {
            rabbitTemplate53.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 54) {
            rabbitTemplate54.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 55) {
            rabbitTemplate55.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 56) {
            rabbitTemplate56.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 57) {
            rabbitTemplate57.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 58) {
            rabbitTemplate58.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 59) {
            rabbitTemplate59.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 60) {
            rabbitTemplate60.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 61) {
            rabbitTemplate61.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 62) {
            rabbitTemplate62.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 63) {
            rabbitTemplate63.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 64) {
            rabbitTemplate64.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 65) {
            rabbitTemplate65.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 66) {
            rabbitTemplate66.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 67) {
            rabbitTemplate67.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 68) {
            rabbitTemplate68.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 69) {
            rabbitTemplate69.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 70) {
            rabbitTemplate70.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 71) {
            rabbitTemplate71.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 72) {
            rabbitTemplate72.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 73) {
            rabbitTemplate73.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 74) {
            rabbitTemplate74.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 75) {
            rabbitTemplate75.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 76) {
            rabbitTemplate76.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 77) {
            rabbitTemplate77.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 78) {
            rabbitTemplate78.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 79) {
            rabbitTemplate79.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 80) {
            rabbitTemplate80.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 81) {
            rabbitTemplate81.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 82) {
            rabbitTemplate82.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 83) {
            rabbitTemplate83.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 84) {
            rabbitTemplate84.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 85) {
            rabbitTemplate85.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 86) {
            rabbitTemplate86.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 87) {
            rabbitTemplate87.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 88) {
            rabbitTemplate88.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 89) {
            rabbitTemplate89.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 90) {
            rabbitTemplate90.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 91) {
            rabbitTemplate91.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 92) {
            rabbitTemplate92.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 93) {
            rabbitTemplate93.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 94) {
            rabbitTemplate94.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 95) {
            rabbitTemplate95.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 96) {
            rabbitTemplate96.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 97) {
            rabbitTemplate97.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 98) {
            rabbitTemplate98.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 99) {
            rabbitTemplate99.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
        if (hashTemp == 100) {
            rabbitTemplate100.convertAndSend(directExchange, directRouting, jsonString, correlationData);
        }
    }

    public int hashTemp() {
        return (int) ((Math.random() * 100) + 1);
    }

}
