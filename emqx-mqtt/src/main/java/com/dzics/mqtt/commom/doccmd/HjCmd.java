package com.dzics.mqtt.commom.doccmd;

/**
 * 焊接岛机器人指令值
 */
public class HjCmd {

    /**
     * 设备状态	Status	1：作业 2：待机 3：故障 4：关机 INT
     */
    public static String Status="a563";
    /**
     * 报警 	Alarm	0：正常 1：报警 INT
     */
    public static String Alarm="a566";
    /**
     * 工作模式 	Mode	1：自动 2：编辑 3：MDI 4： DNC 5：手轮 6:手动 INT
     */
    public static String Mode="a562";

    /**
     * 一轴关节坐标  FLOAT
     */
    public static String J1;
    public static String J2;
    public static String J3;
    public static String J4;
    public static String J5;
    /**
     * 六轴角度坐标
     */
    public static String J6;

    /**
     * UX	用户坐标系X坐标  FLOAT
     */
    public static String UX;
    public static String UY;
    public static String UZ;
    public static String UW;
    public static String UP;
    /**
     * UR	用户坐标系R坐标	 FLOAT
     */
    public static String UR;

    /**
     * WX	世界坐标系X坐标	 FLOAT
     */
    public static String WX;
    public static String WY;
    public static String WZ;
    public static String WW;
    public static String WP;
    /**
     * WR	世界坐标系R坐标	 FLOAT
     */
    public static String WR;
    /**
     * 程序运行状态	NcStatus 	NC 程序状态 STRING
     */
    public static String NcStatus;
    /**
     * 主程序名称	MainPgm 	当前加工主程序 STRING
     */
    public static String MainPgm="a591";
    /**
     * 当前程序名称 	CurPgm 	当前加工程序号 	STRING
     */
    public static String CurPgm="a593";
    /**
     * 作业时间	CycSec	程序作业时间 (秒)	INT
     */
    public static String CycSec;
    /**
     * 程序行号 	CurSeq 	当前执行的程序行号 	INT
     */
    public static String CurSeq="a595";
    /**
     * 报警信息 	AlarmMsg 	JSON 字符串 	STRING
     */
    public static String AlarmMsg;
    /**
     *送丝速度	WireSpeed	送丝速度 单位：m/min	FLOAT
     */
    public static String WireSpeed;
    /**
     * 焊接电流 	 Current 	焊接电流 单位：A	FLOAT
     */
    public static String Current;
    /**
     * 焊接电压	Voltage	焊接电压 单位：V 	FLOAT
     */
    public static String Voltage;
    /**
     * 寻位状态 	SearchSignal	寻位状态,0:非寻位1：寻位中	INT
     */
    public static String SearchSignal;
    /**
     * 清枪状态	ClearSignal	清枪状态，0：非清枪1：清枪中	INT
     */
    public static String ClearSignal;
    /**
     * 变位使能 	TurnPosEnb	变位使能，0：未使能1：使能	INT
     */
    public static String TurnPosEnb;
    /**
     * 焊弧状态检测	WeldDetect	焊弧起弧状态，0：未起弧 1：起弧	INT
     */
    public static String WeldDetect;
}
