package com.xujh.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
@Configuration
@ConditionalOnClass(Simple.class)
@EnableConfigurationProperties(SimpleProperties.class)
public class SimpleAutoConfigure {

    @Autowired
    private SimpleProperties properties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "simple.starter", value = "enabled", havingValue = "true")
    Simple simple (){
        return new Simple(properties.getGreet());
    }

}
