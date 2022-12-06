package com.dzics.business.controller.line;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessDzProductionLineService;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.request.AddLineVo;
import com.dzics.common.model.request.BingEquipmentVo;
import com.dzics.common.model.request.PudLineVo;
import com.dzics.common.model.request.line.LineParmsList;
import com.dzics.common.model.response.LineDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.feishi.LineListDo;
import com.dzics.common.util.UnderlineTool;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"产线相关"}, produces = "产线相关接口")
@RequestMapping("/line")
@RestController
public class LineController {

    @Autowired
    BusinessDzProductionLineService lineService;
    @Autowired
    BusinessEquipmentService businessEquipmentService;

    @OperLog(operModul = "产线相关", operType = OperType.ADD, operDesc = "新增产线", operatorType = "后台")
    @ApiOperation(value = "新增产线")
    @ApiOperationSupport(author = "jq", order = 2)
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid AddLineVo data) throws Exception {
        return lineService.add(sub, data);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "分页查询产线列表", operatorType = "后台")
    @ApiOperation(value = "分页查询产线列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<LineDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                               LineParmsList lineParmsList) {
        return lineService.list(sub, lineParmsList);
    }

    @OperLog(operModul = "产线相关", operType = OperType.DEL, operDesc = "删除产线", operatorType = "后台")
    @ApiOperation(value = "删除产线")
    @ApiOperationSupport(author = "jq", order = 3)
    @DeleteMapping("/{id}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @PathVariable(value = "id", required = true) @ApiParam("产线id") Long id
    ) {
        return lineService.del(sub, id);
    }

    @OperLog(operModul = "产线相关", operType = OperType.UPDATE, operDesc = "编辑产线", operatorType = "后台")
    @ApiOperation(value = "编辑产线")
    @ApiOperationSupport(author = "jq", order = 4)
    @PutMapping
    public Result pud(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid PudLineVo data
    ) throws Exception {
        return lineService.pud(sub, data);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "根据id查询产线详情", operatorType = "后台")
    @ApiOperation(value = "根据id查询产线详情")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping("/{id}")
    public Result<LineDo> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  @PathVariable("id") Long id
    ) {

        return lineService.getById(sub, id);
    }




    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "禁用启用产线", operatorType = "后台")
    @ApiOperation(value = "禁用启用产线")
    @ApiOperationSupport(author = "jq", order = 5)
    @PostMapping("/status")
    public Result<DzProductionLine> putStatus(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @RequestParam("id") Long id
    ) {
        return lineService.putStatus(sub, id);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "根据产线id查询设备列表", operatorType = "后台")
    @ApiOperation(value = "根据产线id查询设备列表")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping("/getEquipmentByLineId/{id}")
    public Result<DzEquipment> getEquipmentByLineId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                    @PathVariable("id") Long id
    ) {
        return businessEquipmentService.getEquipmentByLineId(sub, id);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "绑定产线统计产量的设备", operatorType = "后台")
    @ApiOperation(value = "绑定产线统计产量的设备")
    @ApiOperationSupport(author = "jq", order = 5)
    @PutMapping("/bing/equipment/equipment")
    public Result bingEquipment(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                @RequestBody @Valid BingEquipmentVo bingEquipmentVo
    ) {
        return lineService.bingEquipment(sub, bingEquipmentVo);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "查询所有产线", operatorType = "后台")
    @ApiOperation(value = "查询所有产线")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping("/all/line/list")
    public Result<LineListDo> allLineList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) {
        return lineService.allLineList(sub);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "查询所有产线(公共方法)", operatorType = "后台")
    @ApiOperation(value = "查询所有产线(公共方法)")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping("/all/list")
    public Result allList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) {
        return lineService.allList(sub);
    }

}
