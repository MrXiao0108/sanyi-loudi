package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.dto.check.LogPromptMsgDto;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.request.mom.AgvLogParms;
import com.dzics.common.model.request.mom.BackMomLogVo;
import com.dzics.common.model.response.mom.BackMomLogDo;
import com.dzics.common.model.response.mom.MomLogExcelDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-21
 */
@Mapper
public interface LogPromptMsgMomMapper extends BaseMapper<LogPromptMsgMom> {
    List<BackMomLogDo>getBackMonLog(BackMomLogVo backMomLogVo);

    List<MomLogExcelDo>getMomLogExcel(AgvLogParms logParms);

    /**
     * Mom日志查询
     * @return List<LogPromptMsgDto>
     */
    List<LogPromptMsgDto>getMomLogs(@Param("orderNo")String orderNo,@Param("createDate")String createDate, @Param("wipOrderNo")String wipOrderNo,
    @Param("pointCode")String pointCode,@Param("brief")String brief,@Param("beginTime")String beginTime,@Param("endTime")String endTime
            ,@Param("filed")String filed,@Param("type")String type);
}
