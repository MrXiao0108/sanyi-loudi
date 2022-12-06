package com.dzics.business.controller.dzmom.agv;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.DzMomReceiveMaterialService;
import com.dzics.business.service.agv.AgvService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.model.qrCode.QrCodeParms;
import com.dzics.common.model.qrCode.QrCodeType;
import com.dzics.common.model.request.agv.AgvClickSignal;
import com.dzics.common.model.request.agv.AgvClickSignalConfirmV2;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimitAgv;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"ROB"}, produces = "二维码")
@RequestMapping("/api/qrcode/dispatch")
@RestController
public class QrCodeController {

    @Autowired
    private AgvService agvService;


    @OperLog(operModul = "二维码", operType = OperType.ADD, operDesc = "写入二维码", operatorType = "后台")
    @ApiOperation(value = "二维码写入", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/input")
    public Result getFrid(@Valid @RequestBody QrCodeParms qrCodeParms) {
        return agvService.inputQrCode(qrCodeParms);
    }


}
