package com.dzics.common.model.request.kb;

import com.dzics.common.model.entity.SysInterfaceMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 看板传递参数
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class KbParms {
    @ApiModelProperty("订单信息")
    @NotNull(message = "订单信息必填")
    private GetOrderNoLineNo orderNoLineNo;

    /**
     * 方法组名称
     */
    @ApiModelProperty("获取方法组")
    @NotNull(message = "接口信息必填")
    List<SysInterfaceMethod> interfaceMethods;


    private int frequency;
}
