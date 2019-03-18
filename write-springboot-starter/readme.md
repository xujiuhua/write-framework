[编写自己的SpringBoot-starter](https://www.cnblogs.com/yuansc/p/9088212.html)

### 原理步骤
step1: SpringBoot 在启动时会去依赖的starter包中寻找 `resources/META-INF/spring.factories` 文件，然后根据文件中配置的Jar包去扫描项目所依赖的Jar包，这类似于 Java 的 SPI 机制。

step2: 根据 spring.factories配置加载AutoConfigure类。

step3: 根据 @Conditional注解的条件，进行自动配置并将Bean注入Spring Context 上下文当中。

### 实现
**Step1**: 创建一个SpringBoot 项目，并添加下面两个依赖到pom.xml文件当中

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-autoconfigure</artifactId>
    </dependency>
</dependencies>
```
 `spring-boot-configuration-processor` 的作用是编译时生成 `spring-configuration-metadata.json` ，此文件主要给IDE使用。如当配置此jar相关配置属性在 application.yml ，你可以用ctlr+鼠标左键点击属性名，IDE会跳转到你配置此属性的类中。

我们日常使用的Spring官方的Starter一般采取spring-boot-starter-{name} 的命名方式，如 spring-boot-starter-web

而非官方的Starter，官方建议 artifactId 命名应遵循{name}-spring-boot-starter 的格式。 例如：ysc-spring-boot-starter

**Step2:** 编写我们的服务类Simple
```java
public class Simple {
    private String greet;

    public Simple(String greet) {
        this.greet = greet;
    }

    public String greet(String name) {
        return String.format("%s, %s", this.greet, name);
    }
}

```

**Step3:** 编写Properties
```java
@Data
@ConfigurationProperties("simple.starter")
public class SimpleProperties {

    private String greet;

}
 ```
 
**Step4:** 关键点
```java
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
```

```
@ConditionalOnClass，当classpath下发现该类的情况下进行自动配置。
@ConditionalOnMissingBean，当Spring Context中不存在该Bean时。
@ConditionalOnProperty(prefix = "simple.starter",value = "enabled",havingValue = "true")，当配置文件中simple.starter.enabled=true时。
```
