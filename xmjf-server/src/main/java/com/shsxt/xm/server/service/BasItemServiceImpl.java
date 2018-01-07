package com.shsxt.xm.server.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.service.IBasItemService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.server.db.dao.BasItemDao;
import com.shsxt.xm.server.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class BasItemServiceImpl implements IBasItemService {

    @Resource
    private BasItemDao basItemDao;

    @Resource
    private RedisUtils redisUtils;
   /**
     *
     * @param basItemQuery
     * @return
     * ----------------------------- 加入redis 测试
     */
    @Override
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery) {

        /**   redis key
         *  basItemList : list类型数据  basItem model类
         */
        String key = "basItemList::pageNum"+basItemQuery.getPageNum()  //页数
                +"::pageSize::"+basItemQuery.getPageSize()             //每页多少数据
                +"::itemCycle::"+basItemQuery.getItemCycle()           //借款周期
                +"::itemType::"+basItemQuery.getItemType()              //项目类型 1.学车宝 2.车商宝 3.车贷宝 4.车易保
                +"::isHistory::"+basItemQuery.getIsHistory();           // 是否为历史项目  1-历史项目  0-可投标项目

        //从redis 中取出查询 相对应的数据
        List<Object> list = redisUtils.getList(key);
        if(list!=null&&list.size()>0){
            //实例化 page
            Page<BasItemDto> page = new Page<>();
            for(Object object:list){
                //将object 类型 转为  BasItemDto 类型
                page.add((BasItemDto)object);
            }
            System.out.println("------------byRedis---------");
            return new PageList(page);
        }

        //分页
        PageHelper.startPage(basItemQuery.getPageNum(),basItemQuery.getPageSize());
        //查询数据库
        List<BasItemDto> basItemDtos = basItemDao.queryForPage(basItemQuery);

        //判断非空   为未开放投资的项目设置倒计时
        if(!CollectionUtils.isEmpty(basItemDtos)){
            for (BasItemDto basItemDto:basItemDtos){
                //如果记录处于待开放的状态， 计算记录剩余时间  秒数
                //比较查询到的数据中  itemStatus属性为1的 也就是待开放
                if(basItemDto.getItemStatus().equals(1)){
                    //获取开放时间
                    Date releaseTime = basItemDto.getReleaseTime();
                    //减去现在的时间   得到间隔时间    秒数
                    long syTime = (releaseTime.getTime() - new Date().getTime())/1000;
                    basItemDto.setSyTime(syTime);
                }
            }
            /**
             * 加入缓存数据
             */
            redisUtils.setList(key,basItemDtos);
        }
        System.out.println("----------byMysql------------");
      return new PageList(basItemDtos);
    }


    @Override
    public void updateBasItemStatusToOpen(Integer itemId) {
        AssertUtil.isTrue(null == basItemDao.queryById(itemId),"待更新记录不存在");
        //更新项目状态  为 投资项目
        AssertUtil.isTrue(basItemDao.updateBasItemStatusToOpen(itemId)<1,"更新项目状态失败");

    }

    /**
     *   通过项目id 查询项目详情
     * @param itemId
     * @return
     */
    @Override
    public BasItemDto queryBasItemByItemId(Integer itemId) {
        return basItemDao.queryById(itemId);
    }
}
