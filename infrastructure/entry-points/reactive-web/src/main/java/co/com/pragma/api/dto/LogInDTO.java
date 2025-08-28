package co.com.pragma.api.dto;

import co.com.pragma.api.common.ValidationPatterns;
import jakarta.validation.constraints.*;

public record LogInDTO(
       @NotBlank(message = "Email is required and cannot be empty")
       @Pattern(regexp = ValidationPatterns.EMAIL_PATTERN, message = "Email format is not valid")
       String email,
       @NotBlank(message = "Password is required and cannot be empty")
       String password
) {
}
