package com.dzics.business.service;

import com.dzics.business.model.vo.detectordata.AddDetectorPro;
import com.dzics.business.model.vo.detectordata.GroupId;
import com.dzics.business.model.vo.detectordata.ProDuctCheck;
import com.dzics.business.model.vo.detectordata.edit.EditProDuctTemp;
import com.dzics.common.model.request.DetectorDataQuery;
import com.dzics.common.model.response.Result;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 设备检测记录
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
public interface BusDetectorDataService {

    /**
     * 检测记录
     *
     * @param detectorDataQuery
     * @param sub
     * @return
     */
    Result selDetectorData(DetectorDataQuery detectorDataQuery, String sub);

    /**
     * 新增检测项设置，获取检测项默认配置
     *
     * @param groupId 检测项id
     * @param sub
     * @return
     */
    Result selDetectorItem(GroupId groupId, String sub);

    /**
     * 新增产品检测配置
     *
     * @param detectorPro
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp"}, allEntries = true)
    Result addDetectorItem(AddDetectorPro detectorPro, String sub);

    /**
     * 检测配置列表
     *
     * @param proDuctCheck
     * @param sub
     * @return
     */
    Result<List<LinkedHashMap<String, Object>>> queryProDetectorItem(ProDuctCheck proDuctCheck, String sub);


    /**
     * 删除检测配置
     *
     * @param groupId 同组配置id
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp",
            "dzDetectionTemplCache.getByOrderNoProNo"}, allEntries = true)
    Result delProDetectorItem(Long groupId, String sub);


    /**
     * 修改检测产品配置
     *
     * @param templateParm
     * @param sub
     * @return
     */

    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp",
            "dzDetectionTemplCache.getByOrderNoProNo",
            "dzDetectionTemplCache.getByOrderNoProNoIsShow",
            "dzDetectionTemplCache.getDataValue"}, allEntries = true)
    Result editProDetectorItem(EditProDuctTemp templateParm, String sub);

    /**
     * 对比值修改
     *
     * @param editProDuctTemp
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getDzProDetectIonTemp"}, allEntries = true)
    Result dbProDetectorItem(EditProDuctTemp editProDuctTemp, String sub);

    /**
     * 导出 excel  poi
     *
     * @param detectorDataQuery
     * @param sub
     * @return
     */
    void getDetectorExcel(HttpServletResponse response, DetectorDataQuery detectorDataQuery, String sub);

    /**
     * 导出 excel
     *
     * @param detectorDataQuery
     * @param sub
     * @return
     */
    void getDetectorExcel1(HttpServletResponse response, DetectorDataQuery detectorDataQuery, String sub);
}
