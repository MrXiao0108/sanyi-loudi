package com.dzics.business.service.impl;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.ToolCompensationDataService;
import com.dzics.common.dao.*;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.toolinfo.AddToolConfigureVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzToolCompensationDataService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ToolCompensationDataServiceImpl implements ToolCompensationDataService {

    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzToolCompensationDataMapper dzToolCompensationDataMapper;
    @Autowired
    DzToolInfoMapper dzToolInfoMapper;
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    DzToolGroupsMapper dzToolGroupsMapper;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DzToolCompensationDataService dzToolCompensationDataService;
    @Override
    public Result<List<DzToolCompensationData>> getToolConfigureList(String sub, PageLimit pageLimit, Integer groupNo) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        if (pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        List<DzToolCompensationData> dzToolCompensationData = dzToolCompensationDataMapper.getToolConfigureList(pageLimit.getField(),pageLimit.getType(),orgCode,groupNo);
        PageInfo<DzToolCompensationData>info=new PageInfo<>(dzToolCompensationData);
        return new Result(CustomExceptionType.OK,info.getList(),info.getTotal());
    }


    @Override
    public Result addToolConfigure(AddToolConfigureVo addToolConfigureVo) {
        QueryWrapper<DzToolCompensationData>wrapper=new QueryWrapper<>();
        wrapper.eq("equipment_id",addToolConfigureVo.getEquipmentId());
        wrapper.eq("group_no",addToolConfigureVo.getGroupNo());
        wrapper.eq("tool_no",addToolConfigureVo.getToolNo());
        DzToolCompensationData dzToolCompensationData = dzToolCompensationDataMapper.selectOne(wrapper);
        if(dzToolCompensationData!=null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_109);
        }
        Long equipmentId = addToolConfigureVo.getEquipmentId();
        DzEquipment dzEquipment = dzEquipmentMapper.selectById(equipmentId);
        if(dzEquipment==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_111);
        }
        //判断设备是否绑定过相同编号的刀具
        QueryWrapper<DzToolCompensationData> toolNo = new QueryWrapper<DzToolCompensationData>()
                .eq("tool_no", addToolConfigureVo.getToolNo())
                .eq("equipment_id",addToolConfigureVo.getEquipmentId());
        List<DzToolCompensationData> dataList = dzToolCompensationDataMapper.selectList(toolNo);
        if(dataList.size()>0){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_112);
        }
        dzToolCompensationData=new DzToolCompensationData();
        dzToolCompensationData.setEquipmentId(addToolConfigureVo.getEquipmentId());
        dzToolCompensationData.setGroupNo(addToolConfigureVo.getGroupNo());
        dzToolCompensationData.setOrgCode(dzEquipment.getOrgCode());
        dzToolCompensationData.setToolNo(addToolConfigureVo.getToolNo());
        dzToolCompensationData.setOrderId(addToolConfigureVo.getOrderId());
        dzToolCompensationData.setLineId(addToolConfigureVo.getLineId());
        int insert = dzToolCompensationDataMapper.insert(dzToolCompensationData);
        return Result.ok(dzToolCompensationData);
    }

    @Override
    public Result delToolConfigure(Integer id) {
        dzToolCompensationDataMapper.deleteById(id);
        return Result.ok();
    }

    @Override
    public Result getToolByEqIdAndGroupNo(Long equipmentId, Integer groupNo,Long toolGroupsId) {
        List<DzToolInfo>list=dzToolInfoMapper.getToolByEqIdAndGroupNo(equipmentId,groupNo,toolGroupsId);
        return Result.ok(list);
    }


    @Override
    @Transactional
    public Result putToolConfigure(AddToolConfigureVo addToolConfigureVo) {
        DzToolCompensationData data = dzToolCompensationDataMapper.selectById(addToolConfigureVo.getId());
        if(data==null){
            log.error("刀具配置信息不存在,id：{}", addToolConfigureVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        boolean a = data.getEquipmentId().intValue() == addToolConfigureVo.getEquipmentId().intValue();
        boolean b =data.getGroupNo().intValue()==addToolConfigureVo.getGroupNo().intValue();
        boolean c = data.getToolNo().intValue() == addToolConfigureVo.getToolNo().intValue();
        if(a&&b&&c){
            //数据没做更改
            return Result.ok();
        }

        QueryWrapper<DzToolCompensationData>wrapper=new QueryWrapper<>();
        wrapper.eq("equipment_id",addToolConfigureVo.getEquipmentId());
        wrapper.eq("tool_no",addToolConfigureVo.getToolNo());
        DzToolCompensationData dzToolCompensationData = dzToolCompensationDataMapper.selectOne(wrapper);
        if(dzToolCompensationData!=null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_109);
        }
        data.setEquipmentId(addToolConfigureVo.getEquipmentId());
        data.setGroupNo(addToolConfigureVo.getGroupNo());
        data.setToolNo(addToolConfigureVo.getToolNo());
        int insert = dzToolCompensationDataMapper.updateById(data);
        return Result.ok(data);


    }

    @Override
    public Result getEquipmentByLine(Long lineId) {
        QueryWrapper<DzEquipment> eq = new QueryWrapper<DzEquipment>()
                .eq("line_id", lineId)
                .eq("equipment_type", FinalCode.TOOL_EQUIPMENT_CODE)
                .select("id","equipment_no","equipment_name");
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(eq);
        return Result.ok(dzEquipments);
    }

    @Override
    public Result getToolConfigureById(Integer id) {
        DzToolCompensationData dzToolCompensationData = dzToolCompensationDataMapper.selectById(id);
        return Result.ok(dzToolCompensationData);
    }

    @Override
    public Result addToolConfigureById(Long byEquipmentId) {


        //判断设备是否绑定过刀具信息
        QueryWrapper<DzToolCompensationData> eq = new QueryWrapper<DzToolCompensationData>()
                .eq("equipment_id", byEquipmentId);
        List<DzToolCompensationData> dzToolCompensationData = dzToolCompensationDataMapper.selectList(eq);
        if(dzToolCompensationData.size()>0){
           log.error("该设备已经有绑定的刀具信息了，无法批量绑定");
           return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //判断设备是否是机床
        DzEquipment dzEquipment = dzEquipmentMapper.selectById(byEquipmentId);
        if(dzEquipment==null){
            log.error("刀具信息批量绑定，设备id不存在：{}",byEquipmentId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        if(dzEquipment.getEquipmentType().intValue()!=FinalCode.TOOL_EQUIPMENT_CODE){
            log.error("刀具信息批量绑定，要绑定的设备类型不是机床：{}",dzEquipment.getEquipmentType());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //创建批量绑定的对象
        List<DzToolCompensationData>dataList=new ArrayList<>();
            //查询所有刀具组
        List<DzToolGroups> dzToolGroups = dzToolGroupsMapper.selectList(new QueryWrapper<DzToolGroups>());
        List<Integer> collect = dzToolGroups.stream().map(p -> p.getGroupNo()).collect(Collectors.toList());
        //根据产线id查询订单id
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(dzEquipment.getLineId());
        if(dzProductionLine==null){
            log.error("刀具信息批量绑定，设备绑定的产线不存在，id：{}",dzEquipment.getLineId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //查询所有刀具组的刀具信息
        List<DzToolInfo> tool_groups_id = dzToolInfoMapper.selectList(new QueryWrapper<DzToolInfo>().in("tool_groups_id", collect));
        for (DzToolGroups dzToolGroup:dzToolGroups) {
            for (DzToolInfo dzToolInfo:tool_groups_id) {
                if(dzToolInfo.getToolGroupsId().intValue()==dzToolGroup.getToolGroupsId()){
                    DzToolCompensationData data=new DzToolCompensationData();
                    data.setEquipmentId(byEquipmentId);
                    data.setGroupNo(dzToolGroup.getGroupNo());
                    data.setToolNo(dzToolInfo.getToolNo());
                    data.setToolLife(0);
                    data.setToolLifeCounter(0);
                    data.setToolLifeCounterType(0);
                    data.setToolGeometryX(new BigDecimal("0"));
                    data.setToolGeometryY(new BigDecimal("0"));
                    data.setToolGeometryZ(new BigDecimal("0"));
                    data.setToolGeometryC(new BigDecimal("0"));
                    data.setToolGeometryRadius(new BigDecimal("0"));
                    data.setToolWearX(new BigDecimal("0"));
                    data.setToolWearY(new BigDecimal("0"));
                    data.setToolWearZ(new BigDecimal("0"));
                    data.setToolWearC(new BigDecimal("0"));
                    data.setToolWearRadius(new BigDecimal("0"));
                    data.setToolNoseDirection(0);
                    data.setOrgCode(dzEquipment.getOrgCode());
                    data.setOrderId(dzProductionLine.getOrderId());
                    data.setLineId(dzEquipment.getLineId());
                    dataList.add(data);
                }
            }
        };
        if(dataList.size()>0){
            boolean b = dzToolCompensationDataService.saveBatch(dataList);
        }
        return Result.ok();
    }
}
