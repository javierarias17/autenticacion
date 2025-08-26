package co.com.pragma.usecase.registerapplicantuser;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usercase.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RegisterApplicantUserUseCaseTest {

    @InjectMocks
    private RegisterApplicantUserUseCase registerApplicantUserUseCase;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Test
    void shouldCreateUserWhenDataIsValid() {
        User user = User.builder()
                .id(null)
                .email("correo@correo.com")
                .identityDocument("112233")
                .roleId(1L)
                .build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(false));
        when(roleRepository.existsById(user.getRoleId())).thenReturn(Mono.just(true));
        when(userRepository.saveUser(any(User.class)))
                .thenReturn(Mono.just(user.toBuilder().id(1L).build()));

        Mono<User> result = registerApplicantUserUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectNextMatches(respuesta -> respuesta.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        User user = User.builder()
                .email("correo@correo.com")
                .identityDocument("112233")
                .roleId(1L)
                .build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));
        when(userRepository.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(false));
        when(roleRepository.existsById(user.getRoleId())).thenReturn(Mono.just(true));

        Mono<User> result = registerApplicantUserUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getErrors().containsKey("email")
                ).verify();
    }

    @Test
    void shouldFailWhenIdentityDocumentAlreadyExists() {
        User user = User.builder()
                .email("correo@correo.com")
                .identityDocument("112233")
                .roleId(1L)
                .build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(true));
        when(roleRepository.existsById(user.getRoleId())).thenReturn(Mono.just(true));

        Mono<User> result = registerApplicantUserUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getErrors().containsKey("identityDocument")
                ).verify();
    }

    @Test
    void shouldFailWhenRoleDoesNotExist() {
        User user = User.builder()
                .email("correo@correo.com")
                .identityDocument("112233")
                .roleId(99L)
                .build();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.existsByIdentityDocument(user.getIdentityDocument())).thenReturn(Mono.just(false));
        when(roleRepository.existsById(user.getRoleId())).thenReturn(Mono.just(false));

        Mono<User> result = registerApplicantUserUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getErrors().containsKey("roleId")
                ).verify();
    }

}
