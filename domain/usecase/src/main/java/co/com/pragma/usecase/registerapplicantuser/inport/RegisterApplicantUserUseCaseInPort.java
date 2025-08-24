package co.com.pragma.usecase.registerapplicantuser.inport;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface RegisterApplicantUserUseCaseInPort {
    Mono<User> saveUser(User user);
}
