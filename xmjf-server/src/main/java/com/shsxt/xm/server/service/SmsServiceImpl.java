package com.shsxt.xm.server.service;

import com.alibaba.fastjson.JSON;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.constant.TaoBaoConstant;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.ISmsService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements ISmsService{

    @Resource
    private IBasUserService basUserService;

    /**
     *  发送验证短信
     * @param phone
     * @param code
     * @param type
     */
    @Override
    public void sendPhoneSms(String phone,String code,Integer type) {

        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号非法!");
        /**
         *
         *   正则判断手机号非法    d待完善
         *   */
        AssertUtil.isTrue(StringUtils.isBlank(code),"手机号验证码不能为空!");
        AssertUtil.isTrue(null==type,"短信验证码类型不匹配!");
        //注册验证  P2PConstant.VERIFY_TYPE_REGISTER
        //登陆验证  P2PConstant.VERIFY_TYPE_LOGIN
        AssertUtil.isTrue(!type.equals(P2PConstant.VERIFY_TYPE_REGISTER)
                &&!type.equals(P2PConstant.VERIFY_TYPE_LOGIN),"短信验证码类型不匹配!");

        if(type.equals(P2PConstant.VERIFY_TYPE_REGISTER)){
            /**
             * 注册时用户手机号不能重复
             */
            BasUser basUser= basUserService.queryBasUserByPhone(phone);
            AssertUtil.isTrue(null!=basUser,"该手机号已注册!");
            //注释掉doSend()方法调用   是因为没钱   充值 阿里大于
            //doSend(phone,code, TaoBaoConstant.SMS_TEMATE_CODE_REGISTER);
        }
        if(type.equals(P2PConstant.VERIFY_TYPE_LOGIN)){
            //doSend(phone,code,TaoBaoConstant.SMS_TEMATE_CODE_lOGIN);
        }
    }



    public  void doSend(String phone, String code,String templateCode){
        TaobaoClient client = new DefaultTaobaoClient(TaoBaoConstant.SERVER_URL,
                TaoBaoConstant.APP_KEY,TaoBaoConstant.APP_SECRET);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("");
        req.setSmsType(TaoBaoConstant.SMS_TYPE);
        req.setSmsFreeSignName(TaoBaoConstant.SMS_FREE_SIGN_NAME);
        Map<String,String> map=new HashMap<String,String>();
        map.put("code",code);
        req.setSmsParamString(JSON.toJSONString(map));
        req.setRecNum(phone);
        req.setSmsTemplateCode(templateCode);
        AlibabaAliqinFcSmsNumSendResponse rsp = null;
        try {
           rsp = client.execute(req);
          AssertUtil.isTrue(!rsp.isSuccess(),"短信发送失败,请稍后再试!");
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
