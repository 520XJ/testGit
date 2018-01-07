package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusIncomeStat;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusIncomeStatDao extends BaseDao<BusIncomeStat> {

    /**
     * 通过userId 查询  bus_income_stat 收益表
     * @param userId
     * @return
     */
    BusIncomeStat queryBusIncomeStatByUserId(@Param("userId") Integer userId);

}