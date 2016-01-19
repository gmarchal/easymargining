package com.easymargining.replication.eurex.controller;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

/**
 * Session info
 * Created by mlerebou on 24/11/2015.
 */
@Getter
@Setter
public class SessionInfo {
    private String sessionId;

    private URL etdTradeUrl;

    public SessionInfo(String sessionId) {
        this.sessionId = sessionId;
    }
}
