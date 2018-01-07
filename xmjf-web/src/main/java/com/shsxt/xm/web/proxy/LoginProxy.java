package com.shsxt.xm.web.proxy;

import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.utils.AssertUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 登陆校验
 */
@Component
@Aspect
public class LoginProxy {

    @Autowired
    private HttpSession session;

    @Resource
    private IBasUserService basUserService;

    @Pointcut(value ="@annotation(com.shsxt.xm.web.annotations.IsLogin)")
    public  void cut(){}

    @Before(value = "cut()")
    public  void before(){
        BasUser basUser= (BasUser) session.getAttribute("userInfo");
        AssertUtil.isNotLogin(null==basUser||null==basUserService.queryBasUserById(basUser.getId()),"用户未登录");
    }

}
