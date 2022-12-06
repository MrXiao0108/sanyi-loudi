package com.dzics.business.controller.robot;

//@Api(tags = {"机器人管理"}, produces = "机器人管理相关接口")
//@RequestMapping("/robot")
//@RestController
public class RobotController {

  /*  @Autowired
    BusinessEquipmentService businessEquipmentService;

    @OperLog(operModul = "机器人管理相关", operType = OperType.QUERY, operDesc = "分页查询机器人列表", operatorType = "后台")
    @ApiOperation(value = "分页查询机器人列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Msg<EquipmentDo> list(PageLimit pageLimit, SelectEquipmentVo selectEquipmentVo,
                                 @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
                                 ){
        return  businessEquipmentService.list(sub,pageLimit,selectEquipmentVo);
    }

//    @OperLog(operModul = "机器人管理相关", operType = OperType.QUERY, operDesc = "根据id查询机器人", operatorType = "后台")
    @ApiOperation(value = "根据id查询机器人")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/{id}")
    public Msg<EquipmentDo> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                 @PathVariable("id")Long id
    ){
        return  businessEquipmentService.getById(sub,id);
    }

    @OperLog(operModul = "机器人管理相关", operType = OperType.UPDATE, operDesc = "修改机器人", operatorType = "后台")
    @ApiOperation(value = "修改机器人")
    @ApiOperationSupport(author = "jq", order = 3)
    @PutMapping
    public Msg put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @RequestBody @Valid PutEquipmentVo putEquipmentVo
    ){
        return  businessEquipmentService.put(sub,putEquipmentVo);
    }

    @OperLog(operModul = "机器人管理相关", operType = OperType.DEL, operDesc = "删除机器人", operatorType = "后台")
    @ApiOperation(value = "删除机器人")
    @ApiOperationSupport(author = "jq", order = 4)
    @DeleteMapping("/{id}")
    public Msg del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                   @PathVariable(value = "id",required = true)@ApiParam(value = "设备id")Long id
    ){
        return  businessEquipmentService.del(sub,id);
    }
*/
}

