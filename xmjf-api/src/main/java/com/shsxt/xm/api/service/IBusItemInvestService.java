package com.shsxt.xm.api.service;

import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.utils.PageList;

import java.math.BigDecimal;
import java.util.Map;

public interface IBusItemInvestService {
    /**
     *  项目详情
     * @param busItemInvestQuery
     * @return
     */
    PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery);

    /**
     *       用户投资项目
     * @param itemId     项目id
     * @param amount      投资金额
     * @param businessPassword   交易密码
     * @param userId   用户id
     * @return    ResultInfo
     */
    void addBusItemInvest(Integer itemId, BigDecimal amount, String businessPassword, Integer userId);

    /**
     *   投资详情   报表
     * @param userId
     * @return
     */
    public Map<String,Object[]> queryInvestInfoByUserId(Integer userId);

}
