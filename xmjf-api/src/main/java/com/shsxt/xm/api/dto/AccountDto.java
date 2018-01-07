package com.shsxt.xm.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**、
 *  报表专用
 */
public class AccountDto  implements Serializable{
    private static final long serialVersionUID = 4768816348646914733L;
    private String name;
    public BigDecimal y;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "name='" + name + '\'' +
                ", y=" + y +
                '}';
    }
}
