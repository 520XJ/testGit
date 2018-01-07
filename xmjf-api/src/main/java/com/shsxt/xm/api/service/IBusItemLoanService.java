package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.BusItemLoan;

/**
 * Created by lp on 2017/12/11.
 */
public interface IBusItemLoanService {
    public BusItemLoan queryBusItemLoanByItemId(Integer itemId);
}
