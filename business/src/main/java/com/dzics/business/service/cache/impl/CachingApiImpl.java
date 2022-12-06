package com.dzics.business.service.cache.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.cache.CachingApi;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.SysCmdTcpMapper;
import com.dzics.common.model.custom.TcpDescValue;
import com.dzics.common.model.response.EquipmentListDo;
import com.dzics.common.service.SysCmdTcpService;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CachingApiImpl implements CachingApi {
    @Autowired
    SysCmdTcpService sysCmdTcpService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SysCmdTcpMapper cmdTcpMapper;

    @Override
    public String convertTcp(String cmd, String value) {
        if(StringUtils.isEmpty(value)){
            return null;
        }
        String key = RedisKey.TCP_CMD_PREFIX + cmd;
        List<TcpDescValue> cmdTcp = redisUtil.lGet(key, 0, -1);
        if (CollectionUtils.isEmpty(cmdTcp)) {
//                查询指令
            cmdTcp = getCmdTcp(cmd, value);
            redisUtil.lSet(key, cmdTcp);
            for (TcpDescValue tcpDescValue : cmdTcp) {
                String deviceItemValue = tcpDescValue.getDeviceItemValue();
                String tcpDescription = tcpDescValue.getTcpDescription();
                if (value.equals(deviceItemValue)) {
                    return tcpDescription;
                }
            }
        } else {
            for (TcpDescValue tcpDescValue : cmdTcp) {
                String deviceItemValue = tcpDescValue.getDeviceItemValue();
                String tcpDescription = tcpDescValue.getTcpDescription();
                if ("ERROR".equals(tcpDescription)) {
                    return value;
                }
                if (value.equals(deviceItemValue)) {
                    return tcpDescription;
                }
            }

        }
        return null;
    }

    public List<TcpDescValue> getCmdTcp(String cmd, String cmdValue) {
        String cmdName = cmdTcpMapper.getTcpValue(cmd);
        if (StringUtils.isEmpty(cmdName)) {
            log.warn(" 查询指令不存在:cmd: {},cmdName：{}", cmd, cmdName);
            List<TcpDescValue> objects = new ArrayList<>();
            TcpDescValue descValue = new TcpDescValue();
            descValue.setDeviceItemValue("ERROR");
            descValue.setTcpDescription("ERROR");
            objects.add(descValue);
            return objects;
        }
        List<TcpDescValue> descValue = cmdTcpMapper.getGroupType(cmdName);
        if (CollectionUtils.isEmpty(descValue)) {
            log.warn("cmdName:{} 查询指令不存在:descValue: {}", cmdName, descValue);
            List<TcpDescValue> objects = new ArrayList<>();
            TcpDescValue descValueX = new TcpDescValue();
            descValueX.setDeviceItemValue("ERROR");
            descValueX.setTcpDescription("ERROR");
            objects.add(descValueX);
            return objects;
        }
        return descValue;
    }
}
