package com.easymargining.replication.ccg.market;

import com.easymargining.replication.ccg.market.parsers.CcgMarsClassFileLoader;
import com.easymargining.replication.ccg.market.parsers.CcgMarsRiskArrayLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Gilles Marchal on 08/12/2015.
 */
@Getter
@Slf4j
public class MarketDataEnvironment {

    private List<ClassFileItem> classFileItems;
    private List<RiskArrayItem> riskArrayItems;

    public void loadMarketData(MarketDataFileResolver fileResolver) {

        CcgMarsClassFileLoader classFileLoader = new CcgMarsClassFileLoader();
        CcgMarsRiskArrayLoader riskArrayLoader = new CcgMarsRiskArrayLoader();

        try {
            classFileItems = classFileLoader.readCCGClassFileFile(fileResolver.classFile());
            riskArrayItems = riskArrayLoader.readCCGRiskArrayFile(fileResolver.arrayFile());
        } catch (IOException e) {
            e.printStackTrace();
            //TODO : Add log and manage exception.
        } catch (JAXBException e) {
            e.printStackTrace();
            //TODO : Add log and manage exception.
        }

    }
}
