package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * Strategy concreto: Valida que la cuenta tenga saldo mínimo
 */
public class MinimumBalanceValidation implements ValidationStrategy {

    private static final double MINIMUM_BALANCE = 0.0;

    @Override
    public Mono<Boolean> validate(Account account) {
        return Mono.just(account.getBalance() >= MINIMUM_BALANCE);
    }

    @Override
    public String getErrorMessage() {
        return "Account balance cannot be less than " + MINIMUM_BALANCE;
    }
}

