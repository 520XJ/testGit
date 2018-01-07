package com.shsxt.xm.web.resolvers;

import com.alibaba.fastjson.JSON;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.AuthExcetion;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@Component
public class GlobalExceptionsResolver implements HandlerExceptionResolver{

    /**
     *
     * @param request  HttpServletRequest
     * @param response  HttpServletResponse
     * @param handler    Object
     * @param ex    异常  Exception
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        ModelAndView modelAndView=getDefaultModelAndView(request);

        // 用户未登录  转发到登录页面
        if(handler instanceof HandlerMethod){
            if(ex instanceof AuthExcetion){  //登陆异常  也就是未登陆
                try {
                    response.sendRedirect(request.getContextPath()+"/login"); //重定向到 登陆界面
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            HandlerMethod handlerMethod= (HandlerMethod) handler;
            Method method= handlerMethod.getMethod();
            ResponseBody responseBody= method.getAnnotation(ResponseBody.class); //获取到 方法 上是否有@ResponseBody 注解  该注解 返回 json串
            if(null!=responseBody){
                /**
                 * 方法响应内容为json
                 */
                ResultInfo resultInfo=new ResultInfo();
                //  默认错误信息  code 300  msg 操作失败
                resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
                resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
                if(ex instanceof ParamsExcetion){ //参数异常 ， 参数校验异常
                    ParamsExcetion pe= (ParamsExcetion) ex;
                    resultInfo.setCode(pe.getErrorCode());
                    resultInfo.setMsg(pe.getErrorMsg());
                }
                /**
                 * 响应json 到浏览器
                 */
                response.setCharacterEncoding("utf-8");   //编码格式
                response.setContentType("application/json;charset=utf-8");  // 返回浏览器数据格式 json 编码格式 utf-8
                PrintWriter pw=null;    //流
                try {
                    pw= response.getWriter();     //获取响应流
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(null!=pw){
                        pw.close(); //关闭流
                    }
                }
                return null;
            }else{ //没有  @ResponseBody 注解
                /**
                 * 方法响应信息为视图
                 */
                if(ex instanceof ParamsExcetion){
                    ParamsExcetion pe= (ParamsExcetion) ex;
                    modelAndView.addObject("msg",pe.getErrorMsg());
                    modelAndView.addObject("code",pe.getErrorCode());
                }
                // authexception
                return modelAndView;
            }

        }else{
            return getDefaultModelAndView(request);
        }
    }
    public ModelAndView getDefaultModelAndView(HttpServletRequest request) {
        ModelAndView modelAndView=new ModelAndView("error");
        modelAndView.addObject("ctx",request.getContextPath());
        modelAndView.addObject("msg", P2PConstant.OPS_FAILED_MSG);
        modelAndView.addObject("code",P2PConstant.OPS_FAILED_CODE);
        return modelAndView;
    }

}
