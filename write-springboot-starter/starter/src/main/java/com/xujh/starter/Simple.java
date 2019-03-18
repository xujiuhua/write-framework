package com.xujh.starter;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Simple {
    private String greet;

    public Simple(String greet) {
        this.greet = greet;
    }

    public String greet(String name) {
        return String.format("%s, %s", this.greet, name);
    }
}
