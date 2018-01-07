package com.shsxt.xm.api.po;

import java.io.Serializable;

public class BusUserIntegral implements Serializable {
    private static final long serialVersionUID = -2942146831338960837L;
    private Integer id;

    private Integer userId;

    private Integer total;

    private Integer usable;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getUsable() {
        return usable;
    }

    public void setUsable(Integer usable) {
        this.usable = usable;
    }
}