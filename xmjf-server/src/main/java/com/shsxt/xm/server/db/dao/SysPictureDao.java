package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.SysPicture;
import com.shsxt.xm.api.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPictureDao extends BaseDao<SysPicture> {
    public List<SysPicture> querySysPicturesByItemId(@Param("itemId") Integer itemId);

}