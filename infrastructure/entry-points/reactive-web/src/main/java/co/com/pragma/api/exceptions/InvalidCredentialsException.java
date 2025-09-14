package co.com.pragma.api.exceptions;

import co.com.pragma.usercase.exceptions.BusinessException;
import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(String error) {
        super(error, Map.of());
    }
}
