package com.dzics.business.controller.download.system;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.model.vo.depart.ResDepart;
import com.dzics.business.model.vo.depart.SelDepart;
import com.dzics.business.model.vo.rolemenu.SelRole;
import com.dzics.business.model.vo.user.SelUser;
import com.dzics.business.service.BusinessDepartService;
import com.dzics.business.service.BusinessDictService;
import com.dzics.business.service.BusinessSysCmdTcpService;
import com.dzics.business.service.BusinessUserService;
import com.dzics.common.enums.OperType;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.SysCmdTcp;
import com.dzics.common.model.entity.SysDict;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.SysCmdTcpItemVo;
import com.dzics.common.model.response.UserListRes;
import com.dzics.common.model.response.role.ResSysRole;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/2/23.
 * @since
 */
@Api(tags = {"系统管理导出"})
@RestController
@Slf4j
@RequestMapping(value="/reportwork", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class SystemExcelController {
    @Autowired
    private BusinessUserService businessUserService;
    @Autowired
    BusinessSysCmdTcpService businessSysCmdTcpService;
    @Autowired
    BusinessDepartService businessDepartService;
    @Autowired
    BusinessDictService dictService;

    @ApiOperation(value = "用户管理导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/user")
    public void userListsExcel(PageLimit pageLimit, SelUser selUser,HttpServletResponse response,
                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {

        String fileNameBase = "用户管理";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(200000);
            Result<List<UserListRes>> result = businessUserService.userLists(pageLimit, selUser, sub);
            List<UserListRes> data = result.getData();
            for (UserListRes userListRes:data) {
                if(userListRes.getStatus()==1){
                    userListRes.setStatusName("正常");
                }else{
                    userListRes.setStatusName("冻结");
                }
            }
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), UserListRes.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }

    }


        @ApiOperation(value = "角色列表", consumes = MediaType.APPLICATION_JSON_VALUE)
        @GetMapping(value = "/role")
        public void getRoles(PageLimit pageLimit, SelRole selRole,HttpServletResponse response,
                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {

            String fileNameBase = "角色列表";
            try {
                pageLimit.setPage(1);
                pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
                Result<List<ResSysRole>> result =businessUserService.getRoles(sub,pageLimit,selRole);
                List<ResSysRole> data = result.getData();
                String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
                EasyExcel.write(response.getOutputStream(), ResSysRole.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
            }catch (Exception e){
                log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
                response.setCharacterEncoding("utf-8");
                Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
                response.getWriter().println(JSON.toJSONString(error));
            }
        }
    @OperLog(operModul = "TCP指令导出", operType = OperType.QUERY, operDesc = "分页查询TCP指令列表", operatorType = "后台")
    @ApiOperation(value = "分页查询TCP指令列表导出")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping("/tcp")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  @RequestParam(value = "tcpName",required = false)@ApiParam("tcp 指令名称") String tcpName,
                                  @RequestParam(value = "tcpValue",required = false)@ApiParam("tcp 指令值(例如：A501 )") String tcpValue,
                                  @RequestParam(value = "tcpType",required = false)@ApiParam("0数值类型；1状态值") Integer tcpType,
                                  @RequestParam(value = "tcpDescription",required = false)@ApiParam("描述") String tcpDescription,
                                  @RequestParam(value = "deviceType",required = false)@ApiParam("1 数控机床，2  ABB机器人，3检测设备") Integer deviceType,
                                  PageLimit pageLimit,HttpServletResponse response) throws IOException {

        String fileNameBase = "TCP指令";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysCmdTcp>> result =businessSysCmdTcpService.list(pageLimit, tcpName, tcpValue, tcpType, tcpDescription, deviceType);
            List<SysCmdTcp> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysCmdTcp.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @OperLog(operModul = "TCP指令item值导出", operType = OperType.QUERY, operDesc = "分页查询TCP指令Item值列表", operatorType = "后台")
    @ApiOperation(value = "分页查询TCP指令Item值列表导出")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping("/tcp/item")
    public void listItem(PageLimit pageLimit,HttpServletResponse response,
                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                      @RequestParam(value = "id", required = true)@ApiParam("指令id") Integer id
    ) throws IOException {
        String fileNameBase = "TCP指令值";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysCmdTcpItemVo>> result =businessSysCmdTcpService.listItem(pageLimit,id);
            List<SysCmdTcpItemVo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysCmdTcpItemVo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
    @ApiOperation(value = "站点列表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/depart")
    public void queryDepart(
            PageLimit pageLimit, SelDepart selDepart,HttpServletResponse response,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "TCP指令值";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<ResDepart>> result =businessDepartService.queryDepart(pageLimit, selDepart, sub);
            List<ResDepart> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), ResDepart.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @OperLog(operModul = "字典类型数据导出", operType = OperType.QUERY, operDesc = "分页查询字典类型", operatorType = "后台")
    @ApiOperation(value = "分页查询字典类型导出")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping
    public void listDict(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @RequestParam(value = "dictName", required =false)@ApiParam("字典名称") String dictName,
                                    @RequestParam(value = "dictCode", required =false)@ApiParam("字典编码") String dictCode,
                                    @RequestParam(value = "description", required =false)@ApiParam("描述") String description,
                                    PageLimit pageLimit,HttpServletResponse response
    ) throws IOException {
        String fileNameBase = "TCP指令值";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysDict>> result =dictService.listDict(pageLimit,dictName,dictCode,description);
            List<SysDict> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysDict.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
