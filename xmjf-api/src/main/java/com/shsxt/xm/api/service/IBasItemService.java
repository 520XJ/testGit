package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.utils.PageList;

public interface IBasItemService {

    /**
     * 投资项目
     * @param basItemQuery
     * @return
     */
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery);

    /**
     * 待开放投资项目倒计时结束后调用
     * @param
     */
    public void updateBasItemStatusToOpen(Integer itemId);


    /**
     *
     * @param itemId
     * @return
     */
    BasItemDto queryBasItemByItemId(Integer itemId);

}
