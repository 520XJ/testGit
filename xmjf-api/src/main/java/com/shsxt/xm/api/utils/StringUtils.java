package com.shsxt.xm.api.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pg on 15/11/17.
 */
public class StringUtils {

    //判断字符串是否为空
    public static boolean isNull(String string){
        if(("").equals(string)||null==string){
            return true;
        }else
            return false;
    }




    //获取当前时间：YYYYMMDDHHMMSS
    public static String getFullTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return df.format(new Date());
    }

    /**
     *
     * @param length timestamp后带的随机码的长度
     * @return 生成一串订单号
     */
    public static String getOrderNo(int length){
        //生成统一订单号
        return getTimeStamp()+(int)(Math.random()*Math.pow(10, length));
    }

    /**
     *
     *
     * @return 生成一串订单号 没有参数，带4位随机码
     */
    public static String getOrderNo(){
        //生成统一订单号
        String orderNo = getTimeStamp()+(int)(Math.random()*10000);
        return orderNo;
    }

    //生成addtime的timestamp格式
    public static String getTimeStamp(){
        return Long.toString(DateUtils.getTimeStampSeconds());
    }



    //使字符串其中符号改成*
    public static String getString(String s){
        //手机号码
        if(s==null){
            return "*";
        }
        if(s.length()==11){
            s=s.substring(0,3)+"****"+s.substring(7);
            return s;
        //15身份证
        }else if(s.length()==15){
            s=s.substring(0,4)+"*******"+s.substring(11);
            return s;
        //18位身份证
        }else if(s.length()==18){
            s=s.substring(0,4)+"**********"+s.substring(14);
            return s;
        //2位或者3位姓名
        }else if(s.length()==2||s.length()==3){
            s=s.substring(0,1)+"**";
            return s;
        //4位姓名
        }else if(s.length()==4){
            s=s.substring(0,2)+"**";
            return s;
        }else{
            int l=s.length();
            int quar=l/4,half=l/2;
            int end=l-quar;
            String halfStr="";
            for(int i=0;i<half;i++){
                halfStr=halfStr+"*";
            }
            for(int i=0;i<half;i++){

            }
            String ns=s.substring(0,quar)+halfStr+s.substring(end);

            return ns;

        }
    }

}
