package com.easymargining.tools.eurex;

import com.easymargining.replication.eurex.domain.model.EurexProductDefinition;
import com.easymargining.replication.eurex.domain.model.EurexProductDefinitionParsedItem;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmarchal on 17/02/2016.
 */
@Slf4j
public class EurexProductDefinitionParser {

    public static List<EurexProductDefinition> parse(URL file) throws IOException {
        log.info("Read Eurex Product Definition file " + file);
        // BeanListProcessor converts each parsed row to an instance of a given class,
        // then stores each instance into a list.
        BeanListProcessor<EurexProductDefinitionParsedItem> rowProcessor =
                new BeanListProcessor<>(EurexProductDefinitionParsedItem.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.getFormat().setDelimiter(';');

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new FileReader(file.getFile()));

        // The BeanListProcessor provides a list of objects extracted from the input.
        List<EurexProductDefinitionParsedItem> beans = rowProcessor.getBeans();
        log.info("Eurex Product Definition file " + file + ": " + beans.size() + " products.");

        // Convert Bean
        List<EurexProductDefinition> beans2 = new ArrayList<>();
        beans.forEach(
                (bean) -> {
                    // Type : Future or Option, parse first letter of Type
                    String type = null;
                    if (bean.getType() != null) {
                        if (bean.getType().charAt(0) == 'O') {
                            type = "Option";
                        } else if (bean.getType().charAt(0) == 'O') {
                            type = "Future";
                        }
                    }
                    // Tick Size convert to Double
                    Double tickSize = null;
                    if (bean.getTickSize() != null) {
                        if (!bean.getTickSize().equals("No")) {
                            //tickSize = Double.valueOf(bean.getTickSize().replace('.',','));
                            tickSize = Double.valueOf(bean.getTickSize());
                        }
                    }
                    // Trade Unit
                    Double tradUnit = null;
                    if (bean.getTradUnit() != null) {
                        if (!bean.getTradUnit().equals("No")) {
                            //tradUnit = Double.valueOf(bean.getTradUnit().replace('.',','));
                            tradUnit = Double.valueOf(bean.getTradUnit());
                        }
                    }
                    // Tick Value
                    Double tickValue = null;
                    if (bean.getTickValue() != null) {
                        if (!bean.getTickValue().equals("No")) {
                            //tradUnit = Double.valueOf(bean.getTickValue().replace('.',','));
                            tickValue = Double.valueOf(bean.getTickValue());
                        }
                    }
                    // Min Block Size
                    Integer minBlockSize = null;
                    if (bean.getMinBlockSize() != null) {
                        if (!bean.getMinBlockSize().equals("No")) {
                            //tradUnit = Double.valueOf(bean.getMinBlockSize().replace('.',','));
                            minBlockSize = Integer.valueOf(bean.getMinBlockSize());
                        }
                    }

                    beans2.add(
                            new EurexProductDefinition(
                                    bean.getEurexCode(),
                                    type,
                                    bean.getProductName(),
                                    bean.getBbgCode(),
                                    bean.getCurrency(),
                                    bean.getIsinCode(),
                                    tickSize,
                                    tradUnit,
                                    tickValue,
                                    minBlockSize,
                                    bean.getSettlementType()
                            )
                    );
                }
        );

        return beans2;
    }
}
