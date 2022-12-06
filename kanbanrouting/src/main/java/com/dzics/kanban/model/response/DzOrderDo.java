package com.dzics.kanban.model.response;

import com.dzics.kanban.model.entity.DzOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DzOrderDo extends DzOrder {

//    @ApiModelProperty("站点id")

    @ApiModelProperty("站点名称")
    private String departName;

}
