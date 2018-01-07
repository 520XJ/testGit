package com.shsxt.xm.api.utils;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright 2015-2019 Yunfucloud technology co., Ltd.
 * Created by fuxx on 15/11/2.
 */
public class PageList implements Serializable {
    private static final long serialVersionUID = -401798493772397222L;
    private Paginator paginator;
    private List list;

    public PageList(List list) {

        if(list instanceof Page){
            paginator=new Paginator((Page)list);
            this.list=list;
        }
    }

    /**
     *     redis   专用
     * @param page
     */
    public PageList(Page page) {
        paginator=new Paginator(page);
        this.list=page;
    }


    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
