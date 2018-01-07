package com.shsxt.xm.web.proxy;

import com.shsxt.xm.web.annotations.OptLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  日志收集
 */
@Aspect
@Component
public class OptLogProxy {

    @Autowired
    private HttpServletRequest request;

    @Pointcut(value = "@annotation(com.shsxt.xm.web.annotations.OptLog)")
    public void pointCut(){}

    //环绕通知
    @Around(value = "pointCut()&&@annotation(optLog)")
    public Object aroudMethod(ProceedingJoinPoint joinPoint, OptLog optLog) throws Throwable {

        String module = optLog.module();
        String remark = optLog.remark();
        //获取方法名
        MethodSignature signature=(MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        String name = method.getName();//方法名
        // 返回 请求参数
        Map<String, String[]> params = request.getParameterMap();
        //方法开始执行时间
        long start = System.currentTimeMillis();
        //放行  方法执行  前置通知结束
        Object object = joinPoint.proceed();
        //方法执行结束时间
        long end = System.currentTimeMillis();
        System.out.println("方法执行时间"+(end-start)+"ms");
        return object;

    }

}
