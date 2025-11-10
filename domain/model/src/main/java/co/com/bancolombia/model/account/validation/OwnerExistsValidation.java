package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * Strategy concreto: Valida que la cuenta tenga un propietario válido
 */
public class OwnerExistsValidation implements ValidationStrategy {

    @Override
    public Mono<Boolean> validate(Account account) {
        return Mono.just(account.getOwnerId() != null && account.getOwnerId() > 0);
    }

    @Override
    public String getErrorMessage() {
        return "Account must have a valid owner ID";
    }
}

