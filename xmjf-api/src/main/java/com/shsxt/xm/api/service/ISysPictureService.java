package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.SysPicture;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lp on 2017/12/11.
 */
public interface ISysPictureService {
    public List<SysPicture> querySysPicturesByItemId(Integer itemId);

}
