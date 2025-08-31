package co.com.pragma.usecase.getusersbyidentitydocuments;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetUsersByIdentityDocumentsUseCaseTest {

    private static final String EXISTING_IDENTITY_DOCUMENT = "1061769743";
    private static final String NON_EXISTING_IDENTITY_DOCUMENT = "0000000000";

    @InjectMocks
    private GetUsersByIdentityDocumentsUseCase getUsersByIdentityDocumentsUseCase;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenIdentityDocumentExists() {
        User user = User.builder()
                .id(1L)
                .identityDocument(EXISTING_IDENTITY_DOCUMENT)
                .email("javierarias17.dll@gmail.com")
                .build();

        when(userRepository.findByIdentityDocumentIn(List.of(EXISTING_IDENTITY_DOCUMENT)))
                .thenReturn(Flux.just(user));

        Flux<User> result = getUsersByIdentityDocumentsUseCase.execute(List.of(EXISTING_IDENTITY_DOCUMENT));

        StepVerifier.create(result)
                .expectNextMatches(foundUser ->
                        foundUser.getId().equals(1L) &&
                                foundUser.getIdentityDocument().equals(EXISTING_IDENTITY_DOCUMENT))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenIdentityDocumentDoesNotExist() {
        when(userRepository.findByIdentityDocumentIn(List.of(NON_EXISTING_IDENTITY_DOCUMENT)))
                .thenReturn(Flux.empty());

        Flux<User> result = getUsersByIdentityDocumentsUseCase.execute(List.of(NON_EXISTING_IDENTITY_DOCUMENT));

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldReturnMultipleUsersWhenMultipleIdentityDocumentsExist() {
        User user1 = User.builder()
                .id(1L)
                .identityDocument("1234567890")
                .email("luisa_pelaez123@gmail.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .identityDocument("9876543210")
                .email("m-plaza-rino@htomail.com")
                .build();

        List<String> documents = List.of("1234567890", "9876543210");

        when(userRepository.findByIdentityDocumentIn(documents))
                .thenReturn(Flux.just(user1, user2));

        Flux<User> result = getUsersByIdentityDocumentsUseCase.execute(documents);

        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }
}


