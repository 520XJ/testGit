package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.AccountDto;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.MD5;
import com.shsxt.xm.api.utils.Md5Util;
import com.shsxt.xm.server.db.dao.BusAccountDao;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by lp on 2017/12/11.
 */
@Service
public class BusAccountServiceImpl implements IBusAccountService {

    @Resource
    private BusAccountDao busAccountDao;

    @Resource
    private BusAccountRechargeDao busAccountRechargeDao;

    @Resource
    private IBasUserSecurityService basUserSecurityService;


    @Override
    public BusAccount queryBusAccountByUserId(Integer userId) {
        return busAccountDao.queryBusAccountByUserId(userId);
    }

    @Override
    public PayDto addRechargeRequestInfo(BigDecimal amount, String bussinessPassword, Integer userId) {
        checkParams(amount,bussinessPassword,userId);
        /**
         * 构建支付请求参数信息
         */
        BusAccountRecharge busAccountRecharge=new BusAccountRecharge();
        busAccountRecharge.setAddtime(new Date());
        busAccountRecharge.setFeeAmount(BigDecimal.ZERO);
        String orderNo= com.shsxt.xm.api.utils.StringUtils.getOrderNo();
        busAccountRecharge.setOrderNo(orderNo);
        busAccountRecharge.setFeeRate(BigDecimal.ZERO);
        busAccountRecharge.setRechargeAmount(amount);
        busAccountRecharge.setRemark("PC端用户充值");
        busAccountRecharge.setResource("PC端用户充值");
        //订单状态 0失败   1成功  2 审核中
        busAccountRecharge.setStatus(2);
        busAccountRecharge.setType(3);
        busAccountRecharge.setUserId(userId);
        //将充值信息写入到  bus_account_recharge 用户充值记录表中
        AssertUtil.isTrue(busAccountRechargeDao.insert(busAccountRecharge)<1, P2PConstant.OPS_FAILED_MSG);
        PayDto payDto=new PayDto();
        payDto.setBody("PC端用户充值操作");
        payDto.setOrderNo(orderNo);
        payDto.setSubject("PC端用户充值操作");
        payDto.setTotalFee(amount);
        String md5Sign=buildMd5Sign(payDto);
        payDto.setSign(md5Sign);
        return payDto;
    }

    @Override
    public Map<String,Object> queryAccountInfoByUserId(Integer userId) {
        //通过userid 查询出报表所需数据
        Map<String,BigDecimal> map=busAccountDao.queryAccountInfoByUserId(userId);
        List<AccountDto> list=new ArrayList<AccountDto>();
        Map<String,Object> target=new HashMap<String,Object>();
        //组装数据
        // map.isEmpty()如果此映射未包含键-值映射关系，则返回 true
        //页就是说没有数据返回true
        if(null!=map&&!map.isEmpty()){
            // map.entrySet() 返回此映射中包含的映射关系的 set 视图
            for(Map.Entry<String,BigDecimal> entry:map.entrySet()){
                AccountDto accountDto=new AccountDto();
                accountDto.setName(entry.getKey());
                accountDto.setY(entry.getValue());
                list.add(accountDto);
                if(entry.getKey().equals("总金额")){
                    target.put("data2",entry.getValue());// 总资产
                }
            }
            target.put("data1",list);// 资金类型value


        }
        return target;
    }

    private String buildMd5Sign(PayDto payDto) {
        StringBuffer arg = new StringBuffer();
        if(!StringUtils.isBlank(payDto.getBody())){
            arg.append("body="+payDto.getBody()+"&");
        }
        arg.append("notify_url="+payDto.getNotifyUrl()+"&");
        arg.append("out_order_no="+payDto.getOrderNo()+"&");
        arg.append("partner="+payDto.getPartner()+"&");
        arg.append("return_url="+payDto.getReturnUrl()+"&");
        arg.append("subject="+payDto.getSubject()+"&");
        arg.append("total_fee="+payDto.getTotalFee().toString()+"&");
        arg.append("user_seller="+payDto.getUserSeller());
        String tempSign= StringEscapeUtils.unescapeJava(arg.toString());
        Md5Util md5Util=new Md5Util();
        return md5Util.encode(tempSign+payDto.getKey(),"");
    }

    /**
     *  参数校验
     * @param amount
     * @param bussinessPassword
     * @param userId
     */
    private void checkParams(BigDecimal amount, String bussinessPassword, Integer userId) {
        AssertUtil.isTrue(amount.compareTo(BigDecimal.ZERO)<=0,"充值金额非法!");
        BasUserSecurity basUserSecurity=basUserSecurityService.queryBasUserSecurityByUserId(userId);
        AssertUtil.isTrue(null==basUserSecurity,"用户未登录!");
        AssertUtil.isTrue(StringUtils.isBlank(bussinessPassword),"交易密码不能为空!");
        bussinessPassword= MD5.toMD5(bussinessPassword);
        AssertUtil.isTrue(!bussinessPassword.equals(basUserSecurity.getPaymentPassword()),"交易密码错误!");
    }
}
