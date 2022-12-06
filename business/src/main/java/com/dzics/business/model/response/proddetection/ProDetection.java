package com.dzics.business.model.response.proddetection;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检测设置表格数据
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class ProDetection implements Serializable {
    @ApiModelProperty("表头")
    private List<HeaderClom> tableColumn;
    @ApiModelProperty("行数据")
    private List<Map<String, Object>> tableData;

    public ProDetection() {
        tableColumn = new ArrayList<>();
        HeaderClom headerClom1 = new HeaderClom();
        headerClom1.setColData("order_no");
        headerClom1.setColName("订单编号");
        tableColumn.add(headerClom1);
        HeaderClom headerClom2 = new HeaderClom();
        headerClom2.setColData("detector_time");
        headerClom2.setColName("检测时间");
        tableColumn.add(headerClom2);
        HeaderClom headerClom4 = new HeaderClom();
        headerClom4.setColData("productName");
        headerClom4.setColName("检测产品名称");
        tableColumn.add(headerClom4);
        HeaderClom headerClom5 = new HeaderClom();
        headerClom5.setColData("product_no");
        headerClom5.setColName("产品ID");
        tableColumn.add(headerClom5);
//        HeaderClom headerClom6 = new HeaderClom();
//        headerClom6.setColData("departName");
//        headerClom6.setColName("站点名称");
//        tableColumn.add(headerClom6);
        HeaderClom headerClom7 = new HeaderClom();
        headerClom7.setColData("detectionResult");
        headerClom7.setColName("检测结果");
        tableColumn.add(headerClom7);
        HeaderClom headerClom8 = new HeaderClom();
        headerClom8.setColData("produc_barcode");
        headerClom8.setColName("二维码");
        tableColumn.add(headerClom8);
    }
}
