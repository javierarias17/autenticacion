package co.com.pragma.api;

import co.com.pragma.api.validator.input.IdentityDocumentInput;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.api.validator.ValidationHandler;
import co.com.pragma.inport.ValidateUserExistenceUseCaseInPort;
import co.com.pragma.inport.RegisterApplicantUserUseCaseInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterApplicantUserUseCaseInPort registerApplicantUserUseCaseInPort;
    private final ValidateUserExistenceUseCaseInPort validateUserExistenceUseCaseInPort;
    private final ValidationHandler validationHandler;
    private final UserDTOMapper userMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .flatMap(validationHandler::validate)
                .map(userMapper::toModel)
                .flatMap(registerApplicantUserUseCaseInPort::saveUser)
                .map(userMapper::toResponse).flatMap(dto->ServerResponse.status(HttpStatus.OK).bodyValue(dto));
    }

    public Mono<ServerResponse> listenValidateUserExistence(ServerRequest serverRequest) {
        String identityDocument = serverRequest.pathVariable("identityDocument");
        return validationHandler.validateValue(
                IdentityDocumentInput.class,
                "identityDocument",
                identityDocument
        )
        .then(validateUserExistenceUseCaseInPort.findByIdentityDocument(identityDocument))
        .flatMap(user -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userMapper.toResponse(user)))
        .switchIfEmpty(ServerResponse.notFound().build());
    }
}
