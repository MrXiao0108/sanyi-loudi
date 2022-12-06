package com.dzics.common.model.response.productiontask.stationbg;

import com.dzics.common.model.constant.WorkingProcedureCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class StationModelTable {

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("完成时间")
    private String completeTime;

    @ApiModelProperty("时长")
    private String taktTime = "";
    /**
     * 状态
     */
    @ApiModelProperty("工序状态 0 未开始，1 进入 ，2 完成，3 异常")
    private Integer state = WorkingProcedureCode.NOT;

}
