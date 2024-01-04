package com.eidiko.portal.exception.taskstatus;

public class ReportAlreadyExistsException extends RuntimeException {
    public ReportAlreadyExistsException() {
    }

    public ReportAlreadyExistsException(String message) {
        super(message);
    }
}
