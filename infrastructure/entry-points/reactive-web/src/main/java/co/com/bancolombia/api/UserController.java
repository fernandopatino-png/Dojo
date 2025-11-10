package co.com.bancolombia.api;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller para operaciones de usuarios
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * Registrar nuevo usuario con Builder Pattern
     * POST /api/users
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> registerUser(@RequestBody UserRequest request) {
        log.info("Registering user: {}", request.getName());

        // Usar Builder Pattern con validaciones
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .type(request.getType())
            .number(request.getNumber())
            .active(true)
            .build();

        return userRepository.registerUser(user);
    }

    /**
     * Obtener usuario por ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        return userRepository.findUserById(id);
    }

    /**
     * Validar si usuario existe
     * GET /api/users/{id}/exists
     */
    @GetMapping("/{id}/exists")
    public Mono<Boolean> userExists(@PathVariable Long id) {
        log.info("Validating if user exists: {}", id);
        return userRepository.validateUserExists(id);
    }

    @lombok.Data
    public static class UserRequest {
        private String name;
        private String email;
        private String type;
        private String number;
    }
}

