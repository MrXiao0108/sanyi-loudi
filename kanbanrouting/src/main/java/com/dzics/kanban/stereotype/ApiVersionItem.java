package com.dzics.kanban.stereotype;

import lombok.Data;

/**
 * @author neverend
 */
@Data
public class ApiVersionItem implements Comparable<ApiVersionItem> {

    /**
     * 大版本升级
     */
    private int high = 1;
    /**
     * 正常的业务迭代版本号
     */
    private int mid = 0;

    /**
     * 主要针对bug 修复版本
     */
    private int low = 0;

    public ApiVersionItem() {

    }

    @Override
    public int compareTo(ApiVersionItem right) {
        if (this.getHigh() > right.getHigh()) {
            return 1;
        } else if (this.getHigh() < right.getHigh()) {
            return -1;
        }

        if (this.getMid() > right.getMid()) {
            return 1;
        } else if (this.getMid() < right.getMid()) {
            return -1;
        }

        if (this.getLow() > right.getLow()) {
            return 1;
        } else if (this.getLow() < right.getLow()) {
            return -1;
        }
        return 0;
    }
}
