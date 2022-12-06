package com.dzics.common.model.request.mom;

import com.dzics.common.util.PageLimitBase;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年11月04日 13:24
 */
@Data
public class GetFeedingAgvVo extends PageLimitBase {
    /**
     * 产线Id
     * */
    private String lineId;

    /**
     * 投料点编号
     * */
    @ApiModelProperty("投料点编号")
    private String externalCode;
}
