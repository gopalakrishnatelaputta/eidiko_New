package com.eidiko.portal.exception.taskstatus;

public class NotEligibleException extends RuntimeException {
    public NotEligibleException() {
        super();
    }

    public NotEligibleException(String message) {
        super(message);
    }
}
