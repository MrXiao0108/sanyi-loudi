package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.BindingQRCodeService;
import com.dzics.data.acquisition.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BindingQRCodeServiceImpl implements BindingQRCodeService {

    @Autowired
    DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result processingData(RabbitmqMessage rabbitmqMessage) {

        String[] split = rabbitmqMessage.getMessage().split("\\|");
        String tcp = split[0];
        String msg = split[1];
        if ("A816".equals(tcp)) {
            String replace = msg.replace("[", "").replace("]", "");
            String[] data = replace.split(",");
            String authCode = data[0];//交换码
            String qrCode = data[1];//二维码
            PageHelper.startPage(1, 1);
            QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<DzWorkpieceData>()
                    .eq("auth_code", authCode)
                    .orderByDesc("detector_time");
            List<DzWorkpieceData> dataList = dzWorkpieceDataMapper.selectList(wrapper);
            PageInfo<DzWorkpieceData> list = new PageInfo<>(dataList);
            if (list.getList().size() != 1) {
                log.warn("交换码绑定的数据不存在:{}", authCode);
                return null;
            }
            DzWorkpieceData dzWorkpieceData = list.getList().get(0);
            if (dzWorkpieceData == null) {
                log.warn("交换码绑定的数据不存在:{}", authCode);
                return null;
            }
            dzWorkpieceData.setQrCode(qrCode);
            dzWorkpieceDataMapper.updateById(dzWorkpieceData);
            return Result.ok(dzWorkpieceData);
        } else {
            log.warn("检测数据 绑定二维码队列,指令不准确:{}", tcp);
        }

        return null;
    }

    @Override
    public DzWorkpieceData createAuthCode(String rabbitmqMessage) {
        if (rabbitmqMessage == null) {
            log.warn("获取交换码队列，消息为null:{}", rabbitmqMessage);
            return null;
        }
        String[] split = rabbitmqMessage.split(",");
        if (split.length != 5) {
            if (split.length == 3) {
                String type = split[1];
                if (type.equals(FinalCode.UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE)) {
                    String falg = split[2];
                    if (falg.equals(FinalCode.UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE_VALUE_OK)) {
                        redisUtil.set(RedisKey.WORKPIECE_EXCHANGE_RESET, FinalCode.UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE_VALUE_OK_REST);
                    }
                } else {
                    log.warn("重置标类型错误：{}", rabbitmqMessage);
                }
                return null;
            } else {
                log.warn("获取交换码队列，消息格式不准确，解析后的元素长度不等于5 且不等于 3:{}", split);
                return null;
            }

        }
        String id = split[3];//记录id
        String authCode = split[4];//交换码
        //校验交换码是否存在
        List<DzWorkpieceData> auth_code = dzWorkpieceDataMapper.selectList(new QueryWrapper<DzWorkpieceData>().eq("auth_code", authCode));
        if(auth_code.size()>0){
            //未找到指定的检测记录
            log.warn("获取交换码队列，交换码已绑定数据：{}", rabbitmqMessage);
            return null;
        }
        DzWorkpieceData dzWorkpieceData = dzWorkpieceDataMapper.selectById(id);
        if (dzWorkpieceData == null) {
            //未找到指定的检测记录
            log.warn("获取交换码队列，未找到指定id的检测记录：{}", rabbitmqMessage);
            return null;
        }
        dzWorkpieceData.setAuthCode(authCode);
        dzWorkpieceDataMapper.updateById(dzWorkpieceData);
        return dzWorkpieceData;
    }
}
