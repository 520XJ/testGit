package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.MD5;
import com.shsxt.xm.server.db.dao.BasUserDao;
import com.shsxt.xm.server.db.dao.BasUserSecurityDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by lp on 2017/12/11.
 */
@Service
public class BasUserSecurityServiceImpl implements IBasUserSecurityService {
    @Resource
    private BasUserSecurityDao basUserSecurityDao;

    @Resource
    private BasUserDao basUserDao;

    @Override
    public BasUserSecurity queryBasUserSecurityByUserId(Integer userId) {
        return basUserSecurityDao.queryBasUserSecurityByUserId(userId);
    }

    @Override
    public ResultInfo userAuthCheck(Integer userId) {
        //查询
        BasUserSecurity basUserSecurity = basUserSecurityDao.queryBasUserSecurityByUserId(userId);
        //因为只有在登陆状态下才有充值按钮 因此不用做 结果的非空判断
        ResultInfo resultInfo = new ResultInfo();
        switch (basUserSecurity.getRealnameStatus()) {
            case 0:
                resultInfo.setCode(301);
                resultInfo.setMsg("用户未进行实名认证!");
                break;
            case 1:
                resultInfo.setCode(200);
                resultInfo.setMsg("该用户已认证!");
                break;
            case 2:
                resultInfo.setCode(302);
                resultInfo.setMsg("认证申请已提交,正在认证中。。。!");
                break;
        }
        return resultInfo;
    }

    /**
     * @param realName         真实姓名
     * @param idCard           身份证号码
     * @param businessPassword 交易密码
     * @param confirmPassword  确认密码
     * @return
     */
    @Override
    public ResultInfo userAuth(String realName, String idCard, String businessPassword, String confirmPassword, Integer userId) {
        ResultInfo resultInfo = new ResultInfo();

        //查询 该userid   是否存在
        BasUserSecurity basUserSecurity = basUserSecurityDao.queryBasUserSecurityByUserId(userId);
        //判断登陆状态
        BasUser basUser = basUserDao.queryById(userId);
        AssertUtil.isTrue(null==basUser||userId==null||basUserSecurity==null,P2PConstant.OPS_FAILED_MSG);

        //转为intrger类型 进行比较
        if (Integer.parseInt(businessPassword)!=Integer.parseInt(confirmPassword)) {
            resultInfo.setMsg("密码不一致");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        if (idCard.length() != 18 || idCard.length() > 18) {
            resultInfo.setMsg("身份证号码非法");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        if(realName==null||"".equals(realName.trim())){
            resultInfo.setMsg("用户名不能为空");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        //通过身份证号码   查询 是否被实名过
        BasUserSecurity basUserSecurity1 = basUserSecurityDao.queryBasUserSecurityByIdCard(idCard);

        AssertUtil.isTrue(basUserSecurity1!=null,"身份证号码已实名，请检查");

        basUserSecurity.setPaymentPassword(MD5.toMD5(businessPassword));    //加密后的密码
        basUserSecurity.setRealname(realName);      //真实姓名
        basUserSecurity.setIdentifyCard(idCard);    //身份证号
        basUserSecurity.setVerifyTime(new Date());  //时间
        basUserSecurity.setRealnameStatus(1);       //认证状态   0-未认证 1-已认证 2-已提交申请',
        basUserSecurity.setEmailStatus(0);       //  邮箱认证状态	0-未认证 1-已认证 2-已提交申请
        basUserSecurity.setPhoneStatus(0);       //手机认证状态	0-未认证 1-已认证 2-已提交申请

        if(basUserSecurityDao.update(basUserSecurity)<1){
            resultInfo.setMsg("认证失败");
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        resultInfo.setCode(P2PConstant.OPS_SUCCESS_CODE);
        resultInfo.setMsg(P2PConstant.OPS_SUCCESS_MSG);
            return resultInfo;

    }
}

