package co.com.pragma.usecase.registerapplicantuser;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.registerapplicantuser.inport.RegisterApplicantUserUseCaseInPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterApplicantUserUseCase implements RegisterApplicantUserUseCaseInPort {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user){
        return userRepository.save(user);
    }
}
