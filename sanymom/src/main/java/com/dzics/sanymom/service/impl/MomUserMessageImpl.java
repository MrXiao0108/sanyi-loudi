package com.dzics.sanymom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.MomMaterialPointMapper;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MomUser;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.request.mom.MomUserLogin;
import com.dzics.common.model.request.mom.OperationOrderVo;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.MaterialPointStatus;
import com.dzics.common.model.response.mom.MomAuthOrderRes;
import com.dzics.common.model.response.mom.UserLoginMessage;
import com.dzics.common.service.MomUserService;
import com.dzics.common.util.PageLimitBase;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.model.request.OpenWork;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.MomUserMessage;
import com.dzics.sanymom.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Service
@Slf4j
public class MomUserMessageImpl implements MomUserMessage {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${business.robot.ip}")
    private String busIpPort;

    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomUserService momUserService;
    @Value("${order.code}")
    private String orderCodeSys;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MomOrderMapper momOrderMapper;

    @Autowired
    private MomMaterialPointMapper pointMapper;

    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result login(MomUserLogin login) {
        String employeeNo = login.getEmployeeNo();
        String type = login.getType();
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String orderNo = line.getOrderNo();
        String lineNo = line.getLineNo();
        if ("UP".equals(type)) {
            String orderId = line.getOrderNo();
            String lineId = line.getLineNo();
            MomUser loginOk = momUserService.getLineIsLogin(orderId, lineId, orderCodeSys);
            if (loginOk != null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR90);
            }
        }
        MomUser momUser = momUserService.getEmployeeNo(employeeNo);
        if (momUser == null) {
            throw new CustomException(CustomExceptionType.USER_IS_NULL);
        }
        Result ok = Result.ok();
        if ("UP".equals(type)) {
            momUser.setLoginOrderNo(orderNo);
            momUser.setLoginLineNo(lineNo);
            momUser.setLoginState(true);
            ok.setMsg("登录成功");
            ok.setData(momUser);
        } else if ("LO".equals(type)) {
            momUser.setLoginOrderNo(null);
            momUser.setLoginLineNo(null);
            momUser.setLoginState(null);
            ok.setMsg("退出成功");
        } else {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        momUserService.updateByIdCahce(momUser, orderCodeSys);
        return ok;
    }

    @Override
    public Result getUseLineMsg() {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        return Result.OK(line.getLineName());
    }

    @Override
    public Result getUserMessage() {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String orderId = line.getOrderNo();
        String lineId = line.getLineNo();
        MomUser loginOk = momUserService.getLineIsLogin(orderId, lineId, orderCodeSys);
        UserLoginMessage loginMessage = new UserLoginMessage();
        loginMessage.setLineName(line.getLineName());
        loginMessage.setMomUser(loginOk);
        return Result.OK(loginMessage);
    }

    @Override
    public Result<MomUser> getUseLineIslogin() {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String orderId = line.getOrderNo();
        String lineId = line.getLineNo();
        MomUser loginOk = momUserService.getLineIsLogin(orderId, lineId, orderCodeSys);
        return Result.OK(loginOk);

    }

    @Override
    public Result offNoOpenWork() {
        Object o = redisUtil.get(RedisKey.OPEN_SERIALNOS + orderCodeSys);
        if (o == null) {
            return Result.ok(false);
        }
        return Result.ok(o);
    }

    @Override
    public Result offNoOpen(OpenWork openWork) {
        Object o = redisUtil.set(RedisKey.OPEN_SERIALNOS + orderCodeSys, openWork.getOpenClose());
        return Result.ok();
    }

    @Override
    public Result getMomAuthOrderRes(PageLimitBase pageLimitBase) {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        Long orderId = line.getOrderId();
        Long lineId = line.getId();
        PageHelper.startPage(pageLimitBase.getPage(), pageLimitBase.getLimit());
        List<MomAuthOrderRes> res = momOrderMapper.getMomAuthOrderRes(orderId, lineId);
        PageInfo<MomAuthOrderRes> info = new PageInfo<>(res);
        return Result.ok(info.getList(), info.getTotal());
    }

    @Override
    public Result getMaterialPointStatus() {
        List<MaterialPointStatus> pointStatus = pointMapper.getMaterialPointStatus(orderCodeSys);
        if (CollectionUtils.isNotEmpty(pointStatus)) {
            for (MaterialPointStatus materialPointStatus : pointStatus) {
                String pointCode = materialPointStatus.getPointCode();
                if (!StringUtils.isEmpty(pointCode)) {
                    String status = (String) redisUtil.get(RedisKey.MATERIAL_POINT_STATUS + pointCode);
                    materialPointStatus.setStatus(status);
                }
            }
        }
        return Result.ok(pointStatus);

    }

    @Override
    public Result operationOrder(OperationOrderVo operationOrderVo) {
        MonOrder order = momOrderMapper.selectById(operationOrderVo.getProTaskOrderId());
        if(order==null){
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR52.getChinese());
        }
        if (StringUtils.isEmpty(operationOrderVo.getProgressStatus())) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR12.getChinese());
        }
        String runModel = cachingApi.getMomRunModel();
        if (StringUtils.isEmpty(runModel)) {
            throw new CustomException(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR95);
        }
        //非自动模式下不允许手动操作订单
        if("auto".equals(runModel)){
            //自动模式下不允许操作手动订单
            if("DZICS-Manual".equals(order.getWiporderno())){
                throw new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR971.getChinese());
            }
        }else{
            //手动模式下不允许操作自动订单
            if(!"DZICS-Manual".equals(order.getWiporderno())){
                throw new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR98.getChinese());
            }
        }
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        PutMomOrder putMomOrder = new PutMomOrder();
        putMomOrder.setProTaskOrderId(operationOrderVo.getProTaskOrderId());
        putMomOrder.setLineId(line.getId().toString());
        putMomOrder.setProgressStatus(String.valueOf(operationOrderVo.getProgressStatus()));
        putMomOrder.setTransPondKey("Dzics-MomUser");
        //开始订单
        if(MomProgressStatus.LOADING.equals(operationOrderVo.getProgressStatus())){
            //判断有没有操作在进行中
            QueryWrapper<MonOrder> wp = new QueryWrapper<>();
            wp.eq("line_id", line.getId());
            wp.and(wapper -> wapper.eq("ProgressStatus", MomProgressStatus.LOADING)
                    .or().eq("ProgressStatus", MomProgressStatus.STOP)
                    .or().eq("order_operation_result", 1));
            List<MonOrder> list = momOrderMapper.selectList(wp);
            if (CollectionUtils.isNotEmpty(list)) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR51.getChinese());
            }
            //调用主服务器业务端 接口
            String url = busIpPort + "/api/mom/user/start/order";
            try {
                restTemplate.put(url, putMomOrder, Result.class);
                log.info("MomUserMessageImpl [operationOrder] 员工开始订单，订单号：{}",order.getWiporderno());
                return Result.ok();
            } catch (Exception e) {
                log.error("MomUserMessageImpl [operationOrder] 员工开始订单请求转发异常,订单号:{}", order.getWiporderno());
                e.printStackTrace();
                return Result.error(CustomExceptionType.SYSTEM_ERROR,CustomResponseCode.ERR0);
            }
        }else if(MomProgressStatus.CLOSE.equals(operationOrderVo.getProgressStatus())){
                //判断当前订单的操作是否完成
                MonOrder monOrder1 = momOrderMapper.selectById(putMomOrder.getProTaskOrderId());
                if (monOrder1 == null) {
                    return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
                }
                if (monOrder1.getOrderOperationResult().intValue() == 1) {
                    return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
                }
                //调用主服务器业务端 接口
                String url = busIpPort + "/api/mom/user/close/order";
                try {
                    restTemplate.put(url, putMomOrder, Result.class);
                    log.info("MomUserMessageImpl [operationOrder] 员工强制关闭订单，订单号：{}",order.getWiporderno());
                    return Result.ok();
                } catch (Exception e) {
                    log.error("MomUserMessageImpl [operationOrder] 员工强制关闭订单请求转发异常,订单号:{}", order.getWiporderno());
                    e.printStackTrace();
                    return Result.error(CustomExceptionType.SYSTEM_ERROR,CustomResponseCode.ERR0);
                }
        }
        return new Result(CustomExceptionType.Parameter_Exception,CustomResponseCode.ERR12);
    }


}
