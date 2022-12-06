package com.dzics.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class GetOutputByLineIdDo {

    private List<Long> list;
    List<String>dateList;
    private List<Long> ng;
}
