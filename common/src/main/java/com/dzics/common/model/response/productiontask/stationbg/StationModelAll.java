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
public class StationModelAll {

    /**
     * 工位ID
     */
    @ApiModelProperty("工位ID")
    private String stationId;

    /**
     * 工位编码
     */
    @ApiModelProperty("工位编码")
    private String stationCode;

    /**
     * 工位名称
     */
    @ApiModelProperty("工位名称")
    private String stationName;

    /**
     * 工序排序吗
     */
    @ApiModelProperty("工序排序吗")
    private Integer sortCode;


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

    /**
     * 1 表示是NG工位
     */
    private String ngCode;

    /**
     * 需要合并工位的标志
     */
    private String mergeCode;
    /**
     * 出料工位标识
     */
    private String outFlag;
}
