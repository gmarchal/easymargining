package com.easymargining.replication.eurex;

import com.easymargining.replication.eurex.config.MongoConfiguration;
import com.easymargining.replication.eurex.config.WebSecurityConfiguration;
import com.easymargining.replication.eurex.converter.TradeFileHandler;
import com.easymargining.replication.eurex.domain.services.marketdata.EurexMarketDataEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

// VM parameter : -Xms4g -Xmx8g

@Slf4j
@SpringBootApplication
@Import(value = {
        DefaultConfig.class,
        MongoConfiguration.class,
        WebSecurityConfiguration.class
})

/*
    Configuration for the main class.
    VM Options : -Xms4g -Xmx8g
    Program arguments : -vd 2015-11-23 -m D:\gmarchal\eurex-data\marketdata

 */
public class Application {

    private static final String VALUATION_DATE_OPTION = "vd";
    private static final String MARKET_DATA_DIR_OPTION = "m";

    @Autowired
    TradeFileHandler tradeFileHandler;

    public static void main(String[] args) throws ParseException {

        Options commandLineOptions = getCommandLineOptions();
        PosixParser commandLineParser = new PosixParser();
        CommandLine commandLine = commandLineParser.parse(commandLineOptions, args);

        LocalDate valuationDate = LocalDate.parse(commandLine.getOptionValue(VALUATION_DATE_OPTION),
                                                                             DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String marketDataDirectory = commandLine.getOptionValue(MARKET_DATA_DIR_OPTION);



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
        EurexMarketDataEnvironment.init(marketDataDirectory, valuationDate);
    }

    /**
     * Obtains the command-line options.
     *
     * @return the options
     */
    private static Options getCommandLineOptions() {
        Options options = new Options();

        Option valuationDateOption = new Option(VALUATION_DATE_OPTION, true, "Valuation date, yyyy-MM-dd");
        valuationDateOption.setRequired(true);
        options.addOption(valuationDateOption);

        Option marketDataDirOption = new Option(MARKET_DATA_DIR_OPTION, true, "Market data directory");
        marketDataDirOption.setRequired(true);
        options.addOption(marketDataDirOption);

        return options;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
}
