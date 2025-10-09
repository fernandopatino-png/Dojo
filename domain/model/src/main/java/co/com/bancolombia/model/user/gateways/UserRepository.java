package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> registerUser(User user);

    Mono<User> findUserById(Long id);

    Mono<Boolean> validateUserExists(Long id);
}
