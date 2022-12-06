package com.dzics.common.model.request.kb;

import com.dzics.common.model.entity.SysInterfaceGroup;
import com.dzics.common.model.entity.SysInterfaceMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class ReqGroupConfig {
    @ApiModelProperty(value = "接口组")
    private SysInterfaceGroup interfaceGroup;

    @ApiModelProperty("所有接口")
    private List<SysInterfaceMethod> interfaceMethods;


}
