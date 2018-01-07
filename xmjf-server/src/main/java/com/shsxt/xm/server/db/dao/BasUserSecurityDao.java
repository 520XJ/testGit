package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BasUserSecurityDao extends BaseDao<BasUserSecurity> {


    BasUserSecurity queryBasUserSecurityByUserId(@Param("userId") Integer userId);

    /**
     *    查询用户认证状态
     * @param userId
     * @return
     */
    BasUserSecurity userAuthCheck(@Param("userId") Integer userId);

    /**
     *  通过 身份证号码   查询
     * @param idCard   身份证号码
     * @return
     */
    BasUserSecurity queryBasUserSecurityByIdCard(String idCard);

}