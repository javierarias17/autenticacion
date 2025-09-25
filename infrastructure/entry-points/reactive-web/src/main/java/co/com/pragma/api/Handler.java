package co.com.pragma.api;

import co.com.pragma.api.dto.*;
import co.com.pragma.api.security.JwtProvider;
import co.com.pragma.api.security.LoginService;
import co.com.pragma.api.mapper.UserDTOMapper;
import co.com.pragma.api.validator.ValidationHandler;
import co.com.pragma.inport.GetUsersByIdentityDocumentsUseCaseInPort;
import co.com.pragma.inport.RegisterUserUseCaseInPort;
import co.com.pragma.inport.GetAdminEmailsUseCaseInPort;
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
    private final GetUsersByIdentityDocumentsUseCaseInPort getUsersByIdentityDocumentsUseCaseInPort;
    private final GetAdminEmailsUseCaseInPort getAdminEmailsUseCaseInPort;
    private final LoginService loginService;

    private final ValidationHandler validationHandler;
    private final UserDTOMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).ADMINISTRATOR.code) or hasAuthority(T(co.com.pragma.api.security.Role).ADVISOR.code)")
    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDTO.class)
                .flatMap(validationHandler::validate)
                .map(userMapper::toModel)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return user;
                })
                .flatMap(registerUserUseCaseInPort::execute)
                .map(userMapper::toResponse).flatMap(dto->ServerResponse.status(HttpStatus.CREATED).bodyValue(dto));
    }

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).ADVISOR.code) or hasAuthority(T(co.com.pragma.api.security.Role).INTERNAL.code)")
    public Mono<ServerResponse> listenGetUsersByIdentityDocuments(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(GetUsersByIdentityDocumentsInDTO.class)
                .flatMap(validationHandler::validate)
                .flatMap(request ->
                        getUsersByIdentityDocumentsUseCaseInPort.execute(request.lstIdentityDocument())
                                .collectList()
                                .flatMap(lstUser -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new GetUsersByIdentityDocumentsOutDTO(userMapper.toResponseList(lstUser))))
                );
    }

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).ADMINISTRATOR.code) or hasAuthority(T(co.com.pragma.api.security.Role).INTERNAL.code)")
    public Mono<ServerResponse> listenGetAdminEmails(ServerRequest serverRequest) {
        return getAdminEmailsUseCaseInPort.execute()
                .flatMap(lstAdminEmails -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new GetAdminEmailsOutDTO(lstAdminEmails)));
    }

    public Mono<ServerResponse> listenLogIn(ServerRequest request) {
        return request.bodyToMono(LogInDTO.class)
                .flatMap(validationHandler::validate)
                .flatMap(dto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(loginService.logIn(dto), TokenDTO.class));
    }
}
