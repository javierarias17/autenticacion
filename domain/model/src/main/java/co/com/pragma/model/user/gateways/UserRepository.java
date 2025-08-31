package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {

    Mono<User> saveUser(User user);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
    Mono<User> findByEmail(String email);
    Flux<User> findByIdentityDocumentIn(List<String> identityDocuments);
}
