package com.dzics.sanymom.model.request.syncuser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Data
public class SyncEmployeeListUser {

    /**
     * 员工号
     */
    @JsonProperty("EmployeeNo")
    private String EmployeeNo;

    /**
     * 员工姓名
     */
    @JsonProperty("EmployeeName")
    private String EmployeeName;

    /**
     * 数据操作类型
     */
    @JsonProperty("EmpStatus")
    private String EmpStatus;
    /**
     * 预留参数1
     */
    @JsonProperty("paramRsrv1")
    private String paramRsrv1;

    /**
     * 预留参数2
     */
    @JsonProperty("paramRsrv2")
    private String paramRsrv2;

    /**
     * 预留参数3
     */
    @JsonProperty("paramRsrv3")
    private String paramRsrv3;

}
