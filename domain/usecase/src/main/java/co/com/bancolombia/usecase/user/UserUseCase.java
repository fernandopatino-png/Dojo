package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository repository;

    public Mono<User> registerUser(User user) {
        return repository.validateUserExists(user.getId())
                .filter(exists -> !exists)
                .flatMap(exists -> repository.registerUser(user))
                .switchIfEmpty(Mono.error(() -> new Exception("User already exists")));
    }

    public Mono<User> findUserById(Long id) {
        return repository.findUserById(id);
    }
}
