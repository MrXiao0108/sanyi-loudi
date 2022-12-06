package com.dzics.business.controller.robot;

import com.dzics.business.service.BusinessEquipmentDowntimeRecordService;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.GetByEquipmentNoVo;
import com.dzics.common.model.request.SelectEquipmentVo;
import com.dzics.common.model.response.EquipmentDo;
import com.dzics.common.model.response.GetByEquipmentNoDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"机器人停机数据"}, produces = "机器人停机数据管理相关接口")
@RequestMapping("/robotDow")
@RestController
public class RobotDowController {

    //设备表
    @Autowired
    BusinessEquipmentService businessEquipmentService;
    //停机次数表
    @Autowired
    BusinessEquipmentDowntimeRecordService dzEquipmentDowntimeRecordService;


    //    @OperLog(operModul = "机器人停机数据相关", operType = OperType.QUERY, operDesc = "分页查询机器人停机数据列表", operatorType = "后台")
    @ApiOperation(value = "分页查询机器人停机数据列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<EquipmentDo> list(PageLimit pageLimit, SelectEquipmentVo selectEquipmentVo,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) {
        Result result = businessEquipmentService.list(sub, FinalCode.ROBOT_EQUIPMENT_CODE, pageLimit, selectEquipmentVo);
        return result;
    }

    //    @OperLog(operModul = "机器人停机数据相关", operType = OperType.QUERY, operDesc = "根据机器人序号查询机器人停机记录详情", operatorType = "后台")
    @ApiOperation(value = "根据机器人序号查询机器人停机记录详情")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/getByEquipmentNo")
    public Result<GetByEquipmentNoDo> getByEquipmentNo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                       GetByEquipmentNoVo getByEquipmentNoVo, PageLimit pageLimit
    ) {
        return dzEquipmentDowntimeRecordService.getByEquipmentNo(sub, getByEquipmentNoVo, pageLimit);
    }

}
