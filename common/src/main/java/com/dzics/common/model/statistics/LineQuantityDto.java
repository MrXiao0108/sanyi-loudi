package com.dzics.common.model.statistics;

import lombok.Data;

/**
 * @Classname LineQuantityDto
 * @Description 产线生产数量 查询参数
 * @Date 2022/6/9 13:35
 * @Created by NeverEnd
 */
@Data
public class LineQuantityDto {
    private String orderNo;
    private String lineNo;
}
