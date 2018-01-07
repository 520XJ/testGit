package com.shsxt.xm.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  支付 回调信息 model
 */
public class CallBackDto implements Serializable{

    private static final long serialVersionUID = 2248396723975331056L;

    /**
     *  充值金额
     */
    private BigDecimal totalFee;


    /**
     *  订单编号
     */
    private String outOrderNo;


    /**
     *
     */
    private String tradeNo;

    /**
     * 签名
     */
    private String sign;

    /**
     *
     */
    private String tradeStatus;

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CallBackDto{" +
                "totalFee=" + totalFee +
                ", outOrderNo='" + outOrderNo + '\'' +
                ", tradeNo='" + tradeNo + '\'' +
                ", sign='" + sign + '\'' +
                ", tradeStatus='" + tradeStatus + '\'' +
                '}';
    }
}
