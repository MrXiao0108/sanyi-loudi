package com.dzics.mqtt.commom.doccmd;

/**
 * 搬运机器人指令值
 */
public class ByCmd {

    /**
     * 连接状态
     */
    public static String connState = "a561";
    /**
     * 1：作业 2：待机 3：故障 4：关机
     */
    public static String Status="a563";
    /**
     * 报警 	Alarm	0：正常 1：报警
     */
    public static String Alarm="a566";

    /**
     * 待机状态    1:待机  0：正常
     * */
    public static String Standby="a567";

    /**
     * 工作模式 	Mode	1：自动 2：编辑 3：MDI 4： DNC 5：手轮 6:手动
     * 0 自动
     * 1 手动
     * 3 手动全速
     * 2 手动
     * 4 手动
     * 5 手动
     * 6 示教
     * 12 力控
     * 13 扭矩
     * 100 未知
     */
    public static String Mode="a562";
//    ======================================
    /**
     * J1	一轴关节坐标
     */
    public static String J1 = "a501";

//    ========================================
    /**
     * 用户坐标系X坐标
     */
    public static String UX = "a504";


// ==============================================
    /**
     * 世界坐标系X坐标
     */
    public static String WX = "a502";


//=============================================
    /**
     * 程序运行状态	NcStatus 	NC 程序状态
     */
    public static String NcStatus = "a563";
    /**
     * 当前程序名称 	CurPgm 	当前加工程序号
     */
    public static String CurPgm="a593";
    /**
     * 作业时间	CycSec	程序作业时间 (秒)
     */
    public static String CycSec ="a802";
    /**
     * 程序行号 	CurSeq 	当前执行的程序行号
     */
    public static String CurSeq="a595";
    /**
     * 报警信息 	AlarmMsg 	JSON 字符串
     */
    public static String AlarmMsg = "w101";
    /**
     * 夹具状态	FixState 	 0：打开 1：关闭
     */
    public static String FixState="a620";

    /**
     * 当前加工主程序
     */
    public static String MainPgm = "a591";
    /**
     * 急停信号
     */
    public static String Emg = "a565";
}
