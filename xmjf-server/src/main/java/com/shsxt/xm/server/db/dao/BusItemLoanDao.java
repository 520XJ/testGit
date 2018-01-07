package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusItemLoan;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusItemLoanDao extends BaseDao<BusItemLoan> {
    public  BusItemLoan queryBusItemLoanByItemId(@Param("itemId") Integer itemId);

}