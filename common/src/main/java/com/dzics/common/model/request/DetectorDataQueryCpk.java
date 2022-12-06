package com.dzics.common.model.request;

import com.dzics.common.model.request.base.SearchTimeBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 检测记录查询条件封装类
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class DetectorDataQueryCpk extends SearchTimeBase {
    @ApiModelProperty(value = "1大正用户，0普通用户")
    private String userIdentity;
    @ApiModelProperty(value = "订单号")
    private String orderNo;
    @ApiModelProperty(value = "产品id", required = true)
    @NotBlank(message = "请选择查询产品")
    private String productNo;
    @ApiModelProperty("站点id")
    private String departId;

    @ApiModelProperty("检测结果 1 正常，0异常")
    private Integer detectionResult;

    @ApiModelProperty("当前页")
    private int page = 1;
    @ApiModelProperty("每页查询条数")
    private int limit = 10;

    @ApiModelProperty(value = "计算哪一检测项", required = true)
    private Integer detectionItem;

}
