package com.dzics.common.model.response.workhistory;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.common.model.write.StatusConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年11月22日 9:00
 */
@Data
public class GetWorkDo {
    @ApiModelProperty(value = "主键ID")
    @ExcelIgnore
    private String id;

    @ApiModelProperty(value = "作业名称")
    @ExcelProperty(value = "作业名称",index = 0)
    private String jobName;

    @ApiModelProperty(value = "服务器IP")
    @ExcelProperty(value = "服务器IP",index = 1)
    private String ip;

    @ApiModelProperty(value = "分片项")
    @ExcelProperty(value = "分片项",index = 2)
    private String shardingItem;


    @ApiModelProperty(value = "执行结果")
    @ExcelProperty(value = "执行结果",index = 3,converter = StatusConverter.class)
    private Integer isSuccess;

    @ApiModelProperty(value = "失败原因")
    @ExcelProperty(value = "失败原因",index = 4)
    private String failureCause;

    @ApiModelProperty(value = "开始时间")
    @ExcelProperty(value = "开始时间",index = 5)
    private String startTime;

    @ApiModelProperty(value = "完成时间")
    @ExcelProperty(value = "结束时间",index = 6)
    private String endTime;
}
