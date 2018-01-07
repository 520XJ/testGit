package com.shsxt.xm.api.utils;


import com.shsxt.xm.api.exceptions.AuthExcetion;
import com.shsxt.xm.api.exceptions.ParamsExcetion;

import java.util.ArrayList;

public class AssertUtil {

	/**
	 *	Boolean 为true 抛异常
	 * @param flag     Boolean
	 * @param errorMsg   异常信息
	 */
	public static void isTrue(Boolean flag,String errorMsg) {
		if(flag){
			throw new ParamsExcetion(errorMsg);
		}
	}

	/**
	 *	Boolean 为true 抛异常
	 * @param flag  Boolean
	 * @param errorMsg  异常信息
	 * @param errorCode  异常状态码值
	 */
	public static void isTrue(Boolean flag,String errorMsg,Integer errorCode) {
		if(flag){
			throw new ParamsExcetion(errorMsg,errorCode);
		}
	}

	/**
	 *   Boolean 为true 抛异常
	 * @param flag  Boolean
	 * @param errorMsg  异常信息
	 */
	public static void isNotLogin(Boolean flag,String errorMsg) {
		if(flag){
			throw new AuthExcetion(errorMsg);
		}
	}

	
}
