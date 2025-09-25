package co.com.pragma.usecase.getadminemails;

import co.com.pragma.inport.GetAdminEmailsUseCaseInPort;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetAdminEmailsUseCase implements GetAdminEmailsUseCaseInPort {

    private final UserRepository userRepository;
    private static final Long ADMIN_ROLE_ID = 1L;

    @Override
    public Mono<List<String>> execute() {
        return userRepository.findByRoleId(ADMIN_ROLE_ID)
                .map(user -> user.getEmail())
                .collectList();
    }
}
