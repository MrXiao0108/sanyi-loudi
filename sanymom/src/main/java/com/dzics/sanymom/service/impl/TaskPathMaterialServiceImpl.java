package com.dzics.sanymom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomOrderPathMaterial;
import com.dzics.common.service.MomOrderPathMaterialService;
import com.dzics.sanymom.service.TaskPathMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/5/27.
 * @since
 */
@Slf4j
@Service
public class TaskPathMaterialServiceImpl implements TaskPathMaterialService {

    @Autowired
    private MomOrderPathMaterialService momOrderPathMaterialService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void savePathMaterials(List<MomOrderPathMaterial> materialList) {
        momOrderPathMaterialService.saveBatch(materialList);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void delByOrdPathIds(List<String> collect) {
        momOrderPathMaterialService.remove(new QueryWrapper<MomOrderPathMaterial>().in("working_procedure_id",collect));
    }
}
