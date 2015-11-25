package com.socgen.finit.easymargin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

/**
 * Created by Gilles Marchal on 24/11/2015.
 */
@SpringBootApplication
@Import(value = { EurexMarginingServiceConfig.class } )
public class EurexMarginingServiceApplication {

    public static void main(String[] args) {

        final SpringApplication springApplication = new SpringApplication(EurexMarginingServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        ApplicationContext ctx =springApplication.run(args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

}
