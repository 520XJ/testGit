package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

public interface BusAccountDao extends BaseDao<BusAccount> {

    /**
     * 通过userId 用户id 查询 账户表
     * @param userId
     * @return
     */
    public BusAccount queryBusAccountByUserId(@Param("userId") Integer userId);

    /**
     *    用户 资产详情 报表
     * @param userId
     * @return
     */
    public Map<String,BigDecimal> queryAccountInfoByUserId(@Param("userId") Integer userId);
}