package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.inport.RegisterUserUseCaseInPort;
import co.com.pragma.usercase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserUseCaseInPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Mono<User> execute(User user) {
        Mono<Boolean> existsEmail = userRepository.existsByEmail(user.getEmail());
        Mono<Boolean> existsIdentityDocument = userRepository.existsByIdentityDocument(user.getIdentityDocument());
        Mono<Boolean> existsRole = roleRepository.existsById(user.getRoleId());

        return Mono.zip(existsEmail, existsIdentityDocument,existsRole)
                .flatMap((Tuple3<Boolean, Boolean, Boolean> tuple) -> {
                    Map<String, String> errors = new HashMap<>();
                    if (tuple.getT1()) errors.put("email","Email already registered");
                    if (tuple.getT2()) errors.put("identityDocument","Identity document already registered");
                    if (!tuple.getT3()) errors.put("roleId","Role Id does not exist");

                    if (!errors.isEmpty()) {
                        return Mono.error(new ValidationException(errors));
                    }
                    user.setId(null);
                    return userRepository.saveUser(user);
                });
    }
}