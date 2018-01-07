package com.shsxt.xm.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by lp on 2017/12/16.
 */
public class InvestDto  implements Serializable{

    private static final long serialVersionUID = -6765544734137592678L;

    private String month;
    private BigDecimal total;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
