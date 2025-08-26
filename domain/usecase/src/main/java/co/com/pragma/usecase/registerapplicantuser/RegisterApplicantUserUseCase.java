package co.com.pragma.usecase.registerapplicantuser;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.inport.RegisterApplicantUserUseCaseInPort;
import co.com.pragma.usercase.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class RegisterApplicantUserUseCase implements RegisterApplicantUserUseCaseInPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Mono<User> saveUser(User user) {
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
                        return Mono.error(new BusinessException(errors));
                    }
                    return userRepository.saveUser(user);
                });
    }
}