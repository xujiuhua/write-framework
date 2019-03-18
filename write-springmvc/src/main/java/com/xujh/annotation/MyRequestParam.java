package com.xujh.annotation;

import java.lang.annotation.*;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {

    /**
     * 表示参数的别名，必填
     * @return
     */
    String value();

}
