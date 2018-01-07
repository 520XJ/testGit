package com.shsxt.xm.api.constant;

public class P2PConstant {
    public static final  Integer OPS_SUCCESS_CODE=200;
    public static final String   OPS_SUCCESS_MSG="操作成功";
    public static final  Integer OPS_FAILED_CODE=300;
    public static final String   OPS_FAILED_MSG="操作失败";

    //  图片验证码常量
    public  static  final  String PICTURE_VERIFY_CODE="XM_00001";

    // 手机验证码 session key
    public static final String  PHONE_VERIFY_CODE="XM_00002_";
    // 手机验证码失效时间 session key
    public static final String  PHONE_VERIFY_CODE_EXPIRE_TIME="XM_00003_";

    /**
     * 验证码类型  注册
     */
    public static final Integer VERIFY_TYPE_REGISTER = 1;

    /**
     * 验证码类型  登陆
     */
    public static final Integer VERIFY_TYPE_LOGIN = 2;




}
