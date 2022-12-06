package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessSysCmdTcpService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.SysCmdTcp;
import com.dzics.common.model.request.AddAndUpdCmdTcpVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"TCP指令相关"}, produces = "TCP指令相关接口")
@RequestMapping("/cmdTcp")
@RestController
public class CmdTcpController {
    @Autowired
    BusinessSysCmdTcpService businessSysCmdTcpService;

    @OperLog(operModul = "TCP指令相关", operType = OperType.QUERY, operDesc = "分页查询TCP指令列表", operatorType = "后台")
    @ApiOperation(value = "分页查询TCP指令列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<SysCmdTcp> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  @RequestParam(value = "tcpName",required = false)@ApiParam("tcp 指令名称") String tcpName,
                                  @RequestParam(value = "tcpValue",required = false)@ApiParam("tcp 指令值(例如：A501 )") String tcpValue,
                                  @RequestParam(value = "tcpType",required = false)@ApiParam("0数值类型；1状态值") Integer tcpType,
                                  @RequestParam(value = "tcpDescription",required = false)@ApiParam("描述") String tcpDescription,
                                  @RequestParam(value = "deviceType",required = false)@ApiParam("1 数控机床，2  ABB机器人，3检测设备") Integer deviceType,
                                  PageLimit pageLimit
    ) {
        return businessSysCmdTcpService.list(pageLimit, tcpName, tcpValue, tcpType, tcpDescription, deviceType);
    }

    @OperLog(operModul = "TCP指令相关", operType = OperType.ADD, operDesc = "添加TCP指令", operatorType = "后台")
    @ApiOperation(value = "添加TCP指令")
    @ApiOperationSupport(author = "jq", order = 2)
    @PostMapping
    public Result<SysCmdTcp> add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                 @RequestBody @Valid AddAndUpdCmdTcpVo cmdTcpVo) {
        return businessSysCmdTcpService.add(sub, cmdTcpVo);
    }

//    @OperLog(operModul = "TCP指令相关", operType = OperType.DEL, operDesc = "删除TCP指令", operatorType = "后台")
//    @ApiOperation(value = "删除TCP指令")
//    @ApiOperationSupport(author = "jq", order = 3)
//    @DeleteMapping(value = "/{id}")
//    public Msg del(@PathVariable("id") @ApiParam(value = "指令id", required = true) Integer id) {
//        return businessSysCmdTcpService.del(id);
//    }

    @OperLog(operModul = "TCP指令相关", operType = OperType.UPDATE, operDesc = "修改TCP指令", operatorType = "后台")
    @ApiOperation(value = "修改TCP指令")
    @ApiOperationSupport(author = "jq", order = 4)
    @PutMapping(value = "/update")
    public Result<SysCmdTcp> update(@RequestBody @Valid AddAndUpdCmdTcpVo cmdTcpVo) {
        return businessSysCmdTcpService.update(cmdTcpVo);
    }

    @OperLog(operModul = "TCP指令相关", operType = OperType.QUERY, operDesc = "根据id查询TCP指令", operatorType = "后台")
    @ApiOperation(value = "根据id查询TCP指令")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping(value = "/{id}")
    public Result<SysCmdTcp> getById(@PathVariable("id") @ApiParam(value = "指令id", required = true) Integer id) {
        return businessSysCmdTcpService.getById(id);
    }
}
