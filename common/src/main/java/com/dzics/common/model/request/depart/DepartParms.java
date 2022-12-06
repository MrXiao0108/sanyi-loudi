package com.dzics.common.model.request.depart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepartParms {
    /**
     * 站点ID
     */
    @ApiModelProperty("站点ID")
    private String departId;
}
