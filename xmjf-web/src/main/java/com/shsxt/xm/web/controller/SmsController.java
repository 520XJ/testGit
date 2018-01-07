package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.ISmsService;
import com.shsxt.xm.api.utils.RandomCodesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/sms")
public class SmsController {

    @Resource
    private IBasUserService basUserService;

    @Resource
    private ISmsService smsService;

    /**
     *  发送手机验证码 短信
     * @param phone     手机号
     * @param picCode
     * @param type       验证类型
     * @param session
     * @return
     */
    @RequestMapping("sendPhoneSms")
    @ResponseBody
    public ResultInfo sendPhoneSms(String phone, String picCode, Integer type, HttpSession session){
        ResultInfo resultInfo=new ResultInfo();
        // P2PConstant.PICTURE_VERIFY_CODE  图像验证码值存入session中的key
        String sessionPicCode= (String) session.getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        if(StringUtils.isBlank(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);//状态码值
            resultInfo.setMsg("验证码已失效!");
            return  resultInfo;
        }

        //比较 存在session 中的图像验证码值 和前台传过来的图像验证码值
        if(!picCode.equals(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("验证码不匹配!");
            return  resultInfo;
        }

        //电话号码是否注册过  未注册才能发送注册验证
        BasUser user = basUserService.queryBasUserByPhone(phone);
        if(null!=user){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("该手机已注册!");
            return  resultInfo;
        }

        try {
            //code   短信验证码值
            String code= RandomCodesUtils.createRandom(true,4);
            System.out.println("短信验证码值code:"+code);
            //注释掉  smsService.sendPhoneSms(phone,code,type);  调用发送短信  因为没钱充值 阿里大于
            // smsService.sendPhoneSms(phone,code,type);
            //  手机验证码 存入session
            session.setAttribute(P2PConstant.PHONE_VERIFY_CODE+phone,code);
            // 发送手机验证码当前时间存入session
            session.setAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone,new Date());
            Date time= (Date) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
            System.out.println("短信发送时间time:"+time);
            //设置返回信息   异常则返回catch中的
            resultInfo.setCode(P2PConstant.OPS_SUCCESS_CODE);
            resultInfo.setMsg(P2PConstant.OPS_SUCCESS_MSG);
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }




}
