package com.dzics.common.model.request.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.annotation.QueryType;
import com.dzics.common.enums.QueryTypeEnu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author ZhangChengJun
 * Date 2021/1/26.
 * 时间搜索
 * @since
 */
@Data
public class SearchTimeBase   {
    @ApiModelProperty("开始时间")
    @TableField("create_time")
    @QueryType(QueryTypeEnu.ge)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @TableField("create_time")
    @QueryType(QueryTypeEnu.lt)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
