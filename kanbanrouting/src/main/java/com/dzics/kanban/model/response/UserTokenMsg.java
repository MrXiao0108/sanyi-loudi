package com.dzics.kanban.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhangChengJun
 * Date 2020/5/19.
 */
@Data
@ApiModel(value = "Token对象信息", description = "Token对象信息")
public class UserTokenMsg implements Serializable {

    /**
     * access_token : ACCESS_TOKEN
     * refresh_token : REFRESH_TOKEN
     * scope : SCOPE
     * expires_in : 7200
     */
    /**
     * 接口调用凭证
     */
    @ApiModelProperty(value = "接口调用凭证")
    private String access_token;
    /**
     * access_token 接口调用凭证超时时间，单位（秒）
     */
    @ApiModelProperty(value = "access_token 接口调用凭证超时时间，单位（秒）")
    private long expires_in;

    /**
     * 授权用户唯一标识
     */
    @ApiModelProperty(value = "授权用户唯一标识")
    private String sub;

    @ApiModelProperty("识别码")
    private String code;
    @ApiModelProperty(value = "创建时间")
    private long creatTime;

    @ApiModelProperty(value = "刷新token信息")
    private RefTokenMsg refTokenMsg;
    /**
     * 用户刷新 access_token
     */
    @ApiModelProperty(value = "用户刷新 access_token")
    private String refresh_token;

}
