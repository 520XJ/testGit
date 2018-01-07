package com.shsxt.xm.api.model;

import com.shsxt.xm.api.constant.P2PConstant;

import java.io.Serializable;

/**
 *    ajax请求返回
 */
public class ResultInfo implements Serializable{
    private static final long serialVersionUID = 6479704509276746170L;
    //状态码   默认200
    private Integer code = P2PConstant.OPS_SUCCESS_CODE;
    //信息     默认操作成功
    private String msg = P2PConstant.OPS_SUCCESS_MSG;
    //
    private Object result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
