package com.dzics.business.service;

import com.dzics.common.model.request.mom.AddMomOrder;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import org.springframework.cache.annotation.CacheEvict;

public interface BusMomOrderService {
    /**
     * 手动新增三一mom 订单
     *
     * @param momOrder
     * @param sub
     * @return
     */
    Result addOrder(AddMomOrder momOrder, String sub);

    /**
     * 订单开始按钮
     * @param sub
     * @param putMomOrder
     * @return
     */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result put(String sub, PutMomOrder putMomOrder);

    /**
     * Mom端专用 开始订单按钮
     * */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result MomorderBegin(PutMomOrder putMomOrder);


    /**
     * 强制关闭
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result forceClose(String sub, PutMomOrder putMomOrder);


    /**
     * 订单暂停
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result orderStop(String sub, PutMomOrder putMomOrder);

    /**
     * Mom端专用      暂停订单按钮 ----> 强制关闭订单按钮
     * */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result MomorderClose(PutMomOrder putMomOrder);


    /**
     * 订单恢复
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result orderRecover(String sub, PutMomOrder putMomOrder);

    /**
     * 订单作废
     * @param sub
     * @param proTaskOrderId
     * @return
     */
    @CacheEvict(cacheNames = "cacheService.getNowOrder",allEntries = true)
    Result orderDelete(String sub, String proTaskOrderId);

    /**
     * 取消报工
     * */
    Result orderCancelWorkReporting(String sub,String proTaskOrderId);
}
