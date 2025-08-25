package co.com.pragma.usecase.validateuserexistence;

import co.com.pragma.inport.ValidateUserExistenceUseCaseInPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usercase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidateUserExistenceUseCase implements ValidateUserExistenceUseCaseInPort {

    private final UserRepository userRepository;

    @Override
    public Mono<User> findByIdentityDocument(String identityDocument) {
        return Mono.justOrEmpty(identityDocument)
                .filter(doc -> doc.matches("\\d+"))
                .switchIfEmpty(Mono.error(new ValidationException(
                        java.util.Map.of("identityDocument", "Identity document must be numeric")
                )))
                .flatMap(userRepository::findByIdentityDocument);
    }
}
