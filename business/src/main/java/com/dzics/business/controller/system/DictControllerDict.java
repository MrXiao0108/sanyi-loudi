package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessDictService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.SysDict;
import com.dzics.common.model.request.DictVo;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"字典数据相关"}, produces = "字典数据相关接口")

@RequestMapping("/dicts")
@RestController
public class DictControllerDict {

    @Autowired
    BusinessDictService dictService;
    @OperLog(operModul = "字典数据相关", operType = OperType.QUERY, operDesc = "根据id查询字典类型", operatorType = "后台")
    @ApiOperation(value = "根据id查询字典类型")
    @ApiOperationSupport(author = "jq", order = 5)
    @GetMapping(value = "/{id}")
    public Result<SysDict> selectDictById(@PathVariable("id")@ApiParam(value = "字典id(必填)",required = true) Integer id){
        return  dictService.selectDictById(id);
    }
}
