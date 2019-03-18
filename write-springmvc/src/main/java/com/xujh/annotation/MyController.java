package com.xujh.annotation;

import java.lang.annotation.*;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {

    /**
     * 表示给controller注册别名
     * @return
     */
    String value() default "";

}
