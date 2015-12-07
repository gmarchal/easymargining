package com.easymargining.replication.ccg.market.parsers;

import com.easymargining.replication.ccg.market.ClassFileDatas;
import com.easymargining.replication.ccg.market.ClassFileItem;
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
public class CcgMarsClassFileLoader {

    public List<ClassFileItem> readCCGClassFileFile(URL file) throws IOException, JAXBException {
        log.info("Read CCG ClassFile file " + file);

        JAXBContext jaxbContext = JAXBContext.newInstance(ClassFileDatas.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ClassFileDatas datas = (ClassFileDatas) jaxbUnmarshaller.unmarshal( file );
        List<ClassFileItem> beans = datas.getClassFileDatas();

        log.info("CCG ClassFile file " + file + ": " + beans.size() + " datas.");
        return beans;
    }
}
