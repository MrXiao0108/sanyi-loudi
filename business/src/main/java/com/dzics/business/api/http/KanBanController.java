package com.dzics.business.api.http;


import com.dzics.business.service.BuProductionQuantityService;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class KanBanController {
    @Autowired
    BuProductionQuantityService productionQuantityService;
    /**
     * 产线日生产  合格/不合格   数量
     */
    @RequestMapping("/dd")
    public Object
    getOutputByLineId() {
        GetOrderNoLineNo getOrderNoLineNo = new GetOrderNoLineNo();
        getOrderNoLineNo.setOrderNo("DZ-1875");
        getOrderNoLineNo.setLineNo("1");
        getOrderNoLineNo.setCacheTime(10);
        Result outputByLineId= productionQuantityService.dailyProductionDetails(getOrderNoLineNo);
        System.out.println(outputByLineId.toString());
        return outputByLineId;
    }
}
