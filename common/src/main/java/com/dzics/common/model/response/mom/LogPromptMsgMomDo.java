package com.dzics.common.model.response.mom;

import com.dzics.common.model.entity.LogPromptMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2022/10/9 0009 10:35
 */
@Data
public class LogPromptMsgMomDo {

    @ApiModelProperty("日志表")
    private LogPromptMsg logPromptMsg;

    @ApiModelProperty("结果")
    private String status;
}
