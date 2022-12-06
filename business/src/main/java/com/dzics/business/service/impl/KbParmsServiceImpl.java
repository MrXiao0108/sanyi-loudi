package com.dzics.business.service.impl;

import com.dzics.business.service.InterfaceCombination;
import com.dzics.business.service.InterfaceMethod;
import com.dzics.business.service.KbParmsService;
import com.dzics.common.model.entity.SysInterfaceMethod;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.request.kb.KbParms;
import com.dzics.common.model.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Slf4j
@Service
public class KbParmsServiceImpl implements KbParmsService {
    @Autowired
    private InterfaceCombination interfaceCombination;


    @Autowired
    private InterfaceMethod interfaceMethod;

    /**
     * 根据订单号 和 方法组名称 调用
     *
     * @param kbParms
     * @return
     */
    @Override
    public Result getMethodsGroup(KbParms kbParms) {
        List<SysInterfaceMethod> interfaceMethods = kbParms.getInterfaceMethods();
        GetOrderNoLineNo orderNoLineNo = kbParms.getOrderNoLineNo();
        Result interFaceMethods = interfaceCombination.getInterFaceMethods(interfaceMethods, orderNoLineNo);
        if (kbParms.getFrequency() > 0) {
            interFaceMethods.setRef(false);
        } else {
            interFaceMethods.setRef(true);
        }
        return interFaceMethods;
    }
}
