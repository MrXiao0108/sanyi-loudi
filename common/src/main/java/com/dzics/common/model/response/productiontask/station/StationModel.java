package com.dzics.common.model.response.productiontask.station;

import com.dzics.common.model.constant.WorkingProcedureCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class StationModel {
    @ApiModelProperty("工位ID")
    private String stationId;
    @ApiModelProperty("工位编码")
    private String stationCode;
    @ApiModelProperty("工位名称")
    private String stationName;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("工序状态 0 未开始，1 进入 ，2 完成，3 异常")
    private Integer state = WorkingProcedureCode.NOT;
}
