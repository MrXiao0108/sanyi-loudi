package com.dzics.common.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增接口组合接口组合关系
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class AddInterfaceGroupConfiguration {
    @ApiModelProperty(value = "组id", required = true)
    @NotNull(message = "接口组必选")
    private String groupId;

    @ApiModelProperty(value = "接口id集合", required = true)
    @NotNull(message = "接口必选")
    private List<ReqGroupConfiguration> interfaceIds;
}
