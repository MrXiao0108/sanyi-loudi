package com.dzics.common.model.response.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Data
public class DzicsStationCode {
    @ApiModelProperty("dzics工位编号")
    private String dzStationCode;
    @ApiModelProperty("dzics工位ID")
    private String dzicsCodeId;

}
