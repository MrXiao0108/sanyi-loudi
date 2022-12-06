package com.dzics.common.exception.enums;

public enum ExceptionMsgEnum {
    /**
     * 初始化redis分区块参数错误
     */
    ERR1("初始化redis分区块参数错误", "Error initializing redis partition block parameter"),
    /**
     * 设置GC缓存标志错误
     */
    ERR2("设置GC缓存标志错误", "Error setting GC cache flag");


    ExceptionMsgEnum(String chinese, String english) {
        this.chinese = chinese;
        this.english = english;
    }

    private String chinese;

    public String getChinese() {
        return chinese;
    }

    public String getEnglish() {
        return english;
    }

    private String english;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"chinese\":\"")
                .append(chinese).append('\"');
        sb.append(",\"english\":\"")
                .append(english).append('\"');
        sb.append('}');
        return sb.toString();
    }
}