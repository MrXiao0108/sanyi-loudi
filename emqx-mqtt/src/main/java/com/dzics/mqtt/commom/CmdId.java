package com.dzics.mqtt.commom;

/**
 * 指令标识
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */

public class CmdId {
    /**
     * 软件上线上报指令
     */
    public static final String cmdOnLine = "1";


    /**
     * 软件心跳上报 指令
     */
    public static final String cmdSignal = "3";
    /**
     *实时数据发布 指令
     */
    public static String realTimeData = "10";
}
