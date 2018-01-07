package com.shsxt.xm.api.base;


import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 *          系统上下文
 * @author Administrator
 *
 */
public class BaseController {
	
	@ModelAttribute
	public void preMethod(HttpServletRequest request){
		//request.getContextPath()
		//获取的是项目的站点名点名
		request.setAttribute("ctx", request.getContextPath());
		
	}
}
