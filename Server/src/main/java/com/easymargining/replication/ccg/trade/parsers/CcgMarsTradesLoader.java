package com.easymargining.replication.ccg.trade.parsers;

import com.easymargining.replication.ccg.trade.CcgMarsMarginTradeItem;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
@Service
@Slf4j
public class CcgMarsTradesLoader {

    public List<CcgMarsMarginTradeItem> readTradeFile(URL file) throws IOException {
        log.info("Read CCG trade file " + file);
        // BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
        BeanListProcessor<CcgMarsMarginTradeItem> rowProcessor = new BeanListProcessor<>(CcgMarsMarginTradeItem.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new FileReader(file.getFile()));

        // The BeanListProcessor provides a list of objects extracted from the input.
        List<CcgMarsMarginTradeItem> beans = rowProcessor.getBeans();
        log.info("CCG Trade file " + file + ": " + beans.size() + " trades.");
        return beans;
    }

}
