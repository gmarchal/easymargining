package com.socgen.finit.easymargin.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Session factory to handle web session and files
 * Created by mlerebou on 24/11/2015.
 */
@Service
@Slf4j
public class SessionFactory {

    @Value("${path.trade.folder}")
    private String folderPath;

    @Value("${trade.etd.file.name}")
    private String etdFileName;

    @PostConstruct
    public void init() {
        File theDir = new File(folderPath);
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            log.info("Creating trade directory: " + theDir);
        }
    }

    public SessionInfo createSession() {
        return getOrCreateSession(UUID.randomUUID().toString());
    }

    public SessionInfo getOrCreateSession(String sessionId) {
        File theDir = new File(folderPath, sessionId);

        // if the directory does not exist, create it
        SessionInfo sessionInfo = new SessionInfo(sessionId);
        if (!theDir.exists()) {
            log.info("creating new session directory: " + theDir);

            try{
                theDir.mkdir();
            }
            catch(SecurityException se){
                log.error("create session error", se);
                return null;
            }
        }

        File tradeFile = new File(theDir.getPath(), etdFileName);
        if (!tradeFile.exists()) {
            try {
                tradeFile.createNewFile();
            } catch (IOException e) {
                log.error("create session trade file failed " + etdFileName, e);
            }
        }

        try {
            sessionInfo.setEtdTradeUrl(tradeFile.toURI().toURL());
        } catch (MalformedURLException e) {
            log.error("Trade file error" + tradeFile, e);
        }

        return sessionInfo;
    }
}
