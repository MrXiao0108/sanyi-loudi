package com.dzics.business.controller.agvmanage;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 三一agv管理
 *
 * @author jq
 * 2021-7-22
 */
@Api(tags = {"三一agv管理"}, produces = "三一agv管理相关接口")
@RestController
@RequestMapping("/agv/manage")
public class AgvManageController {
    //模拟列表数据
    public static List<Map<String, String>> listData = new ArrayList<>();
    //模拟来料信号
    public static Map<String,String>data=null;
    @Autowired
    DateUtil dateUtil;

    @OperLog(operModul = "三一agv管理", operType = OperType.QUERY, operDesc = "查询列表", operatorType = "后台")
    @ApiOperation(value = "查询列表")
    @GetMapping
    public Result list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                       @RequestParam(value = "workpieceCode", required = false) String workpieceCode, PageLimit pageLimit) {
        if (listData.size() == 0) {
            Map<String, String> map1 = new HashMap<>();
            map1.put("orderNo", "DZ-6666");
            map1.put("workNumber", "5");
            map1.put("workpieceCode", "w1001");
            map1.put("confirmTime", "2021-07-21 08:08:09");
            listData.add(map1);
            Map<String, String> map2 = new HashMap<>();
            map2.put("orderNo", "DZ-6666");
            map2.put("workNumber", "5");
            map2.put("workpieceCode", "w1002");
            map2.put("confirmTime", "2021-07-21 08:08:10");
            listData.add(map2);
        }
        List<Map<String, String>> mapList = new ArrayList<>();
        if (!StringUtils.isEmpty(workpieceCode)) {
            for (Map<String, String> map : listData) {
                String name = map.get("workpieceCode");
                if (workpieceCode.equals(name)) {
                    mapList.add(map);
                }
            }
        } else {
            mapList = listData;
        }
        return Result.OK(mapList);
    }

    @OperLog(operModul = "三一agv管理", operType = OperType.ADD, operDesc = "确认一筐来料", operatorType = "后台")
    @ApiOperation(value = "确认一筐来料")
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        if(data!=null){
            listData.add(data);
            data=null;
        }
        return Result.OK(data);
    }

    @OperLog(operModul = "三一agv管理", operType = OperType.QUERY, operDesc = "判断是否来料", operatorType = "后台")
    @ApiOperation(value = "判断是否来料")
    @GetMapping("/isHasData")
    public Result isHasData(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
       if(data!=null){
           return Result.OK(data);
       }
        return Result.OK(0);
    }

}
