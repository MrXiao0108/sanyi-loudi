package com.dzics.common.enums;

/**
 * 设备状态通用枚举
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public enum EquiTypeCommonEnum {
    /**
     * 复位
     */
    RESET(0, "复位"),
    /**
     *生产
     */
    PRO(1, "生产"),
    /**
     *停机
     */
    DOWN(2, "停机"),
    /**
     *维修
     */
    MAINTENANCE(3, "维修"),
    /**
     *故障
     */
    THE_FAULT(4, "故障"),
    /**
     *机器断开
     */
    MACHLINE_DISCONNECT(5, "机器断开"),
    /**
     *未连接
     */
    NOT_CONNECTED(6, "未连接"),
    /**
     *暂停
     */
    SUSPENDED(10, "暂停"),
    /**
     *告警
     */
    THE_ALREM(20, "告警"),
    /**
     *急停
     */
    SCRAM(21, "急停"),
    /**
     *空闲
     */
    FREE(30, "空闲"),
    /**
     *通讯断开
     */
    COMMUNICATION_DISCONNECT(100, "通讯断开"),
    /**
     *未知
     */
    THE_UNKONWN(-1, "未知");


    EquiTypeCommonEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
