package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BasUserDao extends BaseDao<BasUser> {
    /**
     *  注册时查询该手机号是否注册过
     * @param phone
     * @return
     */
    public BasUser queryBasUserByPhone(@Param("phone") String phone);

}