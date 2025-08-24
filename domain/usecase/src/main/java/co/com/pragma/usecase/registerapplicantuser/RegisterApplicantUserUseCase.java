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
import java.util.logging.Logger;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegisterApplicantUserUseCase implements RegisterApplicantUserUseCaseInPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static Logger LOGGER=Logger.getLogger("InfoLogging");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public Mono<User> saveUser(User user) {
        return validateAll(user)
                .then(userRepository.save(user))
                .onErrorResume(Exception.class, ex -> {
                    //Errores esperados
                    if (ex instanceof ValidationException) {
                        return Mono.error(ex);
                    }
                    // Errores inesperados
                    LOGGER.severe("Technical error: " + ex);
                    return Mono.error(new TechnicalException("Technical error: Unexpected error saving user"));
                });
    }

    private Mono<Void> validateAll(User user) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            errors.put("firstName", "First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            errors.put("lastName", "Last name is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            errors.put("email", "Email is required");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.put("email", "Invalid email format");
        }
        if (user.getBaseSalary() == null) {
            errors.put("baseSalary", "Base salary is required");
        } else if (user.getBaseSalary().compareTo(BigDecimal.ZERO) < 0
                || user.getBaseSalary().compareTo(new BigDecimal("15000000")) > 0) {
            errors.put("baseSalary", "Base salary must be between 0 and 15,000,000");
        }

        if (!errors.isEmpty()) {
            return Mono.error(new ValidationException(errors));
        }

        // Validaciones contra la BD
        return Mono.zip(
                userRepository.findByEmail(user.getEmail()).hasElement(),
                userRepository.findByIdentityDocument(user.getIdentityDocument()).hasElement()
                //roleRepository.findById(user.getRoleId()).hasElement()
        ).flatMap(results -> {
            boolean emailExists = results.getT1();
            boolean docExists = results.getT2();
            //boolean roleIdExists = results.getT3();

            if (emailExists) errors.put("email", "Email already registered");
            if (docExists) errors.put("identityDocument", "Identity document already registered");
            //if (!roleIdExists) errors.put("roleId", "Role Id does not exist");

            return errors.isEmpty() ? Mono.empty() : Mono.error(new ValidationException(errors));
        });
    }

}