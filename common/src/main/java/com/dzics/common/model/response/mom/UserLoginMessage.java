package com.dzics.common.model.response.mom;

import com.dzics.common.model.entity.MomUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Data
public class UserLoginMessage {
    @ApiModelProperty("产线")
    private String lineName;
    @ApiModelProperty("用户信息")
    private MomUser momUser;
}
