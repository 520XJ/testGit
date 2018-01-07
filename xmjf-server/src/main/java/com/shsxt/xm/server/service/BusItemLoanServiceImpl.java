package com.shsxt.xm.server.service;

import com.shsxt.xm.api.po.BusItemLoan;
import com.shsxt.xm.api.service.IBusItemLoanService;
import com.shsxt.xm.server.db.dao.BusItemLoanDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lp on 2017/12/11.
 */
@Service
public class BusItemLoanServiceImpl  implements IBusItemLoanService{
    @Resource
    private BusItemLoanDao busItemLoanDao;
    @Override
    public BusItemLoan queryBusItemLoanByItemId(Integer itemId) {
        return busItemLoanDao.queryBusItemLoanByItemId(itemId);
    }
}
