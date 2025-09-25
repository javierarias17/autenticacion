package co.com.pragma.usecase.getadminemails;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAdminEmailsUseCaseTest {

    @InjectMocks
    private GetAdminEmailsUseCase getAdminEmailsUseCase;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnAdminEmailsWhenAdminsExist() {
        User admin1 = User.builder()
                .id(1L)
                .firstName("Javier")
                .lastName("Arias")
                .email("javierarias17.dll@gmail.com")
                .roleId(1L)
                .build();

        when(userRepository.findByRoleId(1L))
                .thenReturn(Flux.just(admin1));

        Mono<List<String>> result = getAdminEmailsUseCase.execute();

        StepVerifier.create(result)
                .expectNextMatches(emails ->
                        emails.size() == 1 &&
                        emails.contains("javierarias17.dll@gmail.com"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyListWhenNoAdminsExist() {
        when(userRepository.findByRoleId(1L))
                .thenReturn(Flux.empty());

        Mono<List<String>> result = getAdminEmailsUseCase.execute();

        StepVerifier.create(result)
                .expectNextMatches(emails -> emails.isEmpty())
                .verifyComplete();
    }
}
