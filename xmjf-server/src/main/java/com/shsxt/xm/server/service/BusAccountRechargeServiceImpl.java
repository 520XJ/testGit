package com.shsxt.xm.server.service;

import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.constant.YunTongFuConstant;
import com.shsxt.xm.api.dto.CallBackDto;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.api.po.BusAccountLog;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.service.IBusAccountRechargeService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.Md5Util;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.server.db.dao.BasUserDao;
import com.shsxt.xm.server.db.dao.BusAccountDao;
import com.shsxt.xm.server.db.dao.BusAccountLogDao;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

@Service
public class BusAccountRechargeServiceImpl implements IBusAccountRechargeService {

    @Resource
    private BasUserDao basUserDao;

    @Resource
    private BusAccountRechargeDao busAccountRechargeDao;

    @Resource
    private BusAccountDao busAccountDao;

    @Resource
    private BusAccountLogDao busAccountLogDao;

    @Override
    public void updateBusAccountRecharge(CallBackDto callBackDto, Integer userId) {

        /**                   步骤
         * 1.基本参数校验
         * 用户是否登录校验
         * 金额校验
         * 订单号
         * 签名
         * 结果
         * 2.签名合法性校验
         * 3.订单是否支付成功校验
         * 4.订单存在性校验
         * 5.订单状态校验 未支付订单方可进行状态更新
         * 6.订单金额是否相等校验
         * 7.订单状态更新
         * 8.充值日志记录添加
         */

        //1, 判断用户是否在登陆状态
        BasUser basUser = basUserDao.queryById(userId);
        AssertUtil.isTrue(userId == null||null==basUser, "用户未登陆");

        //2,判断参数 是否为空，  缺一不可
        AssertUtil.isTrue(StringUtils.isBlank(callBackDto.getOutOrderNo())||
                        StringUtils.isBlank(callBackDto.getSign())||
                        StringUtils.isBlank(callBackDto.getTradeNo())||
                        callBackDto.getTotalFee()==null,
                        P2PConstant.OPS_FAILED_MSG);
        //3-1组装签名
        // 3-2对签名进行加密
        //3-3比对一致性
        // 将 out_order_no、total_fee、trade_status、商户 PID、商户 KEY 的值连接
        //起来，进行 md5 加密，
        // * 而后与 sign 进行对比，如果相同则通知验证结果是正确，如果不相同则可能数据被篡
        //改，不要进行业务处理，
        // * 验证通过之后再判断 trade_status 是否等于 TRADE_STATUS_SUCCESS，相等则支付成功，
        //如果不等则支付失败。
        String sign = callBackDto.getOutOrderNo()+callBackDto.getTotalFee()+callBackDto.getTradeStatus()
                + YunTongFuConstant.PARTNER+YunTongFuConstant.KEY;
        Md5Util md5Util = new Md5Util();
        sign = md5Util.encode(sign, null);
        AssertUtil.isTrue(!sign.equals(callBackDto.getSign()), "签名被非法篡改，交易异常，请联系客服");

        AssertUtil.isTrue(!callBackDto.getTradeStatus().equals(YunTongFuConstant.TRADE_STATUS_SUCCESS),P2PConstant.OPS_FAILED_MSG);

        //通过 callBackDto.getTradeNo() 订单号    查询数据库中是否存在该订单
        BusAccountRecharge busAccountRecharge = busAccountRechargeDao.queryBusAccountRechargeByOrderNo(callBackDto.getOutOrderNo());

        AssertUtil.isTrue(null==busAccountRecharge,"该订单不存在，请联系客服");
        //判断 订单状态值
        AssertUtil.isTrue(!busAccountRecharge.getStatus().equals(2)   //审核中
                                ||busAccountRecharge.getStatus().equals(1)  //成功
                                ||busAccountRecharge.getStatus().equals(0)  //失败
                                ,"订单状态异常，请联系客服");

        // compareTo() 将此 BigDecimal 与指定的 BigDecimal 比较。 当此 BigDecimal 在数字上小于、等于或大于 val 时，返回 -1、0 或 1。
        //valueOf()   将非BigDecimal类数字 转换为BigDecimal类
        //判断 订单金额和到账金额
        //？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        AssertUtil.isTrue(busAccountRecharge.getRechargeAmount().compareTo(callBackDto.getTotalFee())!=0
                                //||!busAccountRecharge.getActualAmount().equals(BigDecimal.valueOf(0))
                                 ,"订单金额异常，请联系客服!");

        //更新状态 为 成功
        busAccountRecharge.setStatus(1);
        //设置充值金额，到账金额   因为没有手续费   充值到账金额为100%
        //充值费用 为0 不用设置
        busAccountRecharge.setRechargeAmount(callBackDto.getTotalFee());
        busAccountRecharge.setActualAmount(callBackDto.getTotalFee());
        //设置审核时间
        busAccountRecharge.setAuditTime(new Date());

        //getLocalHost()  本地主机的 IP 地址  返回格式 电脑名/ip
        // getHostAddress()  返回字符串格式的原始 IP 地址
        /*try {
            InetAddress localHost = InetAddress.getLocalHost();
            //设置ip
            busAccountRecharge.setAddip(localHost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }*/

        //更新用户充值记录表
        AssertUtil.isTrue(busAccountRechargeDao.update(busAccountRecharge) < 1, P2PConstant.OPS_FAILED_MSG);

        //通过userId 用户id 查询 账户表
        BusAccount busAccount = busAccountDao.queryBusAccountByUserId(userId);
        //多余判断
        AssertUtil.isTrue(basUser==null,"系统异常");

        // 设置总金额   可用余额    可提现金额
        busAccount.setTotal(busAccount.getTotal().add(callBackDto.getTotalFee()));
        busAccount.setUsable(busAccount.getUsable().add(callBackDto.getTotalFee()));
        busAccount.setCash(busAccount.getCash().add(callBackDto.getTotalFee()));
        //更新账户表
        AssertUtil.isTrue(busAccountDao.update(busAccount)<1,P2PConstant.OPS_FAILED_MSG);

        //用户资金记录表  BusAccountLog
        BusAccountLog busAccountLog = new BusAccountLog();

        busAccountLog.setOperType("user_recharge_success"); //操作类型
        busAccountLog.setOperMoney(callBackDto.getTotalFee()); //操作金额
        busAccountLog.setBudgetType(1); //类型：1-收入，2-支出
        busAccountLog.setCash(busAccount.getCash());// 设置可提现金额
        busAccountLog.setFrozen(busAccount.getFrozen());  //冻结金额
        busAccountLog.setRemark("用户充值");  //备注
        busAccountLog.setRepay(busAccount.getRepay()); //待还金额
        busAccountLog.setTotal(busAccount.getTotal()); //总金额
        busAccountLog.setUsable(busAccount.getUsable()); //可用金额
        busAccountLog.setUserId(userId); //用户id userId
        busAccountLog.setWait(busAccount.getWait()); //待收金额
        busAccountLog.setAddtime(new Date());
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            //设置ip
            busAccountLog.setAddip(localHost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println(e+"1");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e+"2");
        }
        //添加 用户资金记录表
        AssertUtil.isTrue(busAccountLogDao.insert(busAccountLog)<1,P2PConstant.OPS_FAILED_MSG);

    }

    @Override
    public PageList queryBusAccountRechargeByUserId(BusAccountRecharge busAccountRecharge) {
        PageHelper.startPage(busAccountRecharge.getPageNum(),busAccountRecharge.getPageSize());
        List<BusAccountRecharge> busAccountRecharges = busAccountRechargeDao.queryForPage(busAccountRecharge);
         return new PageList(busAccountRecharges);
    }


}