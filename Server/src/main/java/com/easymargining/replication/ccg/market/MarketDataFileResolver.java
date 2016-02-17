package com.easymargining.replication.ccg.market;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Gilles Marchal on 08/12/2015.
 */
@Slf4j
public class MarketDataFileResolver {

    private final String valuationDate;
    private final String marketDataDirectory;
    private final String CLASSFILEFiLENAME = "ClassFile.xml";
    private final String RISKARRAYFiLENAME = "RiskArray.xml";


    public MarketDataFileResolver(String marketDataDirectory, LocalDate valuationDate) {
        this.valuationDate = valuationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.marketDataDirectory = String.format("ccg/%s/%s", new Object[]{marketDataDirectory, this.valuationDate});
    }

    public URL arrayFile(){
        return this.openFile(RISKARRAYFiLENAME, this.marketDataDirectory);
    }

    public URL classFile() {
        return this.openFile(CLASSFILEFiLENAME, this.marketDataDirectory);
    }

    private URL openFile(String templateName, String marketDataDirectory) {
        String template = String.format("%s/%s", new Object[]{marketDataDirectory, templateName});
        URL url = null;
        try {
            url = new ClassPathResource(template).getURL();
        } catch (IOException e) {
            //log.error("Error during URL creation for template : <file:" + template + ">" , e);
        }
        return url;
    }
}
