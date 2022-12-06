package com.dzics.common.model.request.devicecheck;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CheckUpVo {

    @ApiModelProperty(value = "主键(添加不填，编辑必填)")
    private String checkItemId;

    @ApiModelProperty("设备类型(1检测设备,2机床,3机器人)")
    @NotNull(message ="设备类型不能为空" )
    private Integer deviceType;

    @ApiModelProperty("检测项名称")
    @NotEmpty(message ="检测项名称不能为空")
    private String checkName;

    @ApiModelProperty("检测类型")
    @Size(min = 1)
    private List<CheckTypeVo> checkTypeList;

}
