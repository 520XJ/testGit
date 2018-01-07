package com.shsxt.xm.api.po;

import java.io.Serializable;
import java.math.BigDecimal;

public class BusIncomeStat implements Serializable {
    private static final long serialVersionUID = -8106758115618794913L;
    private Integer id;

    private Integer userId;

    private BigDecimal totalIncome;

    private BigDecimal waitIncome;

    private BigDecimal earnedIncome;

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

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getWaitIncome() {
        return waitIncome;
    }

    public void setWaitIncome(BigDecimal waitIncome) {
        this.waitIncome = waitIncome;
    }

    public BigDecimal getEarnedIncome() {
        return earnedIncome;
    }

    public void setEarnedIncome(BigDecimal earnedIncome) {
        this.earnedIncome = earnedIncome;
    }
}