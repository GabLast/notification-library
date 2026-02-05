package org.myorg.application.exceptions;

public class InvalidDataFormat extends RuntimeException {
    public InvalidDataFormat(String message) {
        super(message);
    }
}
