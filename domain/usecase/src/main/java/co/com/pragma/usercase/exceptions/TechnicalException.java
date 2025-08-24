package co.com.pragma.usercase.exceptions;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {
    public TechnicalException(String error) {
        super(error);
    }
}
