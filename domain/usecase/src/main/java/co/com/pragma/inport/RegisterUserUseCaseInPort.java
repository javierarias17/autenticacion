package co.com.pragma.inport;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCaseInPort {
    Mono<User> saveUser(User user);
}
