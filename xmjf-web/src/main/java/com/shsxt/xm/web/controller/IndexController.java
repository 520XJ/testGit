package com.shsxt.xm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.shsxt.xm.api.base.BaseController;


@Controller
public class IndexController extends BaseController{

    /**
     * 返回主页视图
     * @return
     */
    @RequestMapping("index")
    public  String index(){
        return "index";
    }

    /**
     * 返回登录视图
     * @return
     */
    @RequestMapping("login")
    public  String login(){
        return "login";
    }

    @RequestMapping("quickLoginPage")
    public  String quickLoginPage(){
        return "quick_login";
    }

    /**
     * 返回注册视图
     * @return
     */
    @RequestMapping("register")
    public  String register(){
        return "register";
    }
}
