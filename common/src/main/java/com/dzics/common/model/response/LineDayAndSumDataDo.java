package com.dzics.common.model.response;

import lombok.Data;

@Data
public class LineDayAndSumDataDo {

    /**
     * 日产
     */
    private Long dayData = 0L;
    /**
     * 总产
     */
    private Long sumData = 0L;
}
