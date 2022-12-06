package com.dzics.common.model.constant;

import lombok.Data;

/**
 * AGV 料框 类型定义
 */
@Data
public class AgvPalletType {

    /**
     * 料框 满料拖出  1
     */
    public static final String A = "料框满料拖出";
    /**
     * 料框 满料拖出  1
     */
    public static final String ANU = "1";
    public static final String B = "料框空料拖出";
    /**
     * 料框 空料拖出  2
     */
    public static final String BNU = "2";
    public static final String C = "料框清料拖出";
    /**
     * 料框 青料拖出  3
     */
    public static final String CNU = "3";
    public static final String D = "料框请求毛坯";
    /**
     * 料框 请求毛坯  4
     */
    public static final String DNU = "4";
    public static final String E = "料框AGV到位";
    /**
     * 料框 AGV 到位  5
     */
    public static final String ENU = "5";
    public static final String F = "料框AGV离开";
    /**
     * 料框 AGV 离开  6
     */
    public static final String FNU = "6";
    public static final String G = "料框空料推入";
    /**
     * 料框 空料推入   7
     */
    public static final String GNU = "7";

    public static final String H = "未识别呼叫AGV类型";
    /**
     * 未被别类型
     */
    public static final String HNU = "8";

}
