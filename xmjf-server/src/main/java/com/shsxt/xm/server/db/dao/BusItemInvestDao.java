package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.dto.InvestDto;
import com.shsxt.xm.api.po.BusItemInvest;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusItemInvestDao extends BaseDao<BusItemInvest> {

    /**
     * 查询用户是否投资过新手标
     * @param userId
     * @return
     */
    public  Integer queryUserIsInvestIsNewItem(@Param("userId")Integer userId);

    /**
     *  通过userId 查询出该用户近5个月来的投资记录
     * @param userId
     * @return
     */
    public List<InvestDto> queryInvestInfoByUserId(@Param("userId")Integer userId);
}