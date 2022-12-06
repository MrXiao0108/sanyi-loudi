package com.dzics.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ShiftProductionDetailsDo {
   private List<String> name;
    private List<Long>dataSum;
}
