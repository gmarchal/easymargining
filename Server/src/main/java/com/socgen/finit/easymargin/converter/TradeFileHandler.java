package com.socgen.finit.easymargin.converter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.socgen.finit.easymargin.model.TradeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.testng.collections.Maps;

import javax.annotation.PostConstruct;
import java.io.FileReader;
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

    Map<String, String> columnMapping;

    @PostConstruct
    public void init() {
        log.info("INIT TradeFileHandler");
        columnMapping = Maps.newHashMap();
        columnMapping.put("Product ID", "ProductId");
        columnMapping.put("Expiry Year", "expiryYear");
        columnMapping.put("Expiry Month", "expiryMonth");
        columnMapping.put("Expiry Day", "expiryDay");
        columnMapping.put("Version Number", "versionNumber");
        columnMapping.put("Product Settlement Type", "productSettlementType");
        columnMapping.put("Call Put Flag", "callPutFlag");
        columnMapping.put("Exercise Price", "exercisePrice");
        columnMapping.put("Exercise Style Flag", "exerciseStyleFlag");
        columnMapping.put("Instrument Type", "instrumentType");
        columnMapping.put("Assigned/Notified Balance", "assignedNotifiedBalance");
        columnMapping.put("Exercised/Allocated Balance", "exercisedAllocatedBalance");
        columnMapping.put("Long Balance", "longBalance");
        columnMapping.put("Short Balance", "shortBalance");

    }

    public List<TradeEntity> readTradeFile(URL file) throws IOException {
        HeaderColumnNameTranslateMappingStrategy<TradeEntity> strat = new HeaderColumnNameTranslateMappingStrategy<TradeEntity>();
        strat.setType(TradeEntity.class);
        strat.setColumnMapping(columnMapping);

        CsvToBean csv = new CsvToBean();
        CSVReader reader = new CSVReader(new FileReader(file.getFile()), ',', '"', 0);
        List<TradeEntity> parse = csv.parse(strat, reader);
        return parse.stream().filter(t -> !StringUtils.isEmpty(t.getProductId())).collect(Collectors.toList());
    }
}
