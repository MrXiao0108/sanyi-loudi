package com.dzics.business.config;

import com.dzics.common.model.entity.DzWorkingFlowBig;
import com.dzics.common.service.DzWorkingFlowBigService;
import com.dzics.common.service.DzWorkingFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

//@Component
//@Slf4j
public class TestInitOnly {
//    @Autowired
    private DzWorkingFlowService flowService;
//    @Autowired
    private DzWorkingFlowBigService bigService;

//    @PostConstruct
    public void initFlowBig() {
        List<DzWorkingFlowBig> initFlowBigs = bigService.getInitFlowBig();
        bigService.updateBatchById(initFlowBigs);
    }
}
