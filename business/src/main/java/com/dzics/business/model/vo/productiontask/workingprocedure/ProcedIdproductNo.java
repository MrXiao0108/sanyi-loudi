package com.dzics.business.model.vo.productiontask.workingprocedure;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 工序id 和工件编号获取两者关联
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class ProcedIdproductNo extends PageLimit  {
    @ApiModelProperty(value = "工序ID",required = true)
    @NotNull(message = "工序Id必传")
    private String workingProcedureId;

    @ApiModelProperty("工件编号")
    private String productNo;
}
