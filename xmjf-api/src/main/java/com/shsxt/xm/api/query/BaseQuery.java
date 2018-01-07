package com.shsxt.xm.api.query;

import java.io.Serializable;

/**
 * pageNum   页数   1默认
 * pageSize   每页多少数据   10默认
 */
public class BaseQuery implements Serializable {
    private static final long serialVersionUID = 3209946156249306220L;
    private Integer pageNum=1;
    private Integer pageSize=10;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
