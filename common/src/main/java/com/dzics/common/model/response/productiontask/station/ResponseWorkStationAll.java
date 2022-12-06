package com.dzics.common.model.response.productiontask.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 返回的工件位置信息
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class ResponseWorkStationAll {
    /**
     * 报工信息
     */
    @ApiModelProperty("报工信息")
    private List<ResponseWorkStation> responseWorkStation;

}
