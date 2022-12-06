package com.dzics.business.controller.machinetool;

//@Api(tags = {"机床管理"}, produces = "机床管理相关接口")
//@RequestMapping("/tool")
//@RestController
public class ToolController {

  /*  @Autowired
    BusinessEquipmentService businessEquipmentService;

    @OperLog(operModul = "机床管理相关", operType = OperType.QUERY, operDesc = "分页查询机床列表", operatorType = "后台")
    @ApiOperation(value = "分页查询机床列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Msg<EquipmentDo> list(PageLimit pageLimit, SelectEquipmentVo selectEquipmentVo,
                                 @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ){
        return  businessEquipmentService.list(sub,pageLimit,selectEquipmentVo);
    }

//    @OperLog(operModul = "机床管理相关", operType = OperType.QUERY, operDesc = "根据id查询机床", operatorType = "后台")
    @ApiOperation(value = "根据id查询机床")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/{id}")
    public Msg<EquipmentDo> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @PathVariable("id")Long id
    ){
        return  businessEquipmentService.getById(sub,id);
    }

    @OperLog(operModul = "机床管理相关", operType = OperType.UPDATE, operDesc = "修改机床", operatorType = "后台")
    @ApiOperation(value = "修改机床")
    @ApiOperationSupport(author = "jq", order = 3)
    @PutMapping
    public Msg put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                   @RequestBody PutEquipmentVo putEquipmentVo
    ){
        return  businessEquipmentService.put(sub,putEquipmentVo);
    }

    @OperLog(operModul = "机床管理相关", operType = OperType.DEL, operDesc = "删除机床", operatorType = "后台")
    @ApiOperation(value = "删除机床")
    @ApiOperationSupport(author = "jq", order = 4)
    @DeleteMapping("/{id}")
    public Msg del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                   @RequestParam(value = "id",required = true)@ApiParam("设备id") Long id
    ){
        return  businessEquipmentService.del(sub,id);
    }*/
}
