package com.dzics.mqtt.commom.doccmd;

/**
 * @Classname RclCmd
 * @Description 描述
 * @Date 2022/4/26 13:38
 * @Created by NeverEnd
 */
public class RclCmd {
    /**
     * sanyi -> 设备状态	Status	        1：作业 2：待机 3：故障 4：关机	INT
     * dzdc ->  H562	运行状态         0 重启  1:停机  2:待机  3 生产  4 故障 , 5 关机
     */
    public static String Status = "h562";

    /**
     * 报警  0：正常 1：报警
     */
    public static String Alarm = "h565";


    /**
     * 连接状态
     * 0	脱机
     * 1	联机
     * 11	虚拟机
     */
    public static String connState = "h561";
    /**
     * 感应器的输入电压 V
     */
    public static String InputVoltage = "h701";
    /**
     * 感应器的输入电流 A
     */
    public static String InputCurrent = "h702";
    /**
     * 输入电流频率Hz
     */
    public static String InputCurrentFreq = "h703";
    /**
     * 设定时间s
     */
    public static String SetTime = "h704";
    public static String ActualTime = "h705";
    /**
     * 移动速度  mm/s
     */
    public static String Speed = "h706";
    /**
     * 工件转速 Rad/min
     */
    public static String WorkpieceSpeed = "h707";
    /**
     *冷却液温度 ℃
     */
    public static String CoolTemp  = "h801";
    /**
     * 冷却超温报警
     */
    public static String CoolOverTempAlarm = "h802";
    /**
     * 冷却低温报警
     */
    public static String CoolLowTempAlarm = "h803";
    /**
     * 冷却液压力 MPa
     */
    public static String CoolPress = "h804";
    /**
     * 冷却液流量 L/s
     */
    public static String CoolFlow = "h805";
    /**
     * 设定冷却时间s
     */
    public static String SetCoo = "h806";
    /**
     * 实际冷却时间
     */
    public static String ActualCoolTime = "h807";
}
