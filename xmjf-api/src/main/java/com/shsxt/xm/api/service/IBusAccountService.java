package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BusAccount;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by lp on 2017/12/11.
 */
public interface IBusAccountService {

    public BusAccount queryBusAccountByUserId(Integer userId);

    /**
     *
     * @param amount
     * @param bussinessPassword
     * @param userId
     * @return
     */
    public PayDto addRechargeRequestInfo(BigDecimal amount, String bussinessPassword, Integer userId);

    /**
     *  通过用户id 查询 资产
     *  返回 map 集合  报表使用
     * @param userId
     * @return
     */
    Map<String, Object> queryAccountInfoByUserId(Integer userId);
}
