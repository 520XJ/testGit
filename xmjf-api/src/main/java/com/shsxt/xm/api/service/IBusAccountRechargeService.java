package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.CallBackDto;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.utils.PageList;

import java.util.List;

public interface IBusAccountRechargeService {
    /**
     *      支付成功回调传回参数   写入数据库
     * @param callBackDto
     * @param userId
     */
    public void  updateBusAccountRecharge(CallBackDto callBackDto,Integer userId);

    /**
     *  通过userId  查询用户充值记录    查询多条数据
     * @param busAccountRecharge
     * @return    PageList
     */
    public PageList queryBusAccountRechargeByUserId(BusAccountRecharge busAccountRecharge);
}
