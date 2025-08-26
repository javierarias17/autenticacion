package co.com.pragma.usecase.validateuserexistence;

import co.com.pragma.inport.ValidateUserExistenceUseCaseInPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidateUserExistenceUseCase implements ValidateUserExistenceUseCaseInPort {

    private final UserRepository userRepository;

    @Override
    public Mono<User> findByIdentityDocument(String identityDocument) {
        return userRepository.findByIdentityDocument(identityDocument);
    }
}
