package co.com.pragma.api.dto;

import co.com.pragma.api.common.ValidationPatterns;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UserDTO (Long id,
       @NotBlank(message = "First name is required and cannot be empty")
       String firstName,
       @NotBlank(message = "Last name is required and cannot be empty")
       String lastName,
       @NotBlank(message = "Email is required and cannot be empty")
       @Pattern(regexp = ValidationPatterns.EMAIL_PATTERN, message = "Email format is not valid")
       String email,
       LocalDate birthDate,
       String address,
       @NotBlank(message = "Identity document is required and cannot be empty")
       String identityDocument,
       String phone,
       Long roleId,
       @NotNull(message = "Base salary is required")
       @DecimalMin(value = "0.00", message = "Base salary must be greater than or equal to 0")
       @DecimalMax(value = "15000000.00", message = "Base salary must be less than or equal to 15000000")
       Double baseSalary) {
}
