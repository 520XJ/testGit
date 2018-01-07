package com.shsxt.xm.api.exceptions;

/**
 *   自定义异常
 *   必须  extends RuntimeException
 */
public class ParamsExcetion extends RuntimeException{
    private static final long serialVersionUID = -6505163304504978876L;

    /**
     *  异常信息
     */
    private String errorMsg = "操作失败";

    /**
     * 异常信息状态码
     */
    private Integer errorCode = 300;

    /**
     *   两个构造器   为  AssertUtil  准备   不同需要使用不同的
     * @param errorMsg
     */

    public ParamsExcetion(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ParamsExcetion(String errorMsg, Integer errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
