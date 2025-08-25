package co.com.pragma.usecase.registerapplicantuser;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.registerapplicantuser.inport.RegisterApplicantUserUseCaseInPort;
import co.com.pragma.usercase.exceptions.TechnicalException;
import co.com.pragma.usercase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegisterApplicantUserUseCase implements RegisterApplicantUserUseCaseInPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Logger LOGGER=Logger.getLogger("InfoLogging");

    private static final Pattern EMAIL_PATTERN =Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final String EMAIL = "email";

    @Override
    public Mono<User> saveUser(User user) {
        return validateAll(user)
                .then(Mono.just(user)
                        .map(usr -> {usr.setId(null); return usr;}))
                .flatMap(userRepository::save)
                .onErrorResume(Exception.class, ex -> {
                    //Errores esperados
                    if (ex instanceof ValidationException) {
                        return Mono.error(ex);
                    }
                    // Errores inesperados
                    LOGGER.severe("Technical error: " + ex);
                    return Mono.error(new TechnicalException("Technical error: Unexpected error saving user. Please contact the administrator."));
                });
    }

    private Mono<Void> validateAll(User user) {
        Map<String, String> errors = new HashMap<>();
        // Validaciones en memoria
        validateRequiredFields(user, errors);
        validateRanges(user, errors);
        if (!errors.isEmpty()) {
            return Mono.error(new ValidationException(errors));
        }
        // Validaciones contra BD
        return validateWithDatabase(user, errors);
    }

    private void validateRequiredFields(User user, Map<String, String> errors) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            errors.put("firstName", "First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            errors.put("lastName", "Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            errors.put(EMAIL, "Email is required");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.put(EMAIL, "Invalid email format");
        }
        if (user.getBaseSalary() == null) {
            errors.put("baseSalary", "Base salary is required");
        }
        if (user.getIdentityDocument() == null || user.getIdentityDocument().isBlank()) {
            errors.put("identityDocument", "Identity document is required");
        }
    }

    private void validateRanges(User user, Map<String, String> errors) {
        if (user.getBaseSalary() != null &&
                (user.getBaseSalary().compareTo(BigDecimal.ZERO) < 0 ||
                        user.getBaseSalary().compareTo(new BigDecimal("15000000")) > 0)) {
            errors.put("baseSalary", "Base salary must be between 0 and 15,000,000");
        }
    }

    private Mono<Void> validateWithDatabase(User user, Map<String, String> errors) {
        return Mono.zip(
                userRepository.existsByEmail(user.getEmail()),
                userRepository.existsByIdentityDocument(user.getIdentityDocument()),
                roleRepository.existsById(user.getRoleId())
        ).flatMap(results -> {
            boolean emailExists = results.getT1();
            boolean docExists = results.getT2();
            boolean roleIdExists = results.getT3();

            if (emailExists) errors.put(EMAIL, "Email already registered");
            if (docExists) errors.put("identityDocument", "Identity document already registered");
            if (!roleIdExists) errors.put("roleId", "Role Id does not exist");

            return errors.isEmpty() ? Mono.empty() : Mono.error(new ValidationException(errors));
        });
    }

}