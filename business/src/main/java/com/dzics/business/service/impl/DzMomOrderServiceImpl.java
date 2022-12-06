package com.dzics.business.service.impl;

import com.dzics.business.service.DzMomOrderService;
import com.dzics.common.service.MomOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DzMomOrderServiceImpl implements DzMomOrderService {

    @Autowired
    private MomOrderService momOrderService;


}
