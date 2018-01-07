package com.shsxt.xm.api.dto;

import com.shsxt.xm.api.po.BasItem;

import java.io.Serializable;


public class BasItemDto extends BasItem implements Serializable  {
    private static final long serialVersionUID = -1438917289162983359L;
    private Integer total;

    private Long syTime;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getSyTime() {
        return syTime;
    }

    public void setSyTime(Long syTime) {
        this.syTime = syTime;
    }

    @Override
    public String toString() {
        return "BasItemDto{" +
                "total=" + total +
                ", syTime=" + syTime +
                '}';
    }
}
