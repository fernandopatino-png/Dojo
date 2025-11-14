package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

public class MinimumBalanceValidation implements ValidationStrategy {

    private static final double MINIMUM_BALANCE = 0.0;

    @Override
    public Mono<Boolean> validate(Account account) {
        //Verificar que el saldo sea mayor o igual al mínimo
        boolean isValid = account.getBalance() >= MINIMUM_BALANCE;
        return Mono.just(isValid);
    }

    @Override
    public String getErrorMessage() {
        return "El saldo de la cuenta no puede ser menor a $" + MINIMUM_BALANCE;
    }
}
