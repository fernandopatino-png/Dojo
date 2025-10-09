package co.com.bancolombia.api;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.account.AccountUseCase;
import co.com.bancolombia.usecase.list.ListUseCase;
import co.com.bancolombia.usecase.test.TestUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final AccountUseCase accountUseCase;
    private final ListUseCase listUseCase;
    private final TestUseCase testUseCase;

    public Mono<ServerResponse> registerUserUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(userUseCase::registerUser)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(throwable -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(throwable.getMessage()));
    }

    public Mono<ServerResponse> findUserbyId(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.queryParam("id").orElse("12345"));
        return userUseCase.findUserById(id)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listAccounts(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(listUseCase::listarCuentas)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));

    }
}
