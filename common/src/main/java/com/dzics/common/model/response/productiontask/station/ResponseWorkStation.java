package com.dzics.common.model.response.productiontask.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 返回的工件位置信息
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class ResponseWorkStation {

    /**
     * 工件信息
     */
    @ApiModelProperty("工件信息")
    private WorkingFlowRes workingFlowRes;

    /**
     * 工序信息
     */
    @ApiModelProperty("工序信息")
    List<WorkingProcedureModel> workingProcedureModels;
}
