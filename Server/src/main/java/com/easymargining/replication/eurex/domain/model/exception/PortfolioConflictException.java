package com.easymargining.replication.eurex.domain.model.exception;

/**
 * Created by Gilles Marchal on 11/02/2016.
 */
public class PortfolioConflictException extends RuntimeException {

    public PortfolioConflictException(String OwnerId, String portfolioName, String message ) {
        super(String.format("Portfolio conflict for {%s}: %s", portfolioName,message));
    }

    public PortfolioConflictException(String ownerId, String portfolioName ) {
        this(ownerId, portfolioName, "already exist, thanks to try with another one");
    }
}
