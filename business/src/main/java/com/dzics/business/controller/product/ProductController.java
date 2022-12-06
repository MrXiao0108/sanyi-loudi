package com.dzics.business.controller.product;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.ProductListModel;
import com.dzics.business.service.BusinessProductService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.request.AddProductVo;
import com.dzics.common.model.response.DzProductDo;
import com.dzics.common.model.response.GetProductByOrderIdDo;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"产品管理"}, produces = "产品管理相关接口")
@RequestMapping("/product")
@RestController
public class ProductController {
    @Autowired
    BusinessProductService businessProductService;

    //    @OperLog(operModul = "产品相关", operType = OperType.QUERY, operDesc = "分页查询产品列表", operatorType = "后台")
    @ApiOperation(value = "分页查询产品列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<DzProductDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                    ProductListModel productListModel
    ) {
        Result result = businessProductService.list(sub, productListModel);
        return result;
    }

    @OperLog(operModul = "产品相关", operType = OperType.ADD, operDesc = "添加产品", operatorType = "后台")
    @ApiOperation(value = "添加产品")
    @ApiOperationSupport(author = "jq", order = 2)
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                      @RequestBody @Valid AddProductVo addProductVo
    ) {
        return businessProductService.add(sub, addProductVo);
    }

    @OperLog(operModul = "产品相关", operType = OperType.UPDATE, operDesc = "修改产品", operatorType = "后台")
    @ApiOperation(value = "修改产品")
    @ApiOperationSupport(author = "jq", order = 2)
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                      @RequestBody @Valid AddProductVo addProductVo
    ) {
        return businessProductService.put(sub, addProductVo);
    }

    @OperLog(operModul = "产品相关", operType = OperType.QUERY, operDesc = "根据productId查询产品", operatorType = "后台")
    @ApiOperation(value = "根据productId查询产品")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/{productId}")
    public Result<DzProduct> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                     @PathVariable("productId") @ApiParam(value = "唯一标识", required = true) Long productId
    ) {
        return businessProductService.getById(sub, productId);
    }

    @OperLog(operModul = "产品相关", operType = OperType.QUERY, operDesc = "删除产品", operatorType = "后台")
    @ApiOperation(value = "删除产品")
    @ApiOperationSupport(author = "jq", order = 2)
    @DeleteMapping("/{productId}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                      @PathVariable("productId") @ApiParam(value = "唯一标识", required = true) Long productId
    ) {
        return businessProductService.del(sub, productId);
    }

    @OperLog(operModul = "产品相关", operType = OperType.QUERY, operDesc = "根据站点id查询产品", operatorType = "后台")
    @ApiOperation(value = "根据站点id查询产品")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/getByDepartId")
    public Result<GetProductByOrderIdDo> getByOrderId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                      @RequestParam("departId") @ApiParam(value = "站点id", required = true) Long departId
    ) {
        return businessProductService.getByOrderId(sub, page, limit, departId);
    }



}
