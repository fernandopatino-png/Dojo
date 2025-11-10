package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * Strategy concreto: Valida que la cuenta esté activa
 */
public class ActiveAccountValidation implements ValidationStrategy {

    @Override
    public Mono<Boolean> validate(Account account) {
        // Por ahora asumimos que todas las cuentas están activas
        // En el futuro se puede agregar un campo 'active' en Account
        return Mono.just(account.getId() != null && account.getBalance() != null);
    }

    @Override
    public String getErrorMessage() {
        return "Account must be active";
    }
}

