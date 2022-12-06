package com.dzics.business.model.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 导出刀具配置
 *
 * @author ZhangChengJun
 * Date 2021/7/6.
 * @since
 */
@Data
public class ToolconfigurationExp {

    @ExcelProperty("设备编号")
    private String equipmentNo;

    @ExcelProperty("设备ID")
    private Long equipmentId;

    @ExcelProperty("刀具组编号")
    private Integer groupNo;

    @ExcelProperty("刀具编号")
    private Integer toolNo;

    @ExcelProperty("新增时间")
    private Date createTime;
}
