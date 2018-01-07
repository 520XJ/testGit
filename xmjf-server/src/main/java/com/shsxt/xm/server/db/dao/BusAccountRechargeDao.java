package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusAccountRechargeDao extends BaseDao<BusAccountRecharge> {

    /**
     *  通过 订单id 查询  用户充值记录表
     * @param orderNo   订单id
     * @return
     */
    BusAccountRecharge queryBusAccountRechargeByOrderNo(@Param("orderNo") String orderNo);



}