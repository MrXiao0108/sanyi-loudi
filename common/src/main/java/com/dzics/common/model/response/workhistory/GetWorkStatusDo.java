package com.dzics.common.model.response.workhistory;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年11月22日 11:15
 */
@Data
public class GetWorkStatusDo {
    @ApiModelProperty(value = "主键ID")
    @ExcelIgnore
    private String id;

    @ApiModelProperty(value = "作业名称")
    @ExcelProperty(value = "作业名称",index = 0)
    private String jobName;

    @ApiModelProperty(value = "分片项")
    @ExcelProperty(value = "分片项",index = 1)
    private String shardingItem;

    @ApiModelProperty(value = "状态")
    @ExcelProperty(value = "状态",index = 2)
    private String state;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty(value = "创建时间",index = 3)
    private String startTime;

    @ApiModelProperty(value = "备注")
    @ExcelProperty(value = "备注",index = 4)
    private String message;
}
