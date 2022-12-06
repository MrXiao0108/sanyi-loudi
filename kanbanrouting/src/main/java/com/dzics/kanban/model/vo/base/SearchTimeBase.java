package com.dzics.kanban.model.vo.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.kanban.annotation.QueryType;
import com.dzics.kanban.enums.QueryTypeEnu;
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
public class SearchTimeBase {
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
