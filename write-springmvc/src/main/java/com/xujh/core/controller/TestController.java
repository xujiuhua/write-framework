package com.xujh.core.controller;

import com.xujh.annotation.MyController;
import com.xujh.annotation.MyRequestMapping;
import com.xujh.annotation.MyRequestParam;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
@MyController
@MyRequestMapping("/test")
public class TestController {

    @MyRequestMapping("a")
    public void a(@MyRequestParam("code") String code) {
        System.out.println("code:" + code);
    }

}
