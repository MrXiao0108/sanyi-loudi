package com.dzics.sanymom.server.netty.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MomOrderQrCode;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.service.DzProductService;
import com.dzics.common.service.MomOrderQrCodeService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.model.common.RobotReturnType;
import com.dzics.sanymom.server.domain.ChannelRepository;
import com.dzics.sanymom.server.domain.User;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.impl.mq.DzicsSendReportLocalImpl;
import com.dzics.sanymom.util.RedisUtil;
import com.dzics.sanymom.util.SnowflakeUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * event handler to process receiving messages
 *
 * @author Jibeom Jung akka. Manty
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class SimpleChatServerHandler extends ChannelInboundHandlerAdapter {
    @Value("${accq.product.position.query}")
    private String queryName;
    @Value("${accq.product.position.routing}")
    private String roiting;
    @Value("${accq.product.position.exchange}")
    private String exchange;

    @Value("${order.code}")
    private String orderNo;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private DzicsSendReportLocalImpl dzicsSendReportLocal;
    private final ChannelRepository channelRepository;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private MomOrderQrCodeService qrCodeService;
    @Autowired
    private DzProductService productService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        String socketAddress = ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0];
        if (log.isInfoEnabled()) {
            log.info("??????????????????:{}", socketAddress);
        }
        String stringMessage = (String) "login " + socketAddress;
        if (log.isDebugEnabled()) {
            log.debug(stringMessage);
        }
        User user = User.of(stringMessage, ctx.channel());
        user.login(channelRepository, ctx.channel());
        ctx.fireChannelActive();
        if (log.isInfoEnabled()) {
            log.info("Bound Channel Count is {}", this.channelRepository.size());
        }
    }

    /**
     * ?????????????????????
     * ??????;??????,?????????
     * AGV;M,X
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String stringMessage = String.valueOf(msg); //
        try {
            if (log.isInfoEnabled()) {
                log.info("???????????????-->:{}", stringMessage);
            }
            if (StringUtils.isEmpty(stringMessage)) {
                log.error("??????????????????????????????????????? {}", stringMessage);
                ctx.channel().writeAndFlush("ER");
                return;
            }
            String[] split = stringMessage.split(";");
            String code = split[0];
            if ("AGV".equals(code)){
//                 ??????AGV ??????????????????
                String runModel = cachingApi.getMomRunModel();
                int rm = -1;
                if (!StringUtils.isEmpty(runModel)){
                     rm = "auto".equals(runModel) ? 1 : 0;
                }
                String res = "AGV;M,"+rm;
                log.info("??????????????????AGV?????? {}", res);
                ctx.channel().writeAndFlush(res);
            }else if("URCODE".equals(code)){
                String res = "";
                //?????????????????????????????????
                List<MonOrder> list = momOrderService.list(new QueryWrapper<MonOrder>()
                        .eq("order_id", line.getOrderId())
                        .eq("line_id", line.getId())
                        .eq("ProgressStatus", 120)
                        .eq("order_operation_result", 2));
                if(CollectionUtils.isEmpty(list)){
                    res = "URCODE;M,"+RobotReturnType.number_three;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC???????????????????????????:{}", res);
                    return;
                }
                //URCODE;M  ?????????????????????
                if(stringMessage.contains(",")==false){
                    res = "URCODE;M,"+RobotReturnType.number_two;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC??????????????????????????????:{}",res);
                    return;
                }
                //URCODE;M,?????????  ????????????????????????????????????
                if(2!=stringMessage.split(",").length){
                    res = "URCODE;M,"+RobotReturnType.number_two;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC??????????????????????????????:{}",res);
                    return;
                }
                //?????????????????????????????????
                String qrCode = "";
                //????????????????????????????????????????????????????????????????????????
                if("DZ-1955".equals(line.getOrderNo()) || "DZ-1956".equals(line.getOrderNo())){
                    qrCode = stringMessage.split(",")[1];
                }else{
                    qrCode = stringMessage.split(",")[1];
                    if(qrCode.contains("_")){
                        qrCode = qrCode.split("_")[1];
                    }
                }
                //??????mom?????????????????????  ????????????????????????????????????????????????????????????
                String productAlias = list.get(0).getProductAlias();
                //???????????????????????????
                byte[] bytes = productAlias.getBytes(StandardCharsets.UTF_8);
                String substring = qrCode.substring(0, bytes.length);
                if(!substring.toUpperCase().equals(productAlias.toUpperCase())){
                    res = "URCODE;M,"+RobotReturnType.number_four;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC????????????????????????????????????:{}",res);
                    return;
                }
                MomOrderQrCode one = qrCodeService.getOne(new QueryWrapper<MomOrderQrCode>()
                        .eq("order_no",line.getOrderNo())
                        .eq("line_no",line.getLineNo())
                        .eq("product_code",qrCode));
                if(one != null){
                    res = "URCODE;M,"+RobotReturnType.number_two;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC????????????????????????:{}",res);
                    return;
                }
                res = "URCODE;M,1";
                ctx.channel().writeAndFlush(res);
                log.info("SimpleChatServerHandler [channelRead] DZDC????????????????????? {}",res);
            } else if("ORDER".equals(code)){
                String res = "";
                //ORDER;M  ?????????????????????
                if(stringMessage.contains(",")==false){
                    res = "ORDER;M,"+RobotReturnType.number_negative_one;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC?????????????????????????????????:{}",res);
                    return;
                }
                //ORDER;M,????????????  ????????????????????????????????????
                if(2!=stringMessage.split(",").length){
                    res = "ORDER;M,"+RobotReturnType.number_negative_one;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC?????????????????????????????????:{}",res);
                    return;
                }

                //??????????????????????????????
                String name = split[1].split(",")[1];
                //?????????????????????????????????????????????????????????????????????
                Object o = redisUtil.get(RedisKey.Now_Work_ProductAlias + line.getOrderNo() + line.getLineNo());
                if(o.toString().equals(name)){
                    log.info("SimpleChatServerHandler [channelRead] ????????????????????????????????????????????????????????????{}",name);
                    return;
                }

                String lineType = "";
//                ???????????????
                if("DZ-1871".equals(line.getOrderNo()) || "DZ-1872".equals(line.getOrderNo()) || "DZ-1873".equals(line.getOrderNo()) || "DZ-1874".equals(line.getOrderNo()) || "DZ-1875".equals(line.getOrderNo()) || "DZ-1876".equals(line.getOrderNo()) || "DZ-1877".equals(line.getOrderNo()) || "DZ-1955".equals(line.getOrderNo())){
                    lineType = "2MHSG";
                }
//                ???????????????
                if("DZ-1878".equals(line.getOrderNo()) || "DZ-1879".equals(line.getOrderNo()) || "DZ-1880".equals(line.getOrderNo()) || "DZ-1956".equals(line.getOrderNo())){
                    lineType = "3MHSG";
                }
                //????????????
                if("DZ-1887".equals(line.getOrderNo()) || "DZ-1888".equals(line.getOrderNo()) || "DZ-1889".equals(line.getOrderNo())){
                    lineType = "2MGT";
                }
                //????????????
                if("DZ-1890".equals(line.getOrderNo()) || "DZ-1891".equals(line.getOrderNo())){
                    lineType = "3MGT";
                }
                DzProduct product = productService.getOne(new QueryWrapper<DzProduct>().eq("line_type", lineType).eq("product_name", name));
                if(product != null){
                    //?????????????????????????????????????????????  W62B???W63B.......
                    MonOrder monOrder = momOrderService.getOne(new QueryWrapper<MonOrder>().eq("order_id", line.getOrderId()).eq("WipOrderNo", "DZICS-Manual"));
                    monOrder.setProductAlias(name);
                    monOrder.setProductId(String.valueOf(product.getProductId()));
                    monOrder.setProductNo(product.getProductNo());
                    boolean b = momOrderService.updateById(monOrder);
                    log.info("SimpleChatServerHandler [channelRead] ??????????????????????????????{}",name,b);
                    redisUtil.set(RedisKey.Now_Work_ProductAlias+line.getOrderNo()+line.getLineNo(),name);
                    res = "ORDER;M,"+RobotReturnType.number_one;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC?????????????????????????????????:{}",res);
                    return;
                }else{
                    //??????????????????????????????????????????????????????
                    res = "ORDER;M,"+RobotReturnType.number_negative_one;
                    ctx.channel().writeAndFlush(res);
                    log.info("SimpleChatServerHandler [channelRead] DZDC?????????????????????????????????:{}",res);
                    return;
                }

            }else if("WorkReport".equals(code)){
                Object workReportStatus = redisUtil.get(RedisKey.Work_Report_Status + line.getOrderNo() + line.getLineNo());
                String res = "WorkReport;M,"+String.valueOf(workReportStatus);
                ctx.channel().writeAndFlush(res);
                log.info("SimpleChatServerHandler [channelRead] ?????????????????????????????? {}",res);
                return;
            }
            else if("Polish".equals(code)){
                String qrCode;
                if(split[1].contains("_")){
                    qrCode = split[1].split("_")[1];
                }else{
                    qrCode = split[1];
                }
                Object o = redisUtil.get(RedisKey.Get_Polish_QrCode + line.getOrderNo());
                //?????????????????????????????????????????????????????????????????????????????????????????????
                if(StringUtils.isEmpty(o) || !qrCode.equals(String.valueOf(o))){
                    redisUtil.set(RedisKey.Get_Polish_QrCode+line.getOrderNo(),qrCode);
                }
                log.info("??????????????????????????????2?????????????????????????????????"+qrCode);
            }
            else {
                //{"ip":"192.168.16.100","lineNo":"2","message":"1","orderNo":"DZ-1819","port":1,"receiveDataSource":"ROB","sourceIp":"192.168.10.100","type":"1"}
                String ipPort = ctx.channel().remoteAddress().toString().replace("/", "");
//        ?????????????????????ip
                String socketAddress = ipPort.substring(0, ipPort.indexOf(":"));
                if (split.length != 2) {
                    log.error("?????????: ????????????????????????????????????{}", stringMessage);
                }
//        ?????????????????????????????????
                RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
                rabbitmqMessage.setMessageId(snowflakeUtil.nextId() + "");
                rabbitmqMessage.setQueueName(queryName);
                rabbitmqMessage.setClientId("rob:" + socketAddress);
                rabbitmqMessage.setOrderCode(orderNo);
                rabbitmqMessage.setLineNo("1");
                rabbitmqMessage.setDeviceType("6");
                rabbitmqMessage.setMessage("A815|[" + split[1] + "]");
//           code ?????????1?????????
                if (code.length()==1) {
                    code = "0"+code;
                }
                rabbitmqMessage.setDeviceCode(code);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String format = dateFormat.format(new Date());
                rabbitmqMessage.setTimestamp(format);
                boolean b = dzicsSendReportLocal.sendMq(rabbitmqMessage, roiting, exchange, queryName);
                ctx.channel().writeAndFlush("OK");
            }
        } catch (Throwable throwable) {
            ctx.channel().writeAndFlush("ER");
            log.error("???????????????????????????:{},???????????????{}", stringMessage, throwable.getMessage(), throwable);
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("????????????:{}", cause.getMessage(), cause);
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Assert.notNull(this.channelRepository, "[Assertion failed] - ChannelRepository is required; it must not be null");
        Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");
        User.current(ctx.channel()).logout(this.channelRepository, ctx.channel());
        if (log.isDebugEnabled()) {
            log.debug("Channel Count is " + this.channelRepository.size());
        }
    }
}