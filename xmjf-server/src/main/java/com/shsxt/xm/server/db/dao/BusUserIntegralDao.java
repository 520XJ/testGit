package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusUserIntegral;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusUserIntegralDao extends BaseDao<BusUserIntegral> {

    BusUserIntegral queryBusUserIntegralByUserId(@Param("userId") Integer userId);
}