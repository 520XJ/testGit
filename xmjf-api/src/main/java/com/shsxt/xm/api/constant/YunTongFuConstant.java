package com.shsxt.xm.api.constant;

/**
 * Created by lp on 2017/12/12.
 */
public class YunTongFuConstant {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    /**
     * 合作身份者PID，签约账号，由16位纯数字组成的字符串，请登录商户
     */
    public final static String  PARTNER = "1122781913193591";

    /**
     *  MD5密钥，安全检验码，由数字和字母组成的32位字符串，请登录商户后
     */
    public final static String KEY = "e9ebb566168042cc8ad454e6c1d4f9d2";

    /**
     * 商户号（8位数字)
     */
    public final static String  USER_SELLER = "74242483";

    /**
     *  服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可
     */
    public final static String  NOTIFY_URL = "http://127.0.0.1:8080/account/callback";

    /**
     * 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    public final static String  RETURN_URL = "http://127.0.0.1:8080/account/callback";

    /**
     * 支付地址，必须外网可以正常访问
     */
    public final static String  GATEWAY_NEW = "http://payment.passpay.net/PayOrder/payorder";

    /**
     * 充值状态
     */
    public static final String TRADE_STATUS_SUCCESS="TRADE_SUCCESS";
}
