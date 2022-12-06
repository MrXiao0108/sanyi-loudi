package com.dzics.common.model.constant;

public class DzUdpType {
    /**
     * 处理交换吗
     */
    public static final String udpTypeQrcode = "1";
    /**
     * 处理页面点击来料信号，发送到UDP，当前收到的数据是UDP收到原路返还的回复数据
     */
    public static final String UDP_TYPE_AGV = "2";
    /**
     * Q,4,orderNo,lineNo,1100,小车,校验结果信息
     */
    public static final String UDP_LOGS = "4";//1100
    /**
     * 设备控制指令
     */
    public static final String UDP_CMD_CONTROL = "5";

    /**
     * 二维码填写动作
     */
    public static final String UDP_CMD_QR_CODE = "7";
    /**
     * 控制指令下发
     */
    public static final String UDP_CMD_CONTROL_INNER = "1200";
    /**
     * 终止
     */
    public static final String CONTROL_STOP = "1";
    /**
     * 开始
     */
    public static final String CONTROL_STAR = "2";
    /**
     * 暂停
     */
    public static final String CONTROL_STAR_STOP = "3";
    /**
     * 由暂停变为开始
     */
    public static final String CONTROL_STAR_STOP_START = "4";

//    =============================
    /**
     * 控制指令回传状态
     */
    public static final String UDP_CMD_CONTROL_UP = "1201";
    public static final String OK = "1";
    public static final String ERR = "0";
//  ================================

    /**
     * udp 回传数量指令值
     */
    public static final String UDP_CMD_CONTROL_SUM = "1202";

//  ================================
    /**
     * 号小车来料信号
     */
    public static final String udpTypeAgvSinal = "1001";
    public static final String udpTypeAgvSinalFRID = "1003";

    /**
     * 号小车确认物料信息
     */
    public static final String undpAgvConfirm = "1002";

    /**
     * 扫描FRID 指令
     */
    public static final String FRID_JSON = "1004";
    /**
     * 扫描到原始信息
     */
    public static final String FRID_OLD = "1005";
    /**
     * 上发需要填写信息指令
     */
    public static final String QR_CODE_INOUT = "1400";
    /**
     * 下发二维码信息指令
     */
    public static final String QR_CODE_RECEIVE_OK = "1401";

    /**
     *  写入结果回复
     */
    public static final String QR_CODE_INOUT_OK = "1402";

    public static final String MA_ER_BIAO = "6";
}
