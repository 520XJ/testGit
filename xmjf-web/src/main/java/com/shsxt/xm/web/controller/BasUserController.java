package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.base.BaseController;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.web.annotations.OptLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("user")
public class BasUserController extends BaseController{

    @Resource
    private IBasUserService basUserService;

    @Resource
    private IBasUserSecurityService basUserSecurityService;

    @RequestMapping("register")
    @ResponseBody
    public ResultInfo userRegister(String phone, String picCode, String code, String password, HttpSession session){
        ResultInfo resultInfo = new ResultInfo();
        //从session中取图片验证码值
        String sessionPicCode = (String)session.getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        if(StringUtils.isBlank(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("图形验证码已失效!");
            return  resultInfo;
        }
        //比较前台传过来的picCode图片验证码值  与session中村的图片验证码值
        if(!picCode.equals(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("图形验证码不匹配!");
            return  resultInfo;
        }
        // 从session中获取发送验证码时间
        Date sessionTime= (Date) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
        if(null==sessionTime){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码已失效!");
            return  resultInfo;
        }
        //获取现在的时间
        Date currTime=new Date();
        //现在的时间减去存在session中村的时间除去1000得到秒数
        long time=(currTime.getTime()-sessionTime.getTime())/1000;
        //大于 180秒    也就是3分钟
        if(time>180){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码已失效!");
            return  resultInfo;
        }
        //从session中取得与该手机相关联的 手机验证码值
        String sessionCode= (String) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
        if(!sessionCode.equals(code)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码不正确!");
            return  resultInfo;
        }
        try {
            basUserService.saveBasUser(phone,password);
            // 移除session 中存储的key 信息
            session.removeAttribute(P2PConstant.PICTURE_VERIFY_CODE);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
            resultInfo.setCode(P2PConstant.OPS_SUCCESS_CODE);
            resultInfo.setMsg(P2PConstant.OPS_SUCCESS_MSG);
        }catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }

        return resultInfo;
    }

    /**
     *  登陆
     * @param phone
     * @param password
     * @param session
     * @return
     */
    @OptLog
    @RequestMapping("userLogin")
    @ResponseBody
    public  ResultInfo userLogin(String phone,String password,HttpSession session){
        ResultInfo resultInfo=new ResultInfo();
        try {
            BasUser basUser= basUserService.userLogin(phone,password);
            session.setAttribute("userInfo",basUser);
            resultInfo.setCode(P2PConstant.OPS_SUCCESS_CODE);
            resultInfo.setMsg(P2PConstant.OPS_SUCCESS_MSG);
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }

    /**
     *  用户退出
     * @param request
     * @return
     */
    @RequestMapping("exit")
    public  String exit(HttpServletRequest request){
        request.getSession().removeAttribute("userInfo");
        return "login";
    }


    /**
     *    用户点击充值   判断用户实名认证状态
     * @return
     */
    @RequestMapping("userAuthCheck")
    @ResponseBody
    public ResultInfo userAuthCheck(HttpServletRequest request){
        //用户登陆状态才显示充值按钮   从session中取出用户信息
        BasUser basUser= (BasUser) request.getSession().getAttribute("userInfo");
        return basUserSecurityService.userAuthCheck(basUser.getId());
    }

    /**
     *   跳转用户实名认证页面
     * @return
     */
    @RequestMapping("auth")
    public String atch(){
        return "user/auth";
    }

    /**
     *  用户认证
     * @return
     */
    @RequestMapping("userAuth")
    @ResponseBody
    public ResultInfo userAuth(String realName,String idCard,String businessPassword,String confirmPassword,HttpSession session){
        ResultInfo resultInfo = new ResultInfo();
        //从session中取出用户信息
        BasUser basUser = (BasUser)session.getAttribute("userInfo");
        return basUserSecurityService.userAuth(realName,idCard,businessPassword,confirmPassword,basUser.getId());
    }

    /**
     *  测试 controller
     *  redis
     *
     */
    @RequestMapping("queryBasUserById")
    @ResponseBody
    public BasUser queryBasUserById(Integer id){
        return basUserService.queryBasUserById(id);
    }

}
