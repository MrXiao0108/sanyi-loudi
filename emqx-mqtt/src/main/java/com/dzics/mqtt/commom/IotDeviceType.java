package com.dzics.mqtt.commom;

public class IotDeviceType {
    /**
     * 搬运
     */
    public static final int BY = 1;
    public static final String BY_PRO_KEY = "CarryRobot";

    /**
     * 焊接岛机器人
     */
    public static final int HJ = 2;

    /**
     * 数控设备
     */
    public static final int SK = 3;
    public static final String SK_PRO_KEY = "CncMach";

    /**
     * 清洗机
     */
    public static final int QX = 4;
    public static final String QX_PRO_KEY = "AssemCleanMach";
    /**
     * 检测设备
     */
    public static final int JC = 5;
    public static final String JC_PRO_KEY = "DetectMach";

    /**
     * 热处理机 也就是 淬火机
     */
    public static final int RCL = 6;

    /**
     * 校直机
     */
    public static final int JZJ = 7;
}
