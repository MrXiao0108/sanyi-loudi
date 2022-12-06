package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessDictService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.SysDict;
import com.dzics.common.model.request.DictVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"字典数据相关"}, produces = "字典数据相关接口")

@RequestMapping("/dict")
@RestController
public class DictController {

    @Autowired
    BusinessDictService dictService;

    @OperLog(operModul = "字典数据相关", operType = OperType.ADD, operDesc = "新增字典类型", operatorType = "后台")
    @ApiOperation(value = "新增字典类型")
    @ApiOperationSupport(author = "jq", order = 1)
    @PostMapping
    public Result<SysDict> addDict(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid DictVo dictVo){
        return  dictService.addDict(sub,dictVo);
    }

//    @OperLog(operModul = "字典数据相关", operType = OperType.DEL, operDesc = "删除字典类型", operatorType = "后台")
//    @ApiOperation(value = "删除字典类型")
//    @ApiOperationSupport(author = "jq", order = 2)
//    @DeleteMapping(value = "/{id}")
//    public Msg delDict(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
//                       @PathVariable("id")@ApiParam(value = "字典id(必填)",required = true) Integer id){
//        return  dictService.delDict(sub,id);
//    }

    @OperLog(operModul = "字典数据相关", operType = OperType.UPDATE, operDesc = "修改字典类型", operatorType = "后台")
    @ApiOperation(value = "修改字典类型")
    @ApiOperationSupport(author = "jq", order = 3)
    @PutMapping
    public Result<SysDict> updDict(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid DictVo dictVo){
        return  dictService.updDict(sub,dictVo);
    }

    @OperLog(operModul = "字典数据相关", operType = OperType.QUERY, operDesc = "分页查询字典类型", operatorType = "后台")
    @ApiOperation(value = "分页查询字典类型")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping
    public Result<SysDict> listDict(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @RequestParam(value = "dictName", required =false)@ApiParam("字典名称") String dictName,
                                    @RequestParam(value = "dictCode", required =false)@ApiParam("字典编码") String dictCode,
                                    @RequestParam(value = "description", required =false)@ApiParam("描述") String description,
                                    PageLimit pageLimit
                                 ){
        return  dictService.listDict(pageLimit,dictName,dictCode,description);
    }
}
