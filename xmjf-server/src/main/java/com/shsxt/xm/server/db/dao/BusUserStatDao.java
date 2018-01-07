package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusUserStat;
import com.shsxt.xm.api.po.BusUserStatKey;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusUserStatDao extends BaseDao<BusUserStat> {

    /**
     *  通过userId 查询用户统计表  BusUserStat
     * @param userId
     * @return
     */
    BusUserStat queryBusUserStatByUserId(@Param("userId") Integer userId);

}