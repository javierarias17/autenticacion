package co.com.pragma.api.security;

import co.com.pragma.api.dto.LogInDTO;
import co.com.pragma.api.dto.TokenDTO;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usercase.exceptions.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);


    public Mono<TokenDTO> logIn(LogInDTO dto) {
        return userRepository.findByEmail(dto.email()).filter(user -> passwordEncoder.matches(dto.password(), user.getPassword()))
                .map(user -> new TokenDTO(jwtProvider.generateToken(user)))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid credentials")));
    }
}