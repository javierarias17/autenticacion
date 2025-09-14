package co.com.pragma.api.exceptions;

import co.com.pragma.usercase.exceptions.BusinessException;
import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String error) {
        super(error, Map.of());
    }
}
