package com.dzics.common.model.response.productiontask.stationbg;

import com.dzics.common.model.response.productiontask.station.WorkingFlowRes;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 返回的工件位置信息
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class ResponseWorkStationBg {

    /**
     * 工件信息
     */
    @ApiModelProperty("工件信息")
    private WorkingFlowResBg workingFlowRes;

    /**
     * 工序信息
     */
    @ApiModelProperty("工位信息")
    List<StationModelAll> stationModels;
}
