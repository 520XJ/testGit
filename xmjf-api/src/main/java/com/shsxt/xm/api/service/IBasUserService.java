package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.BasUser;

public interface IBasUserService {

    /**
     *  通过id 查询 用户表
     * @param id
     * @return
     */
    public BasUser queryBasUserById(Integer id);

    /**
     *   注册时 通过手机号 查询是否注册过
     * @param phone   手机号
     * @return   BasUser类
     */
    public BasUser queryBasUserByPhone(String phone);

    /**
     *  注册一切验证通过  保存用户记录
     * @param phone
     * @param password
     */
    public  void saveBasUser(String phone, String password);

    /**
     * 快捷登录
     * @param phone
     * @return
     */
    public BasUser quickLogin(String phone);

    /**
     * 手机号+密码登录
     * @param phone
     * @param password
     * @return
     */
    public  BasUser userLogin(String phone, String password);


}
