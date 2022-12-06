package com.dzics.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 设备类型静态类
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public enum EquiTypeEnum {
    //    设备类型(1检测设备,2机床,3机器人)
    JCSB(1, "检测设备"),
    JC(2, "机床"),
    JQR(3, "机器人"),
    XJ(4, "相机"),
    EQCODE(6, "工件位置设备"),
    AVG(7, "AGV"),
    CHJ(8,"淬火机"),
    JZJ(9,"校直机"),
    MEN(10,"门");


    EquiTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @EnumValue
    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
