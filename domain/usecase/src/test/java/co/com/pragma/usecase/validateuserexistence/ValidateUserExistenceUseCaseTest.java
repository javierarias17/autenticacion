package co.com.pragma.usecase.validateuserexistence;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidateUserExistenceUseCaseTest {

    private static final String EXISTING_IDENTITY_DOCUMENT = "1061769743";
    private static final String NON_EXISTING_IDENTITY_DOCUMENT = "0000000000";

    @InjectMocks
    private ValidateUserExistenceUseCase validateUserExistenceUseCase;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenIdentityDocumentExists() {
        User user = User.builder()
                .id(1L)
                .identityDocument(EXISTING_IDENTITY_DOCUMENT)
                .email("javierarias17@test.com")
                .build();

        when(userRepository.findByIdentityDocument(EXISTING_IDENTITY_DOCUMENT))
                .thenReturn(Mono.just(user));

        Mono<User> result = validateUserExistenceUseCase.findByIdentityDocument(EXISTING_IDENTITY_DOCUMENT);

        StepVerifier.create(result)
                .expectNextMatches(foundUser ->
                        foundUser.getId().equals(1L) &&
                                foundUser.getIdentityDocument().equals(EXISTING_IDENTITY_DOCUMENT))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenIdentityDocumentDoesNotExist() {
        when(userRepository.findByIdentityDocument(NON_EXISTING_IDENTITY_DOCUMENT))
                .thenReturn(Mono.empty());

        Mono<User> result = validateUserExistenceUseCase.findByIdentityDocument(NON_EXISTING_IDENTITY_DOCUMENT);

        StepVerifier.create(result)
                .verifyComplete();
    }
}


