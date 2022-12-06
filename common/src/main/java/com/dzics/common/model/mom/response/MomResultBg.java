package com.dzics.common.model.mom.response;

import com.dzics.common.model.entity.MomProgressFeedbackLog;
import lombok.Data;

/**
 * mom报工
 *
 * @author ZhangChengJun
 * Date 2021/6/16.
 * @since
 */
@Data
public class MomResultBg<T> {
    private RequestHeaderVo<T> requestVo;
    private ResultVo resultVo;
    private MomProgressFeedbackLog requestLog;
}
