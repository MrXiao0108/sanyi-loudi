package com.dzics.business.service.cache;

import com.dzics.business.model.vo.LockScreenPassWord;
import com.dzics.business.model.vo.detectordata.edit.EditProDuctTemp;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.request.DBDetectTempVo;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.DetectionData;
import com.dzics.common.model.response.ProductParm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 检测模板缓存接口类
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
public interface DzDetectionTemplCache {
    @Cacheable(cacheNames = "dzDetectionTemplCache.list")
    List<DzDetectTempVo> list();

    @Cacheable(cacheNames = "dzDetectionTemplCache.getByOrderNoProNo", key = "#departId+#productNo+#orderId1+#lineId1")
    List<DzProductDetectionTemplate> getByOrderNoProNo(Long departId, String productNo, String orderId1, String lineId1);


    /**
     * 设备名称，产品名称，站点名称
     *
     * @param groupKey
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getGroupKey", key = "#groupKey")
    List<Map<String, Object>> getGroupKey(List<String> groupKey);

    @Cacheable(cacheNames = "dzDetectionTemplCache.getDataValue", key = "#groupKey")
    List<DetectionData> getDataValue(String groupKey);

    /**
     * 根据站点id查询站点下的产品
     *
     * @param departId 站点id
     * @return 产品信息
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getByDepartId", key = "#departId", unless = "#result == null")
    List<ProductParm> getByDepartId(Long departId);

    /**
     * @param groupId 分组id信息
     * @return 配置信息
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getEditDetectorItem", key = "#groupId")
    List<DzDetectTempVo> getEditDetectorItem(String groupId);

    /**
     * @param productNo 产品序号
     * @return 返回产品关联站点
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getByProeuctNoDepartId", key = "#productNo")
    Long getByProeuctNoDepartId(String productNo);

    /**
     * 修改检测配置
     *
     * @param templateParm
     * @return
     */
    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    Result editProDetectorItem(EditProDuctTemp templateParm);

    /**
     * 对比值修改
     *
     * @param editProDuctTemp
     * @return
     */
    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    Result dbProDetectorItem(EditProDuctTemp editProDuctTemp);

    /**
     * @param groupId 获取对比检测值
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getEditDBDetectorItem", key = "#groupId")
    List<DBDetectTempVo> getEditDBDetectorItem(String groupId);

    @CacheEvict(cacheNames = {"dzDetectionTemplCache.getByDepartId",
            "dzDetectionTemplCache.getEditDetectorItem",
            "dzDetectionTemplCache.getByProeuctNoDepartId", "dzDetectionTemplCache.getEditDBDetectorItem"}, allEntries = true)
    boolean delGroupupId(Long groupId);

    @Cacheable(cacheNames = "dzDetectionTemplCache.getByOrderNoProNoIsShow", key = "#departId+#productNo")
    List<DzProductDetectionTemplate> getByOrderNoProNoIsShow(long departId, String productNo);

    /**
     * 获取系统运行模式
     *
     * @param sub
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.systemRunModel", key = "'runModel'")
    Result systemRunModel(String sub);


    /**
     * 修改系统运行模式
     *
     * @param sub
     * @param runDataModel
     * @return
     */
    @CachePut(cacheNames = "dzDetectionTemplCache.systemRunModel", key = "'runModel'")
    Result editSystemRunModel(String sub, RunDataModel runDataModel);

    /**
     * 根据订单号和产线序号查询产线对象
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.getLineIdByOrderNoLineNo", key = "#getOrderNoLineNo.orderNo+#getOrderNoLineNo.lineNo")
    DzProductionLine getLineIdByOrderNoLineNo(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 根据订单号和产线号查询产线    清除缓存
     */
    @CacheEvict(cacheNames={"dzDetectionTemplCache.getLineIdByOrderNoLineNo"}, allEntries = true)
    void deleteLineIdByOrderNoLineNo();


    /**
     * 获取看板锁屏密码
     *
     *
     * @param screenPassWord
     * @param sub
     * @return
     */
    Result getLockScreenPassword(LockScreenPassWord screenPassWord, String sub);

    /**
     * 更新锁屏密码
     * @param sub
     * @param screenPassWord
     * @return
     */
    Result putLockScreenPassword(String sub, LockScreenPassWord screenPassWord);

    @Cacheable(cacheNames = "dzDetectionTemplCache.getMouthDate", key = "#year+#monthValue")
    List<String> getMouthDate(int year, int monthValue);

    @Cacheable(cacheNames = "cacheService.getSystemConfigDepart")
    String getSystemConfigDepart();

    @Cacheable(cacheNames = "cacheService.getIndexIsShowNg")
    String getIndexIsShowNg();

    @Cacheable(value = "cacheService.getProductNameFrequency",key = "#lineType+#productAlias")
    BigDecimal getProductNameFrequency(String lineType, String productAlias);


    @Cacheable(value = "cacheService.getNowOrder",key = "#orderNo+#lineNo+#loading")
    MonOrder getNowOrder(String orderNo, String lineNo, String loading);
}
