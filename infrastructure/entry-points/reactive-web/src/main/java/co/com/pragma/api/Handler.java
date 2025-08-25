package co.com.pragma.api;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.inport.ValidateUserExistenceUseCaseInPort;
import co.com.pragma.usercase.exceptions.TechnicalException;
import co.com.pragma.usercase.exceptions.ValidationException;
import co.com.pragma.inport.RegisterApplicantUserUseCaseInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterApplicantUserUseCaseInPort registerApplicantUserUseCaseInPort;
    private final ValidateUserExistenceUseCaseInPort validateUserExistenceUseCaseInPort;

    private final UserDTOMapper userMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .flatMap(userDTO -> registerApplicantUserUseCaseInPort.saveUser(userMapper.toModel(userDTO)))
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userMapper.toResponse(savedUser)))
                // Manejo de request malformado -> 400
                .onErrorResume(ServerWebInputException.class, e -> {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("Invalid request body:", e.getMostSpecificCause().getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errors);
                })
                // Manejo de validaciones -> 400
                .onErrorResume(ValidationException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(e.getErrors())
                )
                // Manejo de excepciones inesperadas -> 500
                .onErrorResume(TechnicalException.class, e ->
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(e.getMessage())
                );
    }

    public Mono<ServerResponse> listenValidateUserExistence(ServerRequest serverRequest) {
        String identityDocument = serverRequest.pathVariable("identityDocument");

        return validateUserExistenceUseCaseInPort.findByIdentityDocument(identityDocument)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userMapper.toResponse(user)))
                .switchIfEmpty(ServerResponse.notFound().build()) // 404 si no existe
                .onErrorResume(ServerWebInputException.class, e -> {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("Invalid request param:", e.getMostSpecificCause().getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errors);
                })
                .onErrorResume(ValidationException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(e.getErrors())
                )
                .onErrorResume(TechnicalException.class, e ->
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(e.getMessage())
                );
    }


}
