package co.com.pragma.usercase.exceptions;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }

    public InvalidCredentialsException(String error) {
        super(error);
    }
}
