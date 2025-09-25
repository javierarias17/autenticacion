package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, Long>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
    Mono<User> findByEmail(String email);
    Flux<User> findByIdentityDocumentIn(List<String> identityDocuments);
    Flux<User> findByRoleId(Long roleId);
}
