package com.shsxt.xm.api.service;

import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUserSecurity;

/**
 * Created by lp on 2017/12/11.
 */
public interface IBasUserSecurityService {

    /**
     *  通过userid   查询  BasUserSecurity
     * @param userId
     * @return
     */
    public BasUserSecurity queryBasUserSecurityByUserId(Integer userId);

    /**
     *    通过userId 查询用户认证状态
     * @param userId   用户id
     * @return
     */
    public ResultInfo userAuthCheck(Integer userId);

    /**
     *    用户实名认证
     * @param realName
     * @param idCard
     * @param businessPassword
     * @param confirmPassword
     * @return
     */
    public ResultInfo userAuth(String realName,String idCard,String businessPassword,String confirmPassword,Integer userId );


}
