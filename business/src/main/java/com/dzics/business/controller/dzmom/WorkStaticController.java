package com.dzics.business.controller.dzmom;

import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.WorkStationParms;
import com.dzics.common.service.DzWorkingFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"报工"})
@RequestMapping("/api/mom/order")
@RestController
public class WorkStaticController {

    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;

    @ApiOperation(value = "报工流程列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/position")
    public Result getWorkStation(WorkStationParms workStationParms) {
        return dzWorkingFlowService.getLineWorkPostion(workStationParms);
    }
}
