package com.easymargining.replication.eurex.domain.model.exception;

/**
 * Created by Gilles Marchal on 11/02/2016.
 */
public class UserConflictException extends RuntimeException {

    public UserConflictException( String emailUser, String message ) {
        super(String.format("User conflict for {%s}: %s", emailUser,message));
    }

    public UserConflictException(String emailUser ) {
        this(emailUser, "already exist, thanks to try with another one");
    }
}
