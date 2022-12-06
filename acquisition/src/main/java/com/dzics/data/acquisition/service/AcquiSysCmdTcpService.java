package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.TcpDescValue;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
public interface AcquiSysCmdTcpService {
    void selectCmdTcpToRedis();

    /**
     * @param cmd      指令
     * @param cmdValue 指令值
     * @return
     */
    CmdTcp selectCmdTcpToRedis(String cmd, String cmdValue);

    /**
     * 根据指令获取指令的 所有值类型
     * @param cmd  A562
     * @param cmdValue
     * @return
     */
    List<TcpDescValue> getCmdTcp(String cmd, String cmdValue);
}
