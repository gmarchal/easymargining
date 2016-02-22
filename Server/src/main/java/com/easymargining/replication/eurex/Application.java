package com.easymargining.replication.eurex;

import com.easymargining.replication.eurex.config.MongoConfiguration;
import com.easymargining.replication.eurex.config.WebSecurityConfiguration;
import com.easymargining.replication.eurex.converter.TradeFileHandler;
import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import com.easymargining.replication.eurex.domain.services.marketdata.EurexMarketDataEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// VM parameter : -Xms4g -Xmx8g

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

        String valuationDate = "2015-06-03";  // Default Valuation Date.
        if ( args.length > 1  && args[0].equals("-vd")) {
            valuationDate = args[1]; // Valuation Date
        }

        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        
        log.info("Let's inspect the beans provided by Spring Boot:");
        
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.info(beanName);
        }

        log.info("Server started - URL : http://localhost:" +
                ctx.getEnvironment().getProperty("server.port"));

        // Initialize EurexMarketDataEnvironment
        LocalDate s_valuationDate = LocalDate.parse(valuationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);
        EurexMarketDataEnvironment.init(s_valuationDate);
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
}
