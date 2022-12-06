package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.SysCmdTcpMapper;
import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.TcpDescValue;
import com.dzics.common.model.custom.TcpValCmdName;
import com.dzics.common.model.entity.SysCmdTcp;
import com.dzics.common.service.impl.SysCmdTcpServiceImpl;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.AcquiSysCmdTcpService;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
@Service
@Slf4j
public class AcquiSysCmdTcpServiceImpl extends SysCmdTcpServiceImpl implements AcquiSysCmdTcpService {
    @Autowired
    SysCmdTcpMapper cmdTcpMapper;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 查询所有指令缓存到redis
     *
     * @return
     */
    @Override
    public void selectCmdTcpToRedis() {
        log.info("==========初始化指令数据到redis  开始===========");
        List<TcpValCmdName> cmdNameList = cmdTcpMapper.getTcpValueList();
        for (TcpValCmdName tcpValCmdName : cmdNameList) {
            String tcpValue = tcpValCmdName.getTcpValue();
            String cmdName = tcpValCmdName.getCmdName();
            String key = RedisKey.TCP_CMD_PREFIX + tcpValue;
            if (StringUtils.isEmpty(cmdName)) {
                List<TcpDescValue> descValue = new ArrayList<>();
                TcpDescValue value = new TcpDescValue();
                value.setDeviceItemValue("ERROR");
                value.setTcpDescription("ERROR");
                descValue.add(value);
                redisUtil.lSet(key, descValue);
                continue;
            } else {
                List<TcpDescValue> descValueY = cmdTcpMapper.getGroupType(cmdName);
                if (CollectionUtils.isEmpty(descValueY)) {
                    List<TcpDescValue> descValueX = new ArrayList<>();
                    TcpDescValue value = new TcpDescValue();
                    value.setDeviceItemValue("ERROR");
                    value.setTcpDescription("ERROR");
                    descValueX.add(value);
                    redisUtil.lSet(key, descValueX);
                } else {
                    redisUtil.lSet(key, descValueY);
                }
            }
        }
        log.info("==========初始化指令数据到redis  结束===========");
    }

    @Override
    public CmdTcp selectCmdTcpToRedis(String cmd, String cmdValue) {
        SysCmdTcp sysCmdTcp1 = cmdTcpMapper.selectOne(new QueryWrapper<SysCmdTcp>().eq("tcp_value", cmd));
        if (sysCmdTcp1 == null) {
            return null;
        }
        QueryWrapper<SysCmdTcp> wp = new QueryWrapper<>();
        wp.eq("group_type", sysCmdTcp1.getCmdName());
        wp.eq("device_item_value", cmdValue);
        List<SysCmdTcp> sysCmdTcps = cmdTcpMapper.selectList(wp);
        if (CollectionUtils.isNotEmpty(sysCmdTcps)) {
            if (sysCmdTcps.size() > 1) {
                log.error("查询指令:{},指令值：{} 存在多个", cmd, cmdValue);
            }
            SysCmdTcp sysCmdTcp = sysCmdTcps.get(0);
            CmdTcp cmdTcp = new CmdTcp();
            cmdTcp.setTcpValue(sysCmdTcp.getTcpValue());
            cmdTcp.setDeviceItemValue(sysCmdTcp.getDeviceItemValue());
            cmdTcp.setTcpDescription(sysCmdTcp.getTcpDescription());
            return cmdTcp;
        }
        return null;
    }

    @Override
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
