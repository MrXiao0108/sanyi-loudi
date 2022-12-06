package com.dzics.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberUtils {
    /**
     * 验证Integer是否为空.
     *
     * @param obj
     * @return
     */
    public static boolean isEmptyInteger(Integer obj) {
        if (obj == null || obj.equals(0)) {
            return true;
        }
        return false;
    }

    /**
     * @param obj
     * @return
     */
    public static boolean isEmptyLong(Long obj) {
        if (obj == null || obj.equals(0)) {
            return true;
        }
        return false;
    }

    /**
     * 验证是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证是否为数字
     *
     * @param val
     * @return
     */
    public static boolean isNumeric(String tcpValue, String val) {
        for (int i = val.length(); --i >= 0; ) {
            int chr = val.charAt(i);
            if (chr < 48 || chr > 57) {
                log.error("接收到 {} 的值：{} 不是数字", tcpValue, val);
                return false;
            }
        }
        return true;
    }
}
