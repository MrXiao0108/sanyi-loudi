package com.dzics.data.acquisition.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化队列配置
 *
 * @author neverend
 */
@Configuration
public class DirectRabbitConfigInitQuery {
//============================================================================================
    /**
     * 累计数量基础数据底层发送过来的
     */
    @Value("${accq.read.cmd.queue.base}")
    private String queue;
    @Value("${accq.read.cmd.queue.base.exchange}")
    private String exchange;
    @Value("${accq.read.cmd.queue.base.routing}")
    private String routing;

    @Bean(name = "directQueueBase")
    public Queue directQueueBase() {
        return new Queue(queue, true);
    }

    @Bean("directExchangeBase")
    DirectExchange directExchangeBase() {
        return new DirectExchange(exchange, true, false);
    }

    @Bean("bindingDirectBase")
    Binding bindingDirectBase() {
        return BindingBuilder.bind(directQueueBase()).to(directExchangeBase()).with(routing);
    }
//============================================================================================

    /**
     * 机床告警队列
     */
    @Value("${accq.tool.alarm.logs}")
    private String toolAlarmLogs;
    @Value("${accq.tool.alarm.logs.routing}")
    private String toolAlarmLogsRouting;
    @Value("${accq.tool.alarm.logs.routing.exchange}")
    private String toolAlarmLogsExchange;

    @Bean(name = "toolAlarmLogs")
    public Queue toolAlarmLogs() {
        return new Queue(toolAlarmLogs, true);
    }

    @Bean("toolAlarmLogsRouting")
    DirectExchange toolAlarmLogsRouting() {
        return new DirectExchange(toolAlarmLogsRouting, true, false);
    }

    @Bean("toolAlarmLogsExchange")
    Binding toolAlarmLogsExchange() {
        return BindingBuilder.bind(toolAlarmLogs()).to(toolAlarmLogsRouting()).with(toolAlarmLogsExchange);
    }
//============================================================================================
    /**
     * 机器人报工看板队列
     * product position 生产产品当前工序
     */
    @Value("${accq.product.position.query}")
    private String positionQuery;
    @Value("${accq.product.position.routing}")
    private String positionRouting;
    @Value("${accq.product.position.exchange}")
    private String positionExchange;

    @Bean(name = "directQueueposition")
    public Queue directQueueposition() {
        return new Queue(positionQuery, true);
    }

    @Bean(name = "directExchangeBaseposition")
    DirectExchange directExchangeBaseposition() {
        return new DirectExchange(positionExchange, true, false);
    }

    @Bean(name = "bindingDirectBaseposition")
    Binding bindingDirectBaseposition() {
        return BindingBuilder.bind(directQueueposition()).to(directExchangeBaseposition()).with(positionRouting);
    }

//============================================================================================
    /**
     * 华培上发检测数据二维码队列
     */
    @Value("${accq.product.qrode.up.query}")
    private String qrodeupQuery;
    @Value("${accq.product.qrode.up.routing}")
    private String qrodeupRouting;
    @Value("${accq.product.qrode.up.exchange}")
    private String qrodeupExchange;

    @Bean(name = "directQueueqrodeup")
    public Queue directQueueqrodeup() {
        return new Queue(qrodeupQuery, true);
    }

    @Bean(name = "directExchangeBaseqrodeup")
    DirectExchange directExchangeBaseqrodeup() {
        return new DirectExchange(qrodeupExchange, true, false);
    }

    @Bean(name = "bindingDirectBaseqrodeup")
    Binding bindingDirectBaseqrodeup() {
        return BindingBuilder.bind(directQueueqrodeup()).to(directExchangeBaseqrodeup()).with(qrodeupRouting);
    }

//============================================================================================
    /**
     * 刷新前端页面指令信息
     */
    @Value("${dzics.html.queue.kanban.Refresh}")
    private String queueRefresh;
    @Value("${dzics.html.exchange.kanban.Refresh}")
    private String directExchangeRefresh;
    @Value("${dzics.html.routing.kanban.Refresh}")
    private String directRoutingRefresh;

    @Bean(name = "queueRefresh")
    public Queue queueRefresh() {
        return new Queue(queueRefresh, true);
    }

    @Bean("directExchangeRefresh")
    DirectExchange directExchangeRefresh() {
        return new DirectExchange(directExchangeRefresh, true, false);
    }

    @Bean("directRoutingRefresh")
    Binding directRoutingRefresh() {
        return BindingBuilder.bind(queueRefresh()).to(directExchangeRefresh()).with(directRoutingRefresh);
    }
//============================================================================================


    /**
     * 需要报工为MOM的数据队列
     */
    @Value("${mom.accq.product.position.query}")
    private String momPositionQuery;
    @Value("${mom.accq.product.position.routing}")
    private String momPositionRouting;
    @Value("${mom.accq.product.position.exchange}")
    private String momPositionExchange;

    @Bean(name = "momDirectQueueposition")
    public Queue momDirectQueueposition() {
        return new Queue(momPositionQuery, true);
    }

    @Bean(name = "momDdirectExchangeBaseposition")
    DirectExchange momDdirectExchangeBaseposition() {
        return new DirectExchange(momPositionExchange, true, false);
    }

    @Bean(name = "momDbindingDirectBaseposition")
    Binding momDbindingDirectBaseposition() {
        return BindingBuilder.bind(momDirectQueueposition()).to(momDdirectExchangeBaseposition()).with(momPositionRouting);
    }





    /**
     * 向 MQ 发送指令数据 用于发送到IOT 设备
     */
    @Value("${dzics.cmd.iot.query}")
    private String cmdIot;
    @Value("${dzics.cmd.iot.exchange}")
    private String cmdExchange;
    @Value("${dzics.cmd.iot.routing}")
    private String cmdRouting;

    @Bean(name = "cmdIot")
    public Queue cmdIot() {
        return new Queue(cmdIot, true);
    }

    @Bean(name = "cmdExchange")
    DirectExchange cmdExchange() {
        return new DirectExchange(cmdExchange, true, false);
    }

    @Bean(name = "cmdRouting")
    Binding cmdRouting() {
        return BindingBuilder.bind(cmdIot()).to(cmdExchange()).with(cmdRouting);
    }


//    订单开始延迟队列
}

