package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.base.BaseController;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.CallBackDto;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.service.IBusAccountRechargeService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.PageList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

/**
 *  充值
 */
@Controller
@RequestMapping("account")
public class BusAccountController extends BaseController {

    @Resource
    private IBusAccountService busAccountService;

    @Resource
    private IBusAccountRechargeService busAccountRechargeService;

    /**
     * 充值页面
     *
     * @return
     */
    @RequestMapping("rechargePage")
    public String toAccountRechargePage() {
        return "user/recharge";
    }


    /**
     * 点击充值 》》  验证》》跳转到支付页面
     *
     * @param amount            冲值多少   精度不能有误差   不建议使用 double   用BigDecimal
     * @param bussinessPassword 交易密码
     * @param picCode           图像验证码值
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("doAccountRechargeToRechargePage")
    public String doAccountRechargeToRechargePage(BigDecimal amount, String bussinessPassword, String picCode, Model model, HttpServletRequest request) {
        //从session中取出 图像验证码值
        String sessionPicCode = (String) request.getSession().getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        //验证码是否在有效期
        if (StringUtils.isBlank(sessionPicCode)) {
            System.out.println("验证码已失效!");
            return "user/recharge";
        }
        //不完善，后期优化
        //判断是否一致
        if (!picCode.equals(sessionPicCode)) {
            System.out.println("验证码不匹配!");
            return "user/recharge";
        }

        BasUser basUser = (BasUser) request.getSession().getAttribute("userInfo");
        PayDto payDto = busAccountService.addRechargeRequestInfo(amount, bussinessPassword, basUser.getId());
        model.addAttribute("pay", payDto);

        return "user/pay";
    }


    /**
     * 支付完成回调地址
     *
     * @param totalFee           金额
     * @param outOrderNo
     * @param sign               签名
     * @param tradeNo
     * @param tradeStatus
     * @param session
     * @param redirectAttributes 重定向时需要在 url 中拼接参数，
     *                           或者返回的页面需要传递 Model。
     *                           SpringMVC 用 RedirectAttributes 解决了这两个需要。
     *                           使用：如 return "redirect:/index";
     * @return
     * @RequestParam 指定参数名 取别名
     */
    @RequestMapping("callback")
    public String callback(@RequestParam("total_fee") BigDecimal totalFee,
                           @RequestParam("out_order_no") String outOrderNo,
                           String sign,
                           @RequestParam("trade_no") String tradeNo,
                           @RequestParam("trade_status") String tradeStatus,
                           HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println(totalFee);
        //从session中取出对象信息
        BasUser basUser = (BasUser) session.getAttribute("userInfo");

        CallBackDto callBackDto = new CallBackDto();
        callBackDto.setTotalFee(totalFee);
        callBackDto.setOutOrderNo(outOrderNo);
        callBackDto.setTradeNo(tradeNo);
        callBackDto.setSign(sign);
        callBackDto.setTradeStatus(tradeStatus);

        try {
            busAccountRechargeService.updateBusAccountRecharge(callBackDto, basUser.getId());
            redirectAttributes.addAttribute("result", "success");
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("result", "failed");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("result", "failed");
        }
        // 重定向到  account/result
        return "redirect:/account/result";
    }

    @RequestMapping("result")
    public String result(String result, Model model) {
        model.addAttribute("result", result);
        return "result";
    }

    @RequestMapping("rechargeRecodePage")
    public  String rechargeRecodePage(){
        return "user/recharge_record";
    }

    /**
     *    用户充值记录
     * @param session
     * @return
     */
    @RequestMapping("queryRechargeRecodesByUserId")
    @ResponseBody
    public PageList queryRechargeRecodesByUserId(HttpSession session){
        //从session中取出用户信息
        BasUser basUser= (BasUser) session.getAttribute("userInfo");
        BusAccountRecharge busAccountRecharge = new BusAccountRecharge();
        busAccountRecharge.setUserId(basUser.getId());
        return busAccountRechargeService.queryBusAccountRechargeByUserId(busAccountRecharge);
    }

    /**
     *  用户点击我的账户 跳转页面
     * @return
     */
    @RequestMapping("accountInfoPage")
    public  String toAccountInfoPage(){
        return  "user/account_info";
    }

    /**
     *     资产详情   报表
     * @param session
     * @return
     */
    @RequestMapping("accountInfo")
    @ResponseBody
    public Map<String, Object> queryAccountInfoByUserId(HttpSession session){
        BasUser basUser= (BasUser) session.getAttribute("userInfo");
        return busAccountService.queryAccountInfoByUserId(basUser.getId());
    }
}