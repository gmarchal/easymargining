package com.socgen.finit.easymargin.converter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.socgen.finit.easymargin.model.TradeEntity;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.testng.collections.Maps;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Read / write trade file
 * Created by Mederic on 23/11/2015.
 */
@Service
@Slf4j
public class TradeFileHandler {

    public List<TradeEntity> readTradeFile(URL file) throws IOException {
        log.info("Read trade file " + file);
        // BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
        BeanListProcessor<TradeEntity> rowProcessor = new BeanListProcessor<TradeEntity>(TradeEntity.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new FileReader(file.getFile()));

        // The BeanListProcessor provides a list of objects extracted from the input.
        List<TradeEntity> beans = rowProcessor.getBeans();
        log.info("Trade file " + file + ": " + beans.size() + " trades.");
        return beans;
    }

    public void writeTradeFile(List<TradeEntity> tradeEntities, URL file) throws IOException {
        log.info("Write " + tradeEntities.size() +  " trades in file " + file);
        // BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
        CsvWriterSettings csvWriterSettings = new CsvWriterSettings();
        csvWriterSettings.setRowWriterProcessor(new BeanWriterProcessor<>(TradeEntity.class));
        csvWriterSettings.setHeaders("Product ID", "Expiry Year", "Expiry Month", "Expiry Day", "Version Number", "Product Settlement Type", "Call Put Flag", "Exercise Price", "Exercise Style Flag", "Instrument Type", "Assigned/Notified Balance", "Exercised/Allocated Balance", "Long Balance", "Short Balance");
        CsvWriter writer = new CsvWriter(new FileWriter(file.getFile()), csvWriterSettings);

        // Write the record headers of this file
        writer.writeHeaders();

        // Here we just tell the writer to write everything and close the given output Writer instance.
        writer.processRecords(tradeEntities);

        writer.close();
        log.info("Done.");
    }

}
