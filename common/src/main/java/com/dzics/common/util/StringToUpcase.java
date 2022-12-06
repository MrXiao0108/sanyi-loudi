package com.dzics.common.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 字符串操作类
 *
 * @author ZhangChengJun
 * Date 2021/1/18.
 * @since
 */
public class StringToUpcase {
    public static String toUpperCase(String str) {
        String collect = Arrays.stream(str.split("\\/")).map(ss -> captureName(ss)).collect(Collectors.joining());
        return collect;
    }
    private static String captureName(String str) {
        if (str == null || str.equals("")) {
            return "";
        }
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }
}
