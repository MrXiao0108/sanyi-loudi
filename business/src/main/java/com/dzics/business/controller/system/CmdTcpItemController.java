package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessSysCmdTcpService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.SysCmdTcp;
import com.dzics.common.model.request.CmdTcpItemVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"TCP指令值相关"}, produces = "TCP指令值相关接口")
@RequestMapping("/cmdTcp/Item")
@RestController
public class CmdTcpItemController {
    @Autowired
    BusinessSysCmdTcpService businessSysCmdTcpService;

    @OperLog(operModul = "TCP指令相关", operType = OperType.QUERY, operDesc = "分页查询TCP指令Item值列表", operatorType = "后台")
    @ApiOperation(value = "分页查询TCP指令Item值列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<SysCmdTcp> listItem(PageLimit pageLimit,
                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                      @RequestParam(value = "id", required = true)@ApiParam("指令id") Integer id
                                   ){
        return  businessSysCmdTcpService.listItem(pageLimit,id);
    }

    @OperLog(operModul = "TCP指令相关", operType = OperType.ADD, operDesc = "添加TCP指令Item值", operatorType = "后台")
    @ApiOperation(value = "添加TCP指令Item值")
    @ApiOperationSupport(author = "jq", order = 2)
    @PostMapping
    public Result<SysCmdTcp> addItem(@RequestBody @Valid CmdTcpItemVo cmdTcpItemVo){
        return  businessSysCmdTcpService.addItem(cmdTcpItemVo);
    }
    @OperLog(operModul = "TCP指令相关", operType = OperType.DEL, operDesc = "删除TCP指令Item值", operatorType = "后台")
    @ApiOperation(value = "删除TCP指令Item值")
    @ApiOperationSupport(author = "jq", order = 3)
    @DeleteMapping(value = "/{id}")
    public Result del(@PathVariable("id")@ApiParam(value = "指令id",required = true)Integer id){
        return  businessSysCmdTcpService.delItem(id);
    }

    @OperLog(operModul = "TCP指令相关", operType = OperType.UPDATE, operDesc = "修改TCP指令Item值", operatorType = "后台")
    @ApiOperation(value = "修改TCP指令Item值")
    @ApiOperationSupport(author = "jq", order = 4)
    @PutMapping
    public Result<SysCmdTcp> updateItem(@RequestBody @Valid CmdTcpItemVo cmdTcpItemVo){
        return  businessSysCmdTcpService.updateItem(cmdTcpItemVo);
    }
    @OperLog(operModul = "TCP指令相关", operType = OperType.QUERY, operDesc = "根据id查询TCP指令Item值", operatorType = "后台")
    @ApiOperation(value = "根据id查询TCP指令Item值")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping(value = "/{id}")
    public Result<SysCmdTcp> getByIdItem(@PathVariable("id")@ApiParam(value = "指令id",required = true)Integer id){
        return  businessSysCmdTcpService.getByIdItem(id);
    }
}
