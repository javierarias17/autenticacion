package co.com.pragma.r2dbc;

import co.com.pragma.model.role.Role;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleReactiveRepository extends ReactiveCrudRepository<RoleEntity, Long>, ReactiveQueryByExampleExecutor<RoleEntity> {
}
