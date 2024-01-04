package com.eidiko.portal.exception.taskstatus;

public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException() {
        super();
    }
}
