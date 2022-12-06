package com.dzics.data.acquisition.cousomer;

import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.data.acquisition.config.redis.RedisPrefxKey;
import com.dzics.data.acquisition.service.*;
import com.dzics.data.acquisition.service.qrcode.QrcodeService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * udp数据处理消费
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class UdpDataCousomer {

    @Autowired
    private AccStorageLocationService accStorageLocationService;
    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Autowired
    private MomOrderAgvService momOrderAgvService;
    @Autowired
    private AccDzWorkpieceDataService accDzWorkpieceDataService;
    @Autowired
    private QrcodeService qrcodeService;
    @Autowired
    public RedissonClient redissonClient;
    @Value("${accq.product.qrode.up.udp.query}")
    private String bindingCheckCode;
    @Value("${accq.product.qrode.up.udp.query.dead}")
    private String bindingCheckCodeDead;
    @Autowired
    public BindingQRCodeService bindingqrcodeservice;




    /**
     * UDP上发到队列中的数据
     * 例如：获取检测数据交换码 队列
     * 例如：FRID 原始信息 ，扫描FRID信息 等
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.product.qrode.up.udp.query}", containerFactory = "localContainerFactory")
    public void getAuthCode(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费队列: {}  , 消息: {}", bindingCheckCode, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_021);
        try {
            lock.lock();
            if (!StringUtils.isEmpty(msg)) {
                String[] split = msg.split(",");
                if (split.length > 3) {
                    String spl = split[1];
                    switch (spl) {
                        case DzUdpType.UDP_TYPE_AGV:
//                          点击来料信号相关数据
                            log.info("来料信号相关数据：{}", msg);
                            String cmdTypeInner = split[2];
                            if (cmdTypeInner.equals(DzUdpType.FRID_JSON)) {
//                              识别出FRID 信息
                                deviceStatusPush.pushFrdiJson(split);
                            } else if (cmdTypeInner.equals(DzUdpType.FRID_OLD)) {
//                              FRID 原始信息
                                deviceStatusPush.pushFridOld(split);
                            } else {
//                              UDP回复确认收到来料信号
                                SysRealTimeLogs sysRealTimeLogs = accStorageLocationService.getDzRealTimelogs(split);
                                if (sysRealTimeLogs == null) {
                                    log.warn("生成UDP回复->收到 页面下发来料信号格式错误  message :{}", msg);
                                } else {
                                    deviceStatusPush.sendSysRealTimeLogs(sysRealTimeLogs);
                                }
                            }
                            break;
                        case DzUdpType.UDP_LOGS:
//                          Q,4,orderNo,lineNo,1100,小车,校验结果信息,机器人回复校验信息结果
                            log.info("机器人回复校验信息结果信息：{}", msg);
                            SysRealTimeLogs sysRealTimeLogs = accStorageLocationService.getDzRealTimeRoblogs(split);
                            if (sysRealTimeLogs == null) {
                                log.warn("生成UDP回复->收到 页面下发来料信号格式错误  message :{}", msg);
                            } else {
                                deviceStatusPush.sendSysRealTimeLogs(sysRealTimeLogs);
                            }
                            break;
                        case DzUdpType.UDP_CMD_CONTROL:
                            log.info("机器人回复开始订单,设备控制指令信息: {}", msg);
                            momOrderAgvService.udpCmdControl(split);
                            break;
                        case DzUdpType.UDP_CMD_QR_CODE:
                            log.info("接收手动填写二维码信息: {}", msg);
                            qrcodeService.qrcodeControl(split);
                            break;
                        case DzUdpType.MA_ER_BIAO:
                            log.info("处理码儿表检测数据: message: {}", msg);
                            DzWorkpieceData maerbiao = accDzWorkpieceDataService.maerbiao(split);
                            try {
                                boolean isOk2 = deviceStatusPush.sendWorkpieceData(maerbiao);
                            } catch (Throwable throwable) {
                                log.error("码儿表检测数据重新发送到页面发送错误：", throwable);
                            }
                            //此处，处理接收到到码儿表检测数据进行前端检测记录 推送
                            try {
                                boolean iok = deviceStatusPush.sendDetection(maerbiao.getOrderNo(), maerbiao.getLineNo(), maerbiao.getId());
                            }catch(Throwable throwable){
                                log.error("人工打磨检测记录推送异常：{}", throwable.getMessage(), throwable);
                            }
                            try {
                                boolean isOk = deviceStatusPush.sendSingleProbe(maerbiao);
                            } catch (Throwable throwable) {
                                log.error("单项产品检测推送前端错误：", throwable);
                            }
                            try {
                                boolean isOk1 = deviceStatusPush.sendDetectionMonitor(maerbiao.getOrderNo(),maerbiao.getLineNo(),maerbiao.getProducBarcode());
                            }catch(Throwable throwable){
                                log.error("人工打磨台检测数据追踪 推送看板异常",throwable);
                            }
                            break;
                        default:
                            log.warn("队列UDP信号不是别类型：DzUdpType:{} ,message: {}", spl, msg);
                            break;
                    }
                } else {
                    log.warn("UDP上发数据根据逗号分隔长度不应该小于3");
                }
            } else {
                log.warn("UDP上发数据不存在：msg：{}", msg);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列: {} ,消息: {}", bindingCheckCode, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

    /**
     * UDP上发到队列中的数据,处理超时的队列数据
     * 例如：获取检测数据交换码 队列
     * 例如：FRID 原始信息 ，扫描FRID信息 等
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.product.qrode.up.udp.query.dead}", containerFactory = "localContainerFactory")
    public void getAuthCodeDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("消费队列: {}  超时消息  , 消息: {}", bindingCheckCodeDead, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_021);
        try {
            lock.lock();
            if (!StringUtils.isEmpty(msg)) {
                String[] split = msg.split(",");
                if (split.length > 3) {
                    String spl = split[1];
                    switch (spl) {
                        case DzUdpType.UDP_TYPE_AGV:
                            String cmdTypeInner = split[2];
                            if (cmdTypeInner.equals(DzUdpType.FRID_JSON)) {
//                             识别出FRID 信息
                                log.warn("识别出 FRID 信息 处理超时:{}", msg);
                            } else if (cmdTypeInner.equals(DzUdpType.FRID_OLD)) {
//                             FRID 原始信息
                                log.warn("识别出 FRID 原始信息 处理超时:{}", msg);
                            } else {
//                             处理页面点击来料信号，发送到UDP，当前收到的数据是UDP收到原路返还的回复数据
                                log.warn("处理页面点击来料信号，发送到UDP，当前收到的数据是UDP收到原路返还的回复数据 处理超时:{}", msg);
                            }
                            break;
                        case DzUdpType.UDP_LOGS:
                            log.warn("处理UDP回复->收到 页面下发来料信号 处理超时 message :{}", msg);
                            break;
                        case DzUdpType.UDP_CMD_CONTROL:
                            log.warn("机器人回复开始订单指令信息,或二维码绑定订单 处理超时: {}", msg);
                            break;
                        case DzUdpType.UDP_CMD_QR_CODE:
                            log.warn("接收扫码信息,触发写入二维码，和下发二维码回复结果 。 指令处理超时: {}", msg);
                            break;
                        case DzUdpType.MA_ER_BIAO:
                            log.warn("处理码儿表检测数据 超时处理: message: {}", msg);
                            DzWorkpieceData maerbiao = accDzWorkpieceDataService.maerbiao(split);
                            break;
                        default:
                            log.warn("队列UDP信号不是别类型 超时处理：DzUdpType:{} ,message: {}", spl, msg);
                            break;
                    }
                } else {
                    log.warn("死亡队列 UDP上发数据根据逗号分隔长度不应该小于3");
                }
            } else {
                log.warn("UDP上发数据不存在：msg：{}", msg);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列: {} ,消息: {}", bindingCheckCodeDead, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }


}
