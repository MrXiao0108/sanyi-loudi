package com.dzics.sanymom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 质量参数上传 转发队列
 */
@Configuration
public class UploadQualityParamConfig {



    @Value("${mom.upload.quality.param.queue}")
    private String queueQualityParam;
    @Value("${mom.upload.quality.param.exchange}")
    private String qualityParamExchange;
    @Value("${mom.upload.quality.param.routing}")
    private String qualityParamRouting;




    @Bean(name = "queueQualityParam")
    public Queue queueQualityParam() {
        return new Queue(queueQualityParam, true,false,false);
    }
    /**
     * 创建订单交换机
     * @return
     */
    @Bean("qualityParamExchange")
    DirectExchange qualityParamExchange() {
        return new DirectExchange(qualityParamExchange, true, false);
    }



    @Bean("qualityParamRouting")
    Binding qualityParamRouting() {
        return BindingBuilder.bind(queueQualityParam()).to(qualityParamExchange()).with(qualityParamRouting);
    }
}
