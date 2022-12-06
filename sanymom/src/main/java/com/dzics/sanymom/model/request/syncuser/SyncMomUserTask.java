package com.dzics.sanymom.model.request.syncuser;

import com.dzics.sanymom.model.request.agv.AgvTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Data
public class SyncMomUserTask {
    /**
     * 协议版本
     */
    @JsonProperty(value = "SysCode")
    private String Facility;

    @JsonProperty(value = "EmployeeList")
    private List<SyncEmployeeListUser>  EmployeeList;
}
