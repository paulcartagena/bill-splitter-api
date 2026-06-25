package com.billsplitter.exception;

public class ParticipantAlreadyExistsException extends RuntimeException {
    public ParticipantAlreadyExistsException(String message) {
        super(message);
    }
}
