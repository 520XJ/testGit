package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.base.BaseController;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.service.*;
import com.shsxt.xm.api.utils.PageList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("basItem")
public class   BasItemController extends BaseController {

    @Resource
    private IBasItemService basItemService;

    @Resource
    private IBusAccountService busAccountService;

    @Resource
    private IBasUserSecurityService basUserSecurityService;
    @Resource
    private IBusItemLoanService busItemLoanService;
    @Resource
    private ISysPictureService sysPictureService;

    @RequestMapping("list")
    public String toBasItemListPage() {
        return "item/invest_list";
    }


    /**
     *  项目 分页
     * @param basItemQuery
     * @return
     */
    @RequestMapping("queryBasItemsByParams")
    @ResponseBody
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery) {
        return basItemService.queryBasItemsByParams(basItemQuery);
    }

    /**
     * 待开放投资项目倒计时结束后调用
     * @param itemId   待开放投资项目的id值
     * @return
     */
    @RequestMapping("updateBasItemStatusToOpen")
    @ResponseBody
    public ResultInfo updateBasItemStatusToOpen(Integer itemId){
        ResultInfo resultInfo = new ResultInfo();
        //更新状态
        try {
            basItemService.updateBasItemStatusToOpen(itemId);
            resultInfo.setCode(P2PConstant.OPS_SUCCESS_CODE);
            resultInfo.setMsg(P2PConstant.OPS_SUCCESS_MSG);
        }catch (ParamsExcetion e){
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }


    /**
     *   项目详情
     * @param itemId   项目id
     * @param modelMap    存作用域的
     * @param request
     * @return
     */
    @RequestMapping("/itemDetailPage")
    public String itemDetailPage(Integer itemId, ModelMap modelMap, HttpServletRequest request){
        //通过itemId查询到该投资项目基本详情
        BasItemDto basItemDto = basItemService.queryBasItemByItemId(itemId);
       //从session中取出登陆成功存的用户对象
        BasUser basUser = (BasUser)request.getSession().getAttribute("userInfo");
        //判断是否为空  为空项目详情页显示注册   否则 投资
        if(null!=basUser){
            //查询用户信息   显示余额等。。
            BusAccount busAccount = busAccountService.queryBusAccountByUserId(basUser.getId());
           //存作用域
            modelMap.addAttribute("busAccount", busAccount);
        }
        //查询项目发起人的认证状态  支付等    bas_user_security 表  用户安全表
        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(basItemDto.getItemUserId());
        // 通过前台传来的项目id 查询项目借款信息表关于此项目的一切信息   bus_item_loan
        BusItemLoan busItemLoan = busItemLoanService.queryBusItemLoanByItemId(itemId);

        //  sys_picture     通过项目id   查询 认证信息   图片
        List<SysPicture> sysPictures = sysPictureService.querySysPicturesByItemId(itemId);
        modelMap.addAttribute("loanUser",basUserSecurity);
        modelMap.addAttribute("busItemLoan",busItemLoan);
        modelMap.addAttribute("item",basItemDto);
        modelMap.addAttribute("pics",sysPictures);
        return "item/details";
    }

}

