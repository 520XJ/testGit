package com.shsxt.xm.web.controller;


import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBusItemInvestService;
import com.shsxt.xm.web.annotations.IsLogin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("busItemInvest")
public class BusItemInvestController {

    @Resource
    private IBusItemInvestService busItemInvestService;

    /**
     *       用户投资项目
     * @param itemId     项目id
     * @param amount      投资金额
     * @param businessPassword   交易密码
     * @param session
     * @return    ResultInfo
     */
    @IsLogin
    @RequestMapping("userInvest")
    @ResponseBody
    public ResultInfo userInvest(Integer itemId, BigDecimal amount, String businessPassword, HttpSession session){

        ResultInfo resultInfo = new ResultInfo();
        try {
            BasUser basUser=(BasUser)session.getAttribute("userInfo");
            busItemInvestService.addBusItemInvest(itemId, amount, businessPassword, basUser.getId());
            resultInfo.setCode(P2PConstant.OPS_SUCCESS_CODE);
            resultInfo.setMsg(P2PConstant.OPS_SUCCESS_MSG);
        }catch (ParamsExcetion e){
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }catch (Exception e){
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            return resultInfo;
        }
        return resultInfo;
    }

    @RequestMapping("queryInvestInfoByUserId")
    @ResponseBody
    public Map<String,Object[]> queryInvestInfoByUserId(HttpSession session){
        BasUser basUser= (BasUser) session.getAttribute("userInfo");
        return busItemInvestService.queryInvestInfoByUserId(basUser.getId());
    }

}
