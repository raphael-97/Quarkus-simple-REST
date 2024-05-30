package org.acme.exceptions;

import lombok.Getter;

@Getter
public class WrongInputException extends Exception {
    int status;

    public WrongInputException(String message, int status) {
        super(message);
        this.status = status;
    }   
}
