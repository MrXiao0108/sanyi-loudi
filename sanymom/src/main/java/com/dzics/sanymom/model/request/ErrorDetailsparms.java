package com.dzics.sanymom.model.request;

import com.dzics.common.util.PageLimitBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * MOM 请求错误详情 参数
 *
 * @author ZhangChengJun
 * Date 2022/1/21.
 * @since
 */
@Data
public class ErrorDetailsparms extends PageLimitBase {
    @NotNull(message = "id必传")
    private String groupId;
}
