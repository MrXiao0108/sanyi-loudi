package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.exception.BindQrCodeException;
import com.dzics.common.model.constant.QrCode;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.config.redis.RedisPrefxKey;
import com.dzics.data.acquisition.service.*;
import com.dzics.data.acquisition.service.mq.RabbitmqService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消费产品位置队列消息
 *
 * @author ZhangChengJun
 * Date 2021/5/17.
 * @since
 */
@Component
@Slf4j
public class ProductPositionCousomer {
    @Value("${accq.product.position.query}")
    private String positionQuery;
    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Autowired
    private WorkingFlowService workingFlowService;
    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    @Autowired
    private MqService mqService;
    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private RabbitmqService rabbitmqService;
    //            两米活塞杆1号岛	        DZ-1871
    @Value("${accq.1871.inner.product.position.query}")
    private String positionQuery1871;
    @Value("${accq.1871.inner.product.position.routing}")
    private String positionRouting1871;
    @Value("${accq.1871.inner.product.position.exchange}")
    private String positionExchange1871;

    //            两米活塞杆2号岛	        DZ-1872
    @Value("${accq.1872.inner.product.position.query}")
    private String positionQuery1872;
    @Value("${accq.1872.inner.product.position.routing}")
    private String positionRouting1872;
    @Value("${accq.1872.inner.product.position.exchange}")
    private String positionExchange1872;

    //            两米活塞杆3号岛	        DZ-1873
    @Value("${accq.1873.inner.product.position.query}")
    private String positionQuery1873;
    @Value("${accq.1873.inner.product.position.routing}")
    private String positionRouting1873;
    @Value("${accq.1873.inner.product.position.exchange}")
    private String positionExchange1873;

    //            两米活塞杆4号岛	        DZ-1874
    @Value("${accq.1874.inner.product.position.query}")
    private String positionQuery1874;
    @Value("${accq.1874.inner.product.position.routing}")
    private String positionRouting1874;
    @Value("${accq.1874.inner.product.position.exchange}")
    private String positionExchange1874;

    //            两米活塞杆5号岛	        DZ-1875
    @Value("${accq.1875.inner.product.position.query}")
    private String positionQuery1875;
    @Value("${accq.1875.inner.product.position.routing}")
    private String positionRouting1875;
    @Value("${accq.1875.inner.product.position.exchange}")
    private String positionExchange1875;

    //            两米活塞杆6号岛	        DZ-1876
    @Value("${accq.1876.inner.product.position.query}")
    private String positionQuery1876;
    @Value("${accq.1876.inner.product.position.routing}")
    private String positionRouting1876;
    @Value("${accq.1876.inner.product.position.exchange}")
    private String positionExchange1876;

    //            两米活塞杆7号岛	        DZ-1877
    @Value("${accq.1877.inner.product.position.query}")
    private String positionQuery1877;
    @Value("${accq.1877.inner.product.position.routing}")
    private String positionRouting1877;
    @Value("${accq.1877.inner.product.position.exchange}")
    private String positionExchange1877;

    //            三米活塞杆1号岛	        DZ-1878
    @Value("${accq.1878.inner.product.position.query}")
    private String positionQuery1878;
    @Value("${accq.1878.inner.product.position.routing}")
    private String positionRouting1878;
    @Value("${accq.1878.inner.product.position.exchange}")
    private String positionExchange1878;

    //            三米活塞杆2号岛	        DZ-1879
    @Value("${accq.1879.inner.product.position.query}")
    private String positionQuery1879;
    @Value("${accq.1879.inner.product.position.routing}")
    private String positionRouting1879;
    @Value("${accq.1879.inner.product.position.exchange}")
    private String positionExchange1879;

    //            三米活塞杆3号岛	        DZ-1880
    @Value("${accq.1880.inner.product.position.query}")
    private String positionQuery1880;
    @Value("${accq.1880.inner.product.position.routing}")
    private String positionRouting1880;
    @Value("${accq.1880.inner.product.position.exchange}")
    private String positionExchange1880;

    //            两米缸筒1号岛              DZ-1887
    @Value("${accq.1887.inner.product.position.query}")
    private String positionQuery1887;
    @Value("${accq.1887.inner.product.position.routing}")
    private String positionRouting1887;
    @Value("${accq.1887.inner.product.position.exchange}")
    private String positionExchange1887;

    //            两米缸筒2号岛	            DZ-1888
    @Value("${accq.1888.inner.product.position.query}")
    private String positionQuery1888;
    @Value("${accq.1888.inner.product.position.routing}")
    private String positionRouting1888;
    @Value("${accq.1888.inner.product.position.exchange}")
    private String positionExchange1888;

    //            两米缸筒3号岛	            DZ-1889
    @Value("${accq.1889.inner.product.position.query}")
    private String positionQuery1889;
    @Value("${accq.1889.inner.product.position.routing}")
    private String positionRouting1889;
    @Value("${accq.1889.inner.product.position.exchange}")
    private String positionExchange1889;

    //            三米缸筒1号岛	            DZ-1890
    @Value("${accq.1890.inner.product.position.query}")
    private String positionQuery1890;
    @Value("${accq.1890.inner.product.position.routing}")
    private String positionRouting1890;
    @Value("${accq.1890.inner.product.position.exchange}")
    private String positionExchange1890;

    //            三米缸筒2号岛	            DZ-1891
    @Value("${accq.1891.inner.product.position.query}")
    private String positionQuery1891;
    @Value("${accq.1891.inner.product.position.routing}")
    private String positionRouting1891;
    @Value("${accq.1891.inner.product.position.exchange}")
    private String positionExchange1891;

    //            两米活塞杆粗加工线	        DZ-1955
    @Value("${accq.1955.inner.product.position.query}")
    private String positionQuery1955;
    @Value("${accq.1955.inner.product.position.routing}")
    private String positionRouting1955;
    @Value("${accq.1955.inner.product.position.exchange}")
    private String positionExchange1955;

    //            三米活塞杆粗加工线	        DZ-1956
    @Value("${accq.1956.inner.product.position.query}")
    private String positionQuery1956;
    @Value("${accq.1956.inner.product.position.routing}")
    private String positionRouting1956;
    @Value("${accq.1956.inner.product.position.exchange}")
    private String positionExchange1956;

    private void handelReportWork(RabbitmqMessage rabbitmqMessage, String orderCode, String timestamp, String deviceCode) {
        if ("28".equals(deviceCode)) {
            Date date = DateUtil.stringDateToformatDate(timestamp);
            date.setTime(date.getTime() - 2 * 60 * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String format = dateFormat.format(date);
//                            ----
            RabbitmqMessage chMsg = new RabbitmqMessage();
            BeanUtils.copyProperties(rabbitmqMessage, chMsg);
            chMsg.setDeviceCode("M18");
            chMsg.setTimestamp(format);
            ReqWorkQrCodeOrder qrCodeCh = workingFlowService.processingData(chMsg);
            if (qrCodeCh != null) {
                if (QrCode.QR_CODE_IN.equals(qrCodeCh.getOutInputType())) {
                    qrCodeCh.setLineNo(rabbitmqMessage.getLineNo());
                    qrCodeCh.setOrderNo(orderCode);
                    mqService.saveReportWorkHistory(qrCodeCh);
                    ReqWorkQrCodeOrder qrCodeChOut = new ReqWorkQrCodeOrder();
                    BeanUtils.copyProperties(qrCodeCh, qrCodeChOut);
                    qrCodeChOut.setOutInputType(QrCode.QR_CODE_OUT);
                    qrCodeChOut.setStartTime(null);
                    qrCodeChOut.setCompleteTime(new Date());
                    mqService.saveReportWorkHistory(qrCodeChOut);
                }
            }
//                            ----
            RabbitmqMessage jzMsg = new RabbitmqMessage();
            BeanUtils.copyProperties(rabbitmqMessage, jzMsg);
            jzMsg.setDeviceCode("M19");
            jzMsg.setTimestamp(format);
            ReqWorkQrCodeOrder qrCodeJz = workingFlowService.processingData(jzMsg);
            if (qrCodeJz != null) {
                if (QrCode.QR_CODE_IN.equals(qrCodeJz.getOutInputType())) {
                    qrCodeJz.setOrderNo(orderCode);
                    qrCodeJz.setLineNo(rabbitmqMessage.getLineNo());
                    mqService.saveReportWorkHistory(qrCodeJz);
                    ReqWorkQrCodeOrder qrCodeJzOut = new ReqWorkQrCodeOrder();
                    BeanUtils.copyProperties(qrCodeJz, qrCodeJzOut);
                    qrCodeJzOut.setOutInputType(QrCode.QR_CODE_OUT);
                    qrCodeJzOut.setStartTime(null);
                    qrCodeJzOut.setCompleteTime(new Date());
                    mqService.saveReportWorkHistory(qrCodeJzOut);
                }
            }
        }
    }

    private void extracted(String msg) {
        RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
        String orderCode = rabbitmqMessage.getOrderCode();
        String timestamp = rabbitmqMessage.getTimestamp();
        String deviceCode = rabbitmqMessage.getDeviceCode();
        if ("DZ-1955".equals(orderCode) || "DZ-1956".equals(orderCode)) {
            handelReportWork(rabbitmqMessage, orderCode, timestamp, deviceCode);
        }
        ReqWorkQrCodeOrder qrCode = workingFlowService.processingData(rabbitmqMessage);
        if (qrCode != null) {
            try {
                qrCode.setLineNo(rabbitmqMessage.getLineNo());
                qrCode.setOrderNo(rabbitmqMessage.getOrderCode());
                deviceStatusPush.getWorkingFlow(qrCode);
            } catch (Throwable e) {
                log.error("发送报工数据到看板错误: ", e);
            }
            try {
                mqService.saveReportWorkHistory(qrCode);
            } catch (Throwable throwable) {
                log.error("发送数据到工控机的MOM服务进行报工失败: ", throwable);
            }
        }
        accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
    }

    /**
     * {"MessageId":"ea6b57744a94467bba430493e8dbbf01","QueueName":"dzics-dev-gather-v1-product-position","ClientId":"DZROBOT","OrderCode":"DZ-1875","LineNo":"1","DeviceType":"6","DeviceCode":"17","Message":"A815|[1,ASANY-20-OCT-21 13:57 3547]","Timestamp":"2021-10-20 13:26:33.5410"}
     * 根据订单号转发到对应的队列中去 由对应的队列处理报工数据
     */
    @RabbitListener(queues = "${accq.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_112);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            String orderCode = rabbitmqMessage.getOrderCode();
            switch (orderCode) {
                case "DZ-1871":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1871, positionRouting1871, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1871, positionRouting1871, positionQuery, msg);
                    break;
                case "DZ-1872":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1872, positionRouting1872, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1872, positionRouting1872, positionQuery, msg);
                    break;
                case "DZ-1873":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1873, positionRouting1873, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1873, positionRouting1873, positionQuery, msg);
                    break;
                case "DZ-1874":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1874, positionRouting1874, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1874, positionRouting1874, positionQuery, msg);
                    break;
                case "DZ-1875":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1875, positionRouting1875, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1875, positionRouting1875, positionQuery, msg);
                    break;
                case "DZ-1876":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1876, positionRouting1876, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1876, positionRouting1876, positionQuery, msg);
                    break;
                case "DZ-1877":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1877, positionRouting1877, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1877, positionRouting1877, positionQuery, msg);
                    break;
                case "DZ-1878":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1878, positionRouting1878, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1878, positionRouting1878, positionQuery, msg);
                    break;
                case "DZ-1879":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1879, positionRouting1879, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1879, positionRouting1879, positionQuery, msg);
                    break;
                case "DZ-1880":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1880, positionRouting1880, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1880, positionRouting1880, positionQuery, msg);
                    break;
                case "DZ-1887":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1887, positionRouting1887, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1887, positionRouting1887, positionQuery, msg);
                    break;
                case "DZ-1888":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1888, positionRouting1888, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1888, positionRouting1888, positionQuery, msg);
                    break;
                case "DZ-1889":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1889, positionRouting1889, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1889, positionRouting1889, positionQuery, msg);
                    break;
                case "DZ-1890":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1890, positionRouting1890, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1890, positionRouting1890, positionQuery, msg);
                    break;
                case "DZ-1891":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1891, positionRouting1891, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1891, positionRouting1891, positionQuery, msg);
                    break;
                case "DZ-1955":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1955, positionRouting1955, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1955, positionRouting1955, positionQuery, msg);
                    break;
                case "DZ-1956":
                    rabbitmqService.sendForwardPsotionQuery(positionExchange1956, positionRouting1956, msg);
                    log.info("根据订单转发到对应队列 : exchange: {},routing: {}, 数据来源队列:{}, 源数据: {}", positionExchange1956, positionRouting1956, positionQuery, msg);
                    break;
                default:
                    log.error("订单号未识别，转发失败：{},源数据：{}", orderCode, msg);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

    @RabbitListener(queues = "${accq.1871.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1871(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1871);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1871, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1871, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1871, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1872.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1872(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1872);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1872, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1872, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1872, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1873.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1873(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1873);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1873, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1873, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1873, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1874.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1874(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1874);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1874, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1874, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1874, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1875.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1875(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1875);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1875, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1875, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1875, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1876.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1876(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1876);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1876, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1876, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1876, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1877.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1877(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1877);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1877, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1877, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1877, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1878.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1878(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1878);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1878, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1878, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1878, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1879.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1879(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1879);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1879, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1879, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1879, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1880.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1880(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1880);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1880, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1880, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1880, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1887.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1887(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1887);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1887, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1887, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1887, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1888.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1888(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1888);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1888, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1888, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1888, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1889.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1889(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1889);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1889, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1889, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1889, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1890.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1890(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1890);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1890, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1890, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1890, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1891.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1891(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1891);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1891, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1891, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1891, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1955.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1955(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1955);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1955, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1955, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1955, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

    @RabbitListener(queues = "${accq.1956.inner.product.position.query}", containerFactory = "localContainerFactory")
    public void processingWorkReportData1956(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_MOM_DZ_1956);
        boolean flag = true;
        try {
            log.info("处理报工数据队列: {}, 数据 : {}", positionQuery1956, msg);
            lock.lock();
            extracted(msg);
            channel.basicAck(deliveryTag, true);
        } catch (BindQrCodeException e) {
            log.warn("处理报工信息,生产订单不存在,重新进入队列 : {}, 信息: {}", positionQuery1956, msg);
            channel.basicReject(deliveryTag, true);
            lock.unlock();
            flag = false;
            Thread.sleep(10000);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", positionQuery1956, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            if (flag) {
                lock.unlock();
            }
        }
    }

}

