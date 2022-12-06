package com.dzics.mqtt.config;

import com.alibaba.fastjson.JSONObject;
import com.dzics.mqtt.model.entity.SysConfigMqtt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 连接mqtt
 *
 * @author ZhangChengJun
 * Date 2021/7/2.
 * @since
 */
@Configuration
@Slf4j
public class ConnectMqttConfig {
    private static final Base64 base64 = new Base64();
    /**
     * 当前运行环境
     */
    @Value("${spring.profiles.active}")
    private String profilesActive;


    @Bean
    public SysConfigMqtt sysConfigMqtt() throws Throwable {
//        检测是否获取到 连接配置
        if (!StringUtils.isEmpty(profilesActive)) {
            log.info("读取秘钥配置配置文件：{}", profilesActive+".sk");
//              发送请求获取连接配置

            InputStream crt = this.getClass().getClassLoader().getResourceAsStream(profilesActive + ".sk");
            String sk = readTxt(crt);
            String resp = new String(base64.decode(sk), "UTF-8");
            SysConfigMqtt configMqtt = JSONObject.parseObject(resp, SysConfigMqtt.class);
            configMqtt.setSpringProfilesActive(profilesActive);
            String clientId = configMqtt.getVersion() + "." + configMqtt.getProductType() + "." + configMqtt.getPlatform_id() + "." + configMqtt.getNamespace() + "."
                    + configMqtt.getProductKey() + "." + configMqtt.getAccesskey();
            configMqtt.setClientId(clientId);
            configMqtt.setTopicSoft("$SANY/soft/pdev/sany/mdc/" + configMqtt.getAccesskey() + "/data");
            configMqtt.setTopicGateway("$SANY/gateway/pdev/sany/mdc/" + configMqtt.getAccesskey() + "/data");
            log.info("读取取到mqtt连接参数信息：{} , clientId: {} ", JSONObject.toJSONString(configMqtt), clientId);
            return configMqtt;
        } else {
            throw new RuntimeException("无法获取到运行环境：dev or pro");
        }

    }

    public String readTxt(InputStream file) throws IOException {
        String sk = "";
        InputStreamReader in = new InputStreamReader(file, "UTF-8");
        BufferedReader br = new BufferedReader(in);
        StringBuffer content = new StringBuffer();
        while ((sk = br.readLine()) != null) {
            content = content.append(sk);
        }
        return content.toString();
    }

}


