package co.com.pragma.inport;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GetUsersByIdentityDocumentsUseCaseInPort {
    Flux<User> execute(List<String> lstIdentityDocument);
}
