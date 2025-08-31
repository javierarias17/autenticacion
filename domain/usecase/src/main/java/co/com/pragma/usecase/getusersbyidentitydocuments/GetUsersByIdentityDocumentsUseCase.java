package co.com.pragma.usecase.getusersbyidentitydocuments;

import co.com.pragma.inport.GetUsersByIdentityDocumentsUseCaseInPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class GetUsersByIdentityDocumentsUseCase implements GetUsersByIdentityDocumentsUseCaseInPort {

    private final UserRepository userRepository;

    @Override
    public Flux<User> execute(List<String> lstIdentityDocument) {
        return userRepository.findByIdentityDocumentIn(lstIdentityDocument);
    }
}
