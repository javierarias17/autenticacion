package co.com.pragma.api;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.registerapplicantuser.RegisterApplicantUserUseCase;
import co.com.pragma.usecase.registerapplicantuser.inport.RegisterApplicantUserUseCaseInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterApplicantUserUseCaseInPort registerApplicantUserUseCaseInPort;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest){
        return serverRequest.bodyToMono(User.class)
                .flatMap(registerApplicantUserUseCaseInPort::saveUser)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(savedUser));
    }
}
