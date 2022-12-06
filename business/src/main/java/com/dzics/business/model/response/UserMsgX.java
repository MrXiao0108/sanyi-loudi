package com.dzics.business.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ZhangChengJun
 * Date 2020/8/9.
 */
@ApiModel("用户信息个人信息")
@Data
public class UserMsgX  implements Serializable {
    /**
     * 用户信息
     */
    @ApiModelProperty("用户信息")
    private String userName;
    /**
     * 余额
     */
    @ApiModelProperty("余额")
    private BigDecimal money;
    /**
     * 头像地址
     */
    @ApiModelProperty("头像地址")
    private String headUrl;
    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String Name;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String id;
}
