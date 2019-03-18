package com.xujh.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
@Data
@ConfigurationProperties("simple.starter")
public class SimpleProperties {

    private String greet;

}
