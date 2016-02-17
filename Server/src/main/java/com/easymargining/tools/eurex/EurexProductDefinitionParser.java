package com.easymargining.tools.eurex;

import com.easymargining.replication.eurex.domain.model.EurexProductDefinition;
import com.easymargining.replication.eurex.domain.model.EurexTradeEntity;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by gmarchal on 17/02/2016.
 */
@Slf4j
public class EurexProductDefinitionParser {

    public static List<EurexProductDefinition> parse(URL file) throws IOException {
        log.info("Read Eurex Product Definition file " + file);
        // BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
        BeanListProcessor<EurexProductDefinition> rowProcessor = new BeanListProcessor<>(EurexProductDefinition.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new FileReader(file.getFile()));

        // The BeanListProcessor provides a list of objects extracted from the input.
        List<EurexProductDefinition> beans = rowProcessor.getBeans();
        log.info("Eurex Product Definition file " + file + ": " + beans.size() + " products.");

        return beans;
    }
}
