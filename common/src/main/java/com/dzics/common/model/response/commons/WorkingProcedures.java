package com.dzics.common.model.response.commons;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 所有工序
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class WorkingProcedures {
    @ApiModelProperty("工序名称")
    private String workName;
    @ApiModelProperty("工序ID")
    private String workingProcedureId;
}
