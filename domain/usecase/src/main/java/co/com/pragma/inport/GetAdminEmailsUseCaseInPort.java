package co.com.pragma.inport;

import reactor.core.publisher.Mono;

import java.util.List;

public interface GetAdminEmailsUseCaseInPort {
    Mono<List<String>> execute();
}
