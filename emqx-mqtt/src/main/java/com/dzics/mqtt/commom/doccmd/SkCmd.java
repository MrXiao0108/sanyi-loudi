package com.dzics.mqtt.commom.doccmd;

/**
 * 数控设备指令值
 */
public class SkCmd {
    /**
     * 急停   0：正常 1：急停
     * B568	急停状态
     */
    public static String Emg = "b568";

    /**
     * 报警  0：正常 1：报警
     */
    public static String Alarm = "b569";
    /**
     * 连接状态
     * 0	脱机
     * 1	联机
     * 11	虚拟机
     */
    public static String connState = "b561";
    /**
     * 工作模式 1：自动 2：编辑 3：MDI 4： DNC 5：手轮 6:手动
     */
    public static String Mode = "b565";

    /**
     * 轴名称  各轴以‘|’进行分割
     */
    public static String AxisName = "b406";

    /**
     * 机械坐标 各轴以‘|’进行分割
     */
    public static String MachPos = "b502";

    /**
     * 绝对坐标 AbsPos 各轴以‘|’进行分割
     */
    public static String AbsPos = "b501";

    /**
     * 程序运行状态 NcStatus NC 程序状态
     */
    public static String NcStatus = "b591";

    /**
     * 主程序号 MainPgm 当前加工主程序
     */
    public static String MainPgm = "b592";

    /**
     * 当前程序号 CurPgm  当前加工程序号
     */
    public static String CurPgm = "b594";

    /**
     * 加工时间 CycSec   程序加工时间(秒)
     */
    public static String CycSec = "b527";

    /**
     * 程序行号 	CurSeq 	当前执行的程序行号
     */
    public static String CurSeq = "b596";
    /**
     * 产量 	PartCnt 	当前的产量
     */
    public static String PartCnt = "b693";
    /**
     * 切削倍率 	OvFeed 	切削倍率%
     */
    public static String OvFeed = "b552";
    /**
     * 主轴倍率 	OvSpin 	主轴倍率%
     */
    public static String OvSpin = "b553";
    /**
     * 切削速度 	ActFeed 	切削速度 F 单位：m/min
     */
    public static String ActFeed = "b554";
    /**
     * 切削指定速度 	FCode 	指定的转速 F 单位：m/min
     */
    public static String FCode = "b555";
    /**
     * 主轴速度 	ActSpin 	主轴转速 S 单位：r/min
     */
    public static String ActSpin = "b551";
    /**
     * 主轴指定转速	SCode 	指定的主轴转速 单位：r/min
     */
    public static String SCode = "b556";

    /**
     * 刀具号 T
     */
    public static String TCode = "b651";

    /**
     * 当前程序块 	CurNcBlk 	当前正在加工程序代码(50 字符以内)
     */
    public static String CurNcBlk = "b597";

    /**
     * 切削时间 	CutTime	程序切削时间(秒)
     */
    public static String CutTime = "b526";
    /**
     * 报警信息 	AlarmMsg	JSON 字符串 	STRING
     */
    public static String AlarmMsg;

    /**
     * 相对坐标
     */
    public static String RelPos = "b504";

    /**
     * 剩余距离
     */
    public static String RemPos = "b505";
    /**
     * 主轴最高转速
     */
    public static String MaxSpeed = "b557";
    /**
     * 主轴数
     */
    public static String SpinNum = "b405";
    /**
     * 伺服轴数
     */
    public static String Axes = "b404";

    /**
     * 主轴负载 1
     */
    public static String SpinLoad1 = "b801";
    /**
     * 伺服温度
     */
    public static String SvTemp = "b811";
    /**
     * 伺服负载（X/Z轴）
     */
    public static String SvLoad = "b807";
    /**
     * 主轴温度 第一主轴温度
     */
    public static String SpinTemp1 = "b810";
    /**
     * 软件版本
     */
    public static String NcVer = "b403";
}
