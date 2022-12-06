package com.dzics.mqtt.commom.doccmd;

/**
 * 检测设备指令值
 */
public class JcCmd {
    public static final String bas = "d";

    /**
     * sanyi -> 设备状态	Status	        1：作业 2：待机 3：故障 4：关机	INT
     * dzdc ->  B562	运行状态         0 重启  1:停机  2:待机  3 生产  4 故障 , 5 关机
     */
    public static final String Status = "b562";
    /**
     * 连接状态
     * 0	脱机
     * 1	联机
     * 11	虚拟机
     */
    public static String connState = "b561";

    public static final String ST0 = "0";
    public static final String ST1 = "1";
    public static final String ST2 = "2";
    public static final String ST3 = "3";
    public static final String ST4 = "4";
    public static final String ST5 = "5";
    /**
     * sany -》报警 	Alarm 	0：正常 1：报警
     * dzdc -》B569	告警状态 0 正常，其他 报警
     */
    public static String Alarm = "b569";

}
