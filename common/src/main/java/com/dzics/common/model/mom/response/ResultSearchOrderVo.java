package com.dzics.common.model.mom.response;

import lombok.Data;

@Data
public class ResultSearchOrderVo {
    /**
     * 版本   必填
     */
    private int version;
    /**
     * 任务ID  随机生成32位UUID，单次下发指令唯一标识   必填
     */
    private String taskId;
    /**
     * 返回结果   0：正确；其它：错误   必填
     */
    private String code;
    /**
     * 返回消息
     */
    private String msg;
    /**
     * 返回结果集
     */
    private SearchOrder returnData;

    /**
     * http 状态码
     */
    private Integer statusCode;
}
