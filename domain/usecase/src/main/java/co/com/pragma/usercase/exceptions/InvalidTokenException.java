package co.com.pragma.usercase.exceptions;

import lombok.Getter;

@Getter
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String error) {
        super(error);
    }
}
