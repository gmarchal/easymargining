package com.easymargining.replication.ccg.market.parsers;

import com.easymargining.replication.ccg.market.RiskArrayDatas;
import com.easymargining.replication.ccg.market.RiskArrayItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
@Service
@Slf4j
public class CcgMarsRiskArrayLoader {

    public List<RiskArrayItem> readCCGRiskArrayFile(URL file) throws IOException, JAXBException {
        log.info("Read CCG RiskArray file " + file);

        JAXBContext jaxbContext = JAXBContext.newInstance(RiskArrayDatas.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        RiskArrayDatas datas = (RiskArrayDatas) jaxbUnmarshaller.unmarshal( file );
        List<RiskArrayItem> beans = datas.getRiskArrayDatas();

        log.info("CCG RiskArray file " + file + ": " + beans.size() + " datas.");
        return beans;
    }
}
