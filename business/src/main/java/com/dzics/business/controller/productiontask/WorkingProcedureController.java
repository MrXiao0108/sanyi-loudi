package com.dzics.business.controller.productiontask;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.productiontask.workingprocedure.WorkingProcedureAdd;
import com.dzics.business.service.WorkingProcedureService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 工序管理
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Api(tags = {"工序管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/working/procedure")
@Controller
public class WorkingProcedureController {
    @Autowired
    private WorkingProcedureService workingProcedureService;


    @ApiOperation(value = "查询工序", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping
    public Result<List<WorkingProcedureRes>> selWorkingProcedure(PageLimit pageLimit, WorkingProcedureAdd procedureAdd,
                                                                 @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.selWorkingProcedure(pageLimit, procedureAdd, sub);
    }


    @OperLog(operModul = "工序管理", operType = OperType.ADD, operDesc = "新增工序", operatorType = "后台")
    @ApiOperation(value = "新增工序", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PostMapping
    public Result addWorkingProcedure(
            @Valid @RequestBody WorkingProcedureAdd procedureAdd,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.addWorkingProcedure(procedureAdd, sub);
    }

    @OperLog(operModul = "工序管理", operType = OperType.UPDATE, operDesc = "编辑工序", operatorType = "后台")
    @ApiOperation(value = "编辑工序", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PutMapping
    public Result editWorkingProcedure(
            @Valid @RequestBody WorkingProcedureAdd procedureAdd,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.editWorkingProcedure(procedureAdd, sub);
    }

    @OperLog(operModul = "工序管理", operType = OperType.UPDATE, operDesc = "编辑工序", operatorType = "后台")
    @ApiOperation(value = "删除工序", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @DeleteMapping("/{id}")
    public Result delWorkingProcedure(
            @PathVariable(value = "id", required = true) @ApiParam("工序id") String id,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.delWorkingProcedure(id, sub);
    }
}
