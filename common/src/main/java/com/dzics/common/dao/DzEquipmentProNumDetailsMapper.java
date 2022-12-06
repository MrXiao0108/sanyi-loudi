package com.dzics.common.dao;

import com.dzics.common.model.custom.UpValueDevice;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentProNumDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.charts.EquipmentDataDetailsDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备生产数量详情表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
public interface DzEquipmentProNumDetailsMapper extends BaseMapper<DzEquipmentProNumDetails> {

    UpValueDevice getupsaveddnumlinnuty(@Param("lineNum") String lineNum, @Param("deviceNum") String deviceNum, @Param("deviceType") String deviceType, @Param("orderNumber") String orderNumber);

}
