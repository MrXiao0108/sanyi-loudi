package com.dzics.common.model.custom;

import lombok.Data;

import java.util.List;

/**
 * 二次回到队列处理
 *
 * @author ZhangChengJun
 * Date 2021/1/27.
 * @since
 */
@Data
public class SendBase {
    /**
     * 发送时间戳
     */
    private Long senDate;
    /**
     * 数据唯一键
     */
    private DzTcpDateID dzTcpDateID;


    /**
     * 指令信息
     */
    List<CmdTcp> cmdTcps;
}
