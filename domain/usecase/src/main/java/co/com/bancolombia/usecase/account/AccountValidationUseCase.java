package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.validation.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case que aplica Strategy Pattern para validaciones de cuentas
 * Permite agregar/remover estrategias de validación dinámicamente
 */
@RequiredArgsConstructor
public class AccountValidationUseCase {

    private final List<ValidationStrategy> strategies;

    /**
     * Valida una cuenta usando todas las estrategias configuradas
     * Retorna true solo si todas las validaciones pasan
     */
    public Mono<Boolean> validateAccount(Account account) {
        return Flux.fromIterable(strategies)
            .flatMap(strategy -> strategy.validate(account))
            .all(result -> result); // AND lógico de todos los resultados
    }

    /**
     * Valida y retorna los mensajes de error de las validaciones que fallen
     */
    public Mono<List<String>> validateWithErrors(Account account) {
        List<String> errors = new ArrayList<>();

        return Flux.fromIterable(strategies)
            .flatMap(strategy ->
                strategy.validate(account)
                    .flatMap(isValid -> {
                        if (!isValid) {
                            errors.add(strategy.getErrorMessage());
                        }
                        return Mono.just(isValid);
                    })
            )
            .then(Mono.just(errors));
    }

    /**
     * Permite agregar una nueva estrategia de validación dinámicamente
     */
    public void addValidationStrategy(ValidationStrategy strategy) {
        this.strategies.add(strategy);
    }

    /**
     * Permite remover una estrategia de validación
     */
    public void removeValidationStrategy(ValidationStrategy strategy) {
        this.strategies.remove(strategy);
    }
}

