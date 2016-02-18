package com.easymargining.replication.eurex;

import com.easymargining.replication.eurex.config.MongoConfiguration;
import com.easymargining.replication.eurex.config.WebSecurityConfiguration;
import com.easymargining.replication.eurex.converter.TradeFileHandler;
import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.threeten.bp.LocalDate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
@Import(value = {
        DefaultConfig.class,
        MongoConfiguration.class,
        WebSecurityConfiguration.class
})
public class Application {

    @Autowired
    TradeFileHandler tradeFileHandler;

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        
        log.info("Let's inspect the beans provided by Spring Boot:");
        
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.info(beanName);
        }

        log.info("Server started - URL : http://localhost:" +
                ctx.getEnvironment().getProperty("server.port"));

        /*
        // Initialize Product Referential
        try {
            String userDir = System.getProperties().getProperty("user.dir");


            LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);
            List<URL> list = new ArrayList<URL>();
            list.add(new File(userDir + "/Server/src/main/resources/marketData/20150603/ETD/00theoinstpubli20150603aaa.txt").toURI().toURL());
            new ProductReferentialService().loadProducts(list, s_valuationDate);
        } catch( IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
}
