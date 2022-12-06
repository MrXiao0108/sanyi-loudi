package com.dzics.data.acquisition.util;


import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import org.springframework.util.StringUtils;

/**
 * @Classname Subscribe
 * @Description 描述
 * @Date 2022/2/16 8:53
 * @Created by NeverEnd
 */
public  class SubscribeUtil {
    public static boolean checkOutParms(GetOrderNoLineNo orderNoLineNo) {
        if (orderNoLineNo != null) {
            if (!StringUtils.isEmpty(orderNoLineNo.getOrderNo())) {
                return true;
            }
        }
        return false;
    }
}
