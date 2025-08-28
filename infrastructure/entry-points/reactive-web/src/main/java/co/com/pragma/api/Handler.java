package co.com.pragma.api;

import co.com.pragma.api.dto.LogInDTO;
import co.com.pragma.api.dto.TokenDTO;
import co.com.pragma.api.security.JwtProvider;
import co.com.pragma.api.security.LoginService;
import co.com.pragma.api.security.Role;
import co.com.pragma.api.validator.input.IdentityDocumentInput;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.api.validator.ValidationHandler;
import co.com.pragma.inport.LoginUseCaseInPort;
import co.com.pragma.inport.ValidateUserExistenceUseCaseInPort;
import co.com.pragma.inport.RegisterUserUseCaseInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCaseInPort registerUserUseCaseInPort;
    private final LoginUseCaseInPort loginUserUseCaseInPort;

    private final LoginService loginService;
    private final ValidationHandler validationHandler;

    private final ValidateUserExistenceUseCaseInPort validateUserExistenceUseCaseInPort;
    private final UserDTOMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).ADMINISTRATOR.code) or hasAuthority(T(co.com.pragma.api.security.Role).ADVISOR.code)")
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .flatMap(validationHandler::validate)
                .map(userMapper::toModel)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return user;
                })
                .flatMap(registerUserUseCaseInPort::saveUser)
                .map(userMapper::toResponse).flatMap(dto->ServerResponse.status(HttpStatus.CREATED).bodyValue(dto));
    }


    public Mono<ServerResponse> listenLoginUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LogInDTO.class)
                .flatMap(validationHandler::validate)
                .flatMap(loginUserDTO -> loginUserUseCaseInPort.login(loginUserDTO.email(), loginUserDTO.password()))
                .flatMap(token -> ServerResponse.ok().bodyValue(token));
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


    public Mono<ServerResponse> logIn(ServerRequest request) {
        return request.bodyToMono(LogInDTO.class)
                .flatMap(validationHandler::validate)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(loginService.logIn(dto), TokenDTO.class));
    }
}
