package com.dzics.common.enums;

/**
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since 数据是否展示
 */
public enum DetectorDataShow {
    /**
     * 不展示
     */
    OFF(1, "不展示"),
    /**
     * 展示
     */
    NO(0, "展示");

    DetectorDataShow(int code, String desc) {
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
