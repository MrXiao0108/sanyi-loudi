package com.dzics.mqtt.commom.doccmd;

/**
 * 清洗机指令 值 就是机床
 */
public class QxCmd {
    /**
     * 设备状态 	Status 	1：作业 2：待机 3： 故障 4：关机	INT
     *  B562	运行状态         0 重启  1:停机  2:待机  3 生产  4 故障 , 5 关机
     */
    public static String Status  = "b562";
    /**
     * 报警 	Alarm 	0：正常 1：报警 	INT
     */
    public static String Alarm = "b569";
    /**
     * 清洗时长 	CleanTime 	单位：MIN 	Float
     */
    public static String CleanTime = "b527";

    /**
     * 连接状态
     * 0	脱机
     * 1	联机
     * 11	虚拟机
     */
    public static String connState = "b561";

    /**
     * 急停状态 0：未急停 、1：急停 、2：复位 、3：等待
     * */
    public static String stopState = "b568";

}
