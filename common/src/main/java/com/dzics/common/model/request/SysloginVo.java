package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 登录日志参数
 *
 * @author ZhangChengJun
 * Date 2021/2/24.
 * @since
 */
@Data
public class SysloginVo {
    @ApiModelProperty("登录用户名")
    private String userName;
    @ApiModelProperty("0成功 1失败")
    private Integer loginStatus;
    @ApiModelProperty("搜索起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty("搜索结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
