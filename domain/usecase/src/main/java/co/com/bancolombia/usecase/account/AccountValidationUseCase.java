package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.validation.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AccountValidationUseCase {

    private final List<ValidationStrategy> strategies;

    public Mono<Boolean> validateAccount(Account account) {
        //Aplicar todas las estrategias
        return Flux.fromIterable(strategies)
                .flatMap(strategy -> strategy.validate(account))
                .all(result -> result); //todas deben ser true
    }

    public Mono<List<String>> validateWithErrors(Account account) {
        List<String> errors = new ArrayList<>();

        //Para cada estrategia
        return Flux.fromIterable(strategies)
                .flatMap(strategy ->
                        //Ejecutar validación
                        strategy.validate(account)
                                .map(isValid -> {
                                    //Si falla, agregar mensaje de error
                                    if (!isValid) {
                                        errors.add(strategy.getErrorMessage());
                                    }
                                    return isValid;
                                })
                )
                .then(Mono.just(errors)); //Retornar lista de errores
    }

    public void addValidationStrategy(ValidationStrategy strategy) {
        this.strategies.add(strategy);
    }

    public void removeValidationStrategy(ValidationStrategy strategy) {
        this.strategies.remove(strategy);
    }
}

