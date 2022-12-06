package com.dzics.common.model.write;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class UpWorkWrite implements Converter {
    @Override
    public Class supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Integer integer = (Integer) value;
        String x = "";
        if (integer != null) {
            if (integer.intValue() == 0) {
                x = "未上报";
            } else if (integer.intValue() == 1) {
                x = "已上报";
            } else if(integer.intValue() == 3){
                x = "上报异常";
            }
        }
        return new CellData(x);
    }
}
