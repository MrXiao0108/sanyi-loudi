package com.dzics.mqtt.commom.doccmd;

/**
 * @Classname RclCmd
 * @Description 描述
 * @Date 2022/4/26 13:38
 * @Created by NeverEnd
 */
public class JzjCmd {
    /**
     * sanyi -> 设备状态	Status	        1：作业 2：待机 3：故障 4：关机	INT
     * dzdc ->  k562	运行状态         0 重启  1:停机  2:待机  3 生产  4 故障 , 5 关机
     */
    public static String Status = "k562";

    /**
     * 报警  0：正常 1：报警
     */
    public static String Alarm = "k565";


    /**
     * 连接状态
     * 0	脱机
     * 1	联机
     * 11	虚拟机
     */
    public static String connState = "k561";
    /**
     * 上辊行程
     */
    public static String RealLength = "k801";
    /**
     * 上辊角度
     */
    public static String RealAngle = "k802";
}
