package com.dzics.common.model.request.line;

import com.dzics.common.util.PageLimit;
import lombok.Data;

/**
 * 产线管理列表参数
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */
@Data
public class LineParmsList extends PageLimit {
  private   String orderNo;
  private   String departName;
  private   String lineName;
  private   String lineType;
  private   Long lineId;
}
