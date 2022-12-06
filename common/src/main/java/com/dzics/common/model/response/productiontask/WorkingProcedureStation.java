package com.dzics.common.model.response.productiontask;

import com.dzics.common.model.entity.DzWorkStationManagement;
import com.dzics.common.model.entity.DzWorkingProcedure;
import com.dzics.common.model.response.productiontask.station.StationModel;
import com.dzics.common.model.response.productiontask.station.WorkingProcedureModel;
import lombok.Data;

import java.util.List;

/**
 * 工件工序位置
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class WorkingProcedureStation {
    /**
     * 工序信息
     */
    private WorkingProcedureModel procedureModel;
    /**
     * 工位信息
     */
    private List<StationModel> stationModels;
}
