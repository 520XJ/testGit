package com.shsxt.xm.web.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD}) //方法声明
@Retention(RetentionPolicy.RUNTIME)   //运行期生效
@Documented //注解是否将包含在JavaDoc中
public @interface IsLogin {
}
