package com.dzics.sanymom.model;

import com.dzics.sanymom.exception.CustomMomException;
import com.dzics.sanymom.model.response.ResponseStatusMessage;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultDto implements Serializable {
    //版本   必填
    private int version;
    //任务ID  随机生成32位UUID，单次下发指令唯一标识   必填
    private String taskId;
    //返回结果   0：正确；其它：错误   必填
    private String code;
    //返回消息
    private String msg;
    //返回结果集
    private String returnData;

    public static ResultDto error() {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode("1");
        resultDto.setMsg(ResponseStatusMessage.BUST_SERVICE);
        return resultDto;
    }

    public static ResultDto error(CustomMomException e) {
        ResultDto resultDto = new ResultDto();
        resultDto.setCode("1");
        resultDto.setTaskId(e.getTaskId());
        resultDto.setVersion(e.getVersion());
        resultDto.setMsg(e.getMessage());
        return resultDto;
    }
}
