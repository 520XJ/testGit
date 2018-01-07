package com.shsxt.test;

import com.shsxt.xm.api.utils.DateUtils;
import com.shsxt.xm.api.utils.MD5;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class md5Test {
    public static void main(String[] args) {
        System.out.println(MD5.toMD5("123456"));
        BigDecimal bigDecimal = BigDecimal.valueOf(0);
        System.out.println(bigDecimal);
        BigDecimal a = BigDecimal.valueOf(1);
        boolean equals = BigDecimal.valueOf(0.00).equals(0.00);
        System.out.println(a);
        System.out.println(equals);


        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            System.out.println(hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Date date = DateUtils.setDayLast(new Date());
        Date date1 = new Date();
        System.out.println(date1);
        System.out.println(date);
    }
}
