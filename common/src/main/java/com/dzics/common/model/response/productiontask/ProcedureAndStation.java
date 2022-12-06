package com.dzics.common.model.response.productiontask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 工位工序信息
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class ProcedureAndStation {
    @ApiModelProperty("工位ID")
    private String stationId;
    @ApiModelProperty("工位名称")
    private String stationName;
    @ApiModelProperty("工位编码")
    private String stationCode;
    @ApiModelProperty("工序ID")
    private String workingProcedureId;
    @ApiModelProperty("工序排序吗")
    private Integer sortCode;
    @ApiModelProperty("工序编码")
    private String workCode;
    @ApiModelProperty("工序名称")
    private String workName;

}
