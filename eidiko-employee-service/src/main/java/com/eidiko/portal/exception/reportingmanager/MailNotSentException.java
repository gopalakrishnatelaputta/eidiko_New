package com.eidiko.portal.exception.reportingmanager;

public class MailNotSentException extends RuntimeException {
    public MailNotSentException(String mailNotSent) {
        super(mailNotSent);
    }
}
