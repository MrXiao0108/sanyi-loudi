package com.dzics.common.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class RunTimeDo {

    /**
     * 开始运行时间
     */
    private Date runTime;
    /**
     * 结束运行时间
     */
    private Date stopTime;
    /**
     * 运行时长
     */
    private Long sumTime;
}
