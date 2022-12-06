package com.dzics.common.model.custom;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备生产数据对象
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Data
public class UpValueDevice implements Serializable {

    /**
     * 毛坯数量
     */
    private Long roughNum;
    /**
     * 总毛坯数量
     */
    private Long totalRoughNum;


    /**
     * 合格数量
     */
    private Long qualifiedNum;
    /**
     * 总合格数量
     */
    private Long totalQualifiedNum;

    /**
     * 生产数量
     */
    private Long workNum;
    /**
     * 总生产数
     */
    private Long totalNum;

    /**
     * 不良品数量
     */
    private Long badnessNum;
    /**
     * 总不良品数量
     */
    private Long totalBadnessNum;

}
