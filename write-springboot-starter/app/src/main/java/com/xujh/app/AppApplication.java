package com.xujh.app;

import com.xujh.starter.Simple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jiuhua.xu
 */
@SpringBootApplication
public class AppApplication implements CommandLineRunner {

    @Autowired
    private Simple simple;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String greet = simple.greet("Tom");
        System.out.println(greet);
    }
}
