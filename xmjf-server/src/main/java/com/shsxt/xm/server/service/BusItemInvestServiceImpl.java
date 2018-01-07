package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.ItemStatus;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.dto.InvestDto;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.IBusItemInvestService;
import com.shsxt.xm.api.utils.*;
import com.shsxt.xm.server.db.dao.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusItemInvestServiceImpl implements IBusItemInvestService {

    @Resource
    private IBasUserService basUserService;

    @Resource
    private IBasUserSecurityService basUserSecurityService;

    @Resource
    private BasItemDao basItemDao;

    @Resource
    private BusAccountDao busAccountDao;

    @Resource
    private BusItemInvestDao busItemInvestDao;

    @Resource
    private BusUserStatDao busUserStatDao;

    @Resource
    private BusAccountLogDao busAccountLogDao;

    @Resource
    private BusIncomeStatDao busIncomeStatDao;

    @Resource
    private BusUserIntegralDao busUserIntegralDao;

    @Resource
    private BusIntegralLogDao busIntegralLogDao;

    @Override
    public PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery) {
        return null;
    }

    @Override
    public void addBusItemInvest(Integer itemId, BigDecimal amount, String businessPassword, Integer userId) {
        /**
         * 投标业务代码实现思路(不考虑加息券 红包情况)
         1.参数基本校验
         用户是否登录校验
         交易密码校验
         投资项目存在性校验
         是否为移动端校验(仅限非移动端)
         投资项目开放性校验
         投资项目是否满标校验
         账户金额合法性校验
         投资金额合法性校验
         小于账户可用余额
         项目剩余金额合法性校验
         最小投资金额存在
         剩余金额>最小投资金额
         最小投资存在
         投资金额>=最小投资金额
         最大投资存在
         投资金额<=最大投资金额
         新手标重复投资记录校验
         2.投资涉及表
         bas_item         项目表
         bus_item_invest  项目投资表
         bus_user_stat    用户统计表
         bus_account      用户账户表
         bus_account_log  用户账户操作日志表
         bus_income_stat   用户收益信息表
         bus_user_integral  用户积分表
         bus_integral_log  积分操作日志表
         3.表更新业务
         3.1 用户投资记录信息更新
         添加投资记录信息
         3.2 用户统计信息更新
         更新投资次数
         投资投资累计金额
         3.3 用户账户信息更新
         更新账户字段信息
         总金额
         可用金额
         可提现金额
         冻结金额
         代收金额
         3.4 用户账户操作日志表
         更新金额字段信息
         3.5 用户收益信息表字段更新
         总收益
         已赚收益
         代收收益
         3.6 用户积分表更新
         增加用户100 积分
         3.7 用户积分操作表更新
         记录积分信息变化  添加积分增加记录日志
         3.8 基本项目表信息更新
         更新项目投资进度
         项目进行中金额
         4.返回操作结果
         */

        //验证参数合法性
        checkInvestParams(itemId,amount,businessPassword,userId);

        BusItemInvest busItemInvest = new BusItemInvest();
        busItemInvest.setActualCollectAmount(BigDecimal.ZERO); //实际已收总额	做冗余，给客户查看使用，当前收款情况
        busItemInvest.setActualCollectInterest(BigDecimal.ZERO); //实际已收利息	做冗余，给客户查看使用，当前收款情况
        busItemInvest.setActualCollectPrincipal(BigDecimal.ZERO); //实际未收本金	做冗余，给客户查看使用，当前收款情况

        //通过项目id 查询 本项目的信息
        BasItem basItem = basItemDao.queryById(itemId);
        //调用利息计算
        BigDecimal lx = Calculator.getInterest(amount, basItem);

        busItemInvest.setActualUncollectAmount(amount.add(lx)); //实际未收总额	做冗余，给客户查看使用，当前收款情况  本金 + 利息
        busItemInvest.setActualUncollectInterest(lx); //实际未收利息	做冗余，给客户查看使用，当前收款情况
        busItemInvest.setActualUncollectPrincipal(amount); //实际未收本金	做冗余，给客户查看使用，当前收款情况
        busItemInvest.setAdditionalRateAmount(BigDecimal.ZERO); //加息券收益 0
        busItemInvest.setAddtime(new Date()); //新增时间
        busItemInvest.setCollectAmount(amount.add(lx)); //应收总额 本金 + 利息
        busItemInvest.setCollectInterest(lx); //应收利息
        busItemInvest.setCollectPrincipal(amount); //应收本金
        busItemInvest.setInvestAmount(amount); //投资金额
        busItemInvest.setInvestCurrent(1); //活期定期 1-定期 2-活期
        /**
         *  回调代码过滤
         */
        busItemInvest.setInvestDealAmount(amount); //实际成交金额
        String oderNo="SXT_TZ_"+ RandomCodesUtils.createRandom(false,11); //生成编号   false 不是纯数字  11 长度
        busItemInvest.setInvestOrder(oderNo); //投资订单号
        busItemInvest.setInvestStatus(0); //投资状态 0-默认状态 1复审通过 2失败 3-投资已还款
        busItemInvest.setInvestType(1);// pc 端投资
        busItemInvest.setItemId(itemId); //项目id
        busItemInvest.setUpdatetime(new Date()); //更新时间
        busItemInvest.setUserId(userId); //用户 投资人id
        //调用 dao 层 添加 投资记录
        AssertUtil.isTrue(busItemInvestDao.insert(busItemInvest)<1, P2PConstant.OPS_FAILED_MSG);

        //通过userId 查询 用户统计表
        BusUserStat busUserStat = busUserStatDao.queryBusUserStatByUserId(userId);
        busUserStat.setInvestCount(busUserStat.getInvestCount()+1); //投资累计次数
        busUserStat.setInvestAmount(busUserStat.getInvestAmount().add(amount)); //投资累计金额
        //更新 用户统计表
        AssertUtil.isTrue(busUserStatDao.update(busUserStat)<1,P2PConstant.OPS_FAILED_MSG);

        //通过userid 查询 账户表
        BusAccount busAccount= busAccountDao.queryBusAccountByUserId(userId);
        busAccount.setTotal(busAccount.getTotal().add(lx)); //总金额
        //BigDecimal.negate()返回一个BigDecimal，其值是(-this)，其标度是this.scale() 比如 1  返回-1
        busAccount.setUsable(busAccount.getUsable().add(amount.negate())); //可用金额
        busAccount.setCash(busAccount.getCash().add(amount.negate())); //可提现金额
        busAccount.setFrozen(busAccount.getFrozen().add(amount)); //冻结金额
        busAccount.setWait(busAccount.getWait().add(amount)); //待收金额
        //更新
        AssertUtil.isTrue(busAccountDao.update(busAccount)<1,P2PConstant.OPS_FAILED_MSG);

        //用户资金记录表
        BusAccountLog busAccountLog = new BusAccountLog();
        busAccountLog.setUserId(userId); //用户id
        busAccountLog.setOperType("用户投标"); //操作类型
        busAccountLog.setOperMoney(amount); //操作金额
        busAccountLog.setBudgetType(2); //类型：1-收入，2-支出
        busAccountLog.setTotal(busAccount.getTotal()); //总金额
        busAccountLog.setUsable(busAccount.getUsable()); //可用金额
        busAccountLog.setFrozen(busAccount.getFrozen()); //冻结金额
        busAccountLog.setWait(busAccount.getWait()); //待收金额
        busAccountLog.setCash(busAccount.getCash()); //可提现金额
        busAccountLog.setRepay(busAccount.getRepay()); //待还金额
        busAccountLog.setRemark("用户投标成功!"); //备注
        busAccountLog.setAddtime(new Date()); //添加时间
        //差个 trade_user_id  交易对方
        //添加
        AssertUtil.isTrue(busAccountLogDao.insert(busAccountLog)<1,P2PConstant.OPS_FAILED_MSG);

        //通过userId 查询  bus_income_stat 收益表
        BusIncomeStat busIncomeStat=busIncomeStatDao.queryBusIncomeStatByUserId(userId);
        busIncomeStat.setWaitIncome(busIncomeStat.getWaitIncome().add(lx)); //待收收益
        busIncomeStat.setTotalIncome(busIncomeStat.getTotalIncome().add(lx)); //总收益
        //更新
        AssertUtil.isTrue(busIncomeStatDao.update(busIncomeStat)<1,P2PConstant.OPS_FAILED_MSG);

        //通过userId 查询 用户积分表 bus_user_integral
        BusUserIntegral busUserIntegral=busUserIntegralDao.queryBusUserIntegralByUserId(userId);
        busUserIntegral.setTotal(busUserIntegral.getTotal()+100); //总积分
        busUserIntegral.setUsable(busUserIntegral.getUsable()+100); //可用积分
        //更新
        AssertUtil.isTrue(busUserIntegralDao.update(busUserIntegral)<1,P2PConstant.OPS_FAILED_MSG);

        //new  BusIntegralLog  积分记录表
        BusIntegralLog busIntegralLog=new BusIntegralLog();
        busIntegralLog.setWay("用户投标"); //来源
        busIntegralLog.setUserId(userId); //userId
        busIntegralLog.setStatus(0); //收入-0支出-1
        busIntegralLog.setAddtime(new Date()); //添加时间
        //添加
        AssertUtil.isTrue(busIntegralLogDao.insert(busIntegralLog)<1,P2PConstant.OPS_FAILED_MSG);

        basItem.setItemOngoingAccount(basItem.getItemOngoingAccount().add(amount)); //进行中金额
        basItem.setInvestTimes(basItem.getInvestTimes()+1); //投标次数
        //比较  basItem.getItemAccount()借款金额 与 basItem.getItemOngoingAccount()进行中金额 是否相等
        if(basItem.getItemAccount().compareTo(basItem.getItemOngoingAccount())==0){
            basItem.setItemStatus(ItemStatus.FULL_COMPLETE); //设置 项目状态 为20 满标
        }
        MathContext mc = new MathContext(2, RoundingMode.HALF_DOWN);
        basItem.setItemScale(basItem.getItemOngoingAccount().divide(basItem.getItemAccount(),mc).multiply(BigDecimal.valueOf(100))); //项目进行进度
        //更新
        AssertUtil.isTrue(basItemDao.update((BasItemDto) basItem)<1,P2PConstant.OPS_FAILED_MSG);

    }

    @Override
    public Map<String, Object[]> queryInvestInfoByUserId(Integer userId) {
        Map<String,Object[]> map=new HashMap<String,Object[]>();
        List<InvestDto> investDtos= busItemInvestDao.queryInvestInfoByUserId(userId);
        String[] months;//存放月份
        BigDecimal[] totals;
        if(!CollectionUtils.isEmpty(investDtos)){
            months=new String[investDtos.size()];
            totals=new BigDecimal[investDtos.size()];
            for(int i=0;i<investDtos.size();i++){
                InvestDto investDto=investDtos.get(i);
                months[i]=investDto.getMonth();
                totals[i]=investDto.getTotal();
            }
            map.put("data1",months);
            map.put("data2",totals);
        }
        return map;
    }


    public void checkInvestParams( Integer itemId, BigDecimal amount, String businessPassword,Integer userId){

        /*
        1.参数基本校验
                用户是否登录校验
        交易密码校验
                投资项目存在性校验
        是否为移动端校验(仅限非移动端)
        投资项目开放性校验
                投资项目是否满标校验
        账户金额合法性校验
                投资金额合法性校验
        小于账户可用余额
                项目剩余金额合法性校验
        最小投资金额存在
        剩余金额>最小投资金额
        最小投资存在
        投资金额>=最小投资金额
        最大投资存在
        投资金额<=最大投资金额
        新手标重复投资记录校验
        */

        //  查询bas_user 表判断  用户是否存在
        AssertUtil.isTrue(basUserService.queryBasUserById(userId)==null
                                ||userId==0
                                ||userId==null,"用户不存在或未登陆");
        //查询用户安全信息表  bas_user_security    判断 加密后的交易密码是否一致
        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(userId);
        AssertUtil.isTrue(!basUserSecurity.getPaymentPassword().equals(MD5.toMD5(businessPassword)),"交易密码错误");

        //通过项目id itemId  查询 BasItemDto
        BasItem basItem = basItemDao.queryById(itemId);
        AssertUtil.isTrue(itemId==null
                                ||!basItem.getId().equals(itemId)
                                ||0==itemId,"项目不存在");
        AssertUtil.isTrue(basItem.getItemIsalive().equals(0),"项目已失败，不能进行投资");//项目的存活状态：1-正常发标 0-项目失败',
        //判断 投资金额
        AssertUtil.isTrue(null==amount||amount.compareTo(BigDecimal.ZERO)<=0,"投资金额非法!");
        AssertUtil.isTrue(basItem.getMoveVip().equals(1),"移动端专享项目，pc端不能进行投资操作!");
        AssertUtil.isTrue(!basItem.getItemStatus().equals(10),"该项目处理未开放状态，暂时不能执行投资操作!");
        AssertUtil.isTrue(basItem.getItemStatus().equals(20),"项目已满标，不能再进行投资操作!");

        BusAccount busAccount = busAccountDao.queryBusAccountByUserId(userId);
        //判断用户可用余额是否为 0   当此 BigDecimal 在数字上小于、等于或大于 val 时，返回 -1、0 或 1。
        // compareTo（）将此 BigDecimal 与指定的 BigDecimal 比较
        AssertUtil.isTrue(busAccount.getUsable().compareTo(BigDecimal.ZERO)<=0,"账户金额不存在，请先执行充值操作!");
        BigDecimal singleMinInvestmet=basItem.getItemSingleMinInvestment();  //项目最少投资金额
        //判断  用户余额大于0的条件下    是否小于本项目的单笔最少投资金额
        if(singleMinInvestmet.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(busAccount.getUsable().compareTo(singleMinInvestmet)<0,"账户余额小于当前投资项目最小投资金额，请先执行充值操作!");
        }

        //判断项目单笔最少投资金额大于 0 的条件下
        if(singleMinInvestmet.compareTo(BigDecimal.ZERO)>0) {
            //判断 投资金额 + 项目已投金额 是否大于 项目借款金额
            AssertUtil.isTrue((basItem.getItemOngoingAccount().add(amount)).compareTo(basItem.getItemAccount()) > 0, "该笔投资已超出该项目所剩的可投资金额");
            AssertUtil.isTrue(amount.compareTo(singleMinInvestmet)<0,"单笔投资不能小于最小投资金额");
        }
        BigDecimal singleMaxInvestment=basItem.getItemSingleMaxInvestment();
        if(singleMaxInvestment.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(amount.compareTo(singleMaxInvestment)>0,"投资金额不能大于单笔投资最大金额!");
        }
        //判断用户是否投资过新手标
        AssertUtil.isTrue(busItemInvestDao.queryUserIsInvestIsNewItem(userId)>0,"新手标不能进行重复投资操作!");
    }
}