package com.dzics.common.model.request;

import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
public class ReqUploadBase64 {
    List<String> imgBase64;
}
