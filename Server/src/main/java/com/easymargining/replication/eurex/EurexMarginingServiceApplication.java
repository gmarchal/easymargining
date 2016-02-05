package com.easymargining.replication.eurex;

import com.easymargining.replication.eurex.config.MongoConfiguration;
import com.easymargining.replication.eurex.config.WebSecurityConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

/**
 * Created by Gilles Marchal on 24/11/2015.
 */
@Slf4j
@SpringBootApplication
@Import(value = {
        EurexMarginingServiceConfig.class,
        MongoConfiguration.class,
        WebSecurityConfiguration.class
})
public class EurexMarginingServiceApplication {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(EurexMarginingServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        ApplicationContext ctx =springApplication.run(args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

        log.info("Server started - URL : http://localhost:" +
                ctx.getEnvironment().getProperty("server.port"));
    }

}
