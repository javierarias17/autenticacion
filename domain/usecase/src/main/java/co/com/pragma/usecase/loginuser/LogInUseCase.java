package co.com.pragma.usecase.loginuser;

import co.com.pragma.inport.LoginUseCaseInPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usercase.exceptions.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LogInUseCase implements LoginUseCaseInPort {

    private final UserRepository userRepository;

    @Override
    public Mono<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> {
                    if (!user.getPassword().equals(password)) {
                        return Mono.error(new InvalidCredentialsException());
                    }
                    //TODO: Se crea el token para enviar
                    return Mono.just(user);
                });
    }
}
