package com.dzics.business.controller.inspection;


import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessToolGroupsService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzToolGroups;
import com.dzics.common.model.entity.DzToolInfo;
import com.dzics.common.model.request.toolinfo.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@Api(tags = {"刀具组信息"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/product/check")
@Controller
public class ToolGroupsInfoController {

    @Autowired
    BusinessToolGroupsService businessToolGroupsService;

    /**
     * 查询刀具组列表
     * @param tokenHdaer
     * @param sub
     * @param groupNo
     * @return
     */
    @ApiOperation(value = "查询刀具组")
    @ApiOperationSupport(author = "jq", order = 10)
    @GetMapping
    public Result<DzToolGroups> getToolGroupsList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                  PageLimit pageLimit,
                                                  @RequestParam(value = "groupNo",required = false) String groupNo) {
        Result toolGroupsList = businessToolGroupsService.getToolGroupsList(sub, pageLimit, groupNo);
        return toolGroupsList;
    }

    /**
     * 新增刀具组
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "新增刀具组", operType = OperType.ADD, operDesc = "新增刀具组", operatorType = "后台")
    @ApiOperation(value = "新增刀具组")
    @ApiOperationSupport(author = "jq", order = 11)
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @RequestBody @Valid AddDzToolGroupVo addDzToolGroupVo
                                    ) {
        return businessToolGroupsService.addToolGroups(addDzToolGroupVo);
    }

    /**
     * 新增刀具组
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "删除刀具组", operType = OperType.DEL, operDesc = "删除刀具组", operatorType = "后台")
    @ApiOperation(value = "删除刀具组")
    @ApiOperationSupport(author = "jq", order = 12)
    @DeleteMapping(value = "/{toolGroupsId}")
    public Result delToolGroups(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @PathVariable("toolGroupsId")Long toolGroupsId
    ) {
        return businessToolGroupsService.delToolGroups(toolGroupsId);
    }

    /**
     * 编辑刀具组编号
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "修改刀具组编号", operType = OperType.UPDATE, operDesc = "修改刀具组编号", operatorType = "后台")
    @ApiOperation(value = "修改刀具组编号")
    @ApiOperationSupport(author = "jq", order = 13)
    @PutMapping
    public Result putToolGroups(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                @RequestBody @Valid PutToolGroupsVo putToolGroupsVo
    ) {
        return businessToolGroupsService.putToolGroups(putToolGroupsVo);
    }
    /**
     * 根据刀具组id查询刀具列表
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "根据刀具组id查询刀具列表")
    @ApiOperationSupport(author = "jq", order = 14)
    @GetMapping("/toolGroupsId")
    public Result<DzToolInfo> getToolInfoList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @RequestParam("toolGroupsId") Long toolGroupsId, PageLimit pageLimit
    ) {
        return businessToolGroupsService.getToolInfoList(toolGroupsId,pageLimit);
    }

//    /**
//     * 编辑所有刀具编号
//     * @param tokenHdaer
//     * @param sub
//     * @return
//     */
//    @OperLog(operModul = "编辑指定刀具组的所有刀具", operType = OperType.UPDATE, operDesc = "编辑指定刀具组的所有刀具", operatorType = "后台")
//    @ApiOperation(value = "编辑指定刀具组的所有刀具")
//    @ApiOperationSupport(author = "jq", order = 11)
//    @PutMapping("/putToolInfo")
//    public Result putToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
//                                @RequestBody @Valid PutToolInfoVo putToolGroupsVo
//    ) {
//        return businessToolGroupsService.putToolInfo(putToolGroupsVo);
//    }

    /**
     * 删除刀具
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "删除刀具", operType = OperType.DEL, operDesc = "删除刀具", operatorType = "后台")
    @ApiOperation(value = "删除刀具")
    @ApiOperationSupport(author = "jq", order = 15)
    @DeleteMapping("/delToolInfo/{id}")
    public Result delToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @PathVariable("id")@ApiParam("刀具id")Long id
    ) {
        return businessToolGroupsService.delToolInfo(id);
    }
    /**
     * 编辑刀具
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "编辑刀具", operType = OperType.UPDATE, operDesc = "编辑刀具", operatorType = "后台")
    @ApiOperation(value = "编辑刀具")
    @ApiOperationSupport(author = "jq", order = 16)
    @PutMapping("/putToolInfo")
    public Result putToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @RequestBody @Valid PutToolInfoByIdVo putToolInfoByIdVo
    ) {
        return businessToolGroupsService.putToolInfo(putToolInfoByIdVo.getId(),putToolInfoByIdVo.getToolNo());
    }

    /**
     * 新增刀具
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @OperLog(operModul = "新增刀具", operType = OperType.ADD, operDesc = "新增刀具", operatorType = "后台")
    @ApiOperation(value = "新增刀具")
    @ApiOperationSupport(author = "jq", order = 17)
    @PostMapping("/addToolInfo")
    public Result addToolInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                @RequestBody @Valid AddToolInfoVo addToolInfoVo
    ) {
        return businessToolGroupsService.addToolInfo(addToolInfoVo.getToolGroupId(),addToolInfoVo.getToolNo());
    }
}
