package com.dzics.common.model.response.productiontask.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class WorkingProcedureModel {
    @ApiModelProperty("工序编码")
    private String workcode;

    @ApiModelProperty("工序名称")
    private String workName;

    @ApiModelProperty("工序ID")
    private String workingProcedureId;

    @ApiModelProperty("工序状态 0 未开始，1 进入 ，2 完成，3 异常")
    private Integer state;

    @ApiModelProperty("工序排序吗")
    private Integer sortCode;

    /**
     * 经过的工位信息
     */
    private   List<StationModel> stationModels;
}
