package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

public class ActiveAccountValidation implements ValidationStrategy {

    @Override
    public Mono<Boolean> validate(Account account) {
        //Verificar que tenga ID y saldo
        boolean hasId = account.getId() != null;
        boolean hasBalance = account.getBalance() != null;

        boolean isActive = hasId && hasBalance;
        return Mono.just(isActive);
    }

    @Override
    public String getErrorMessage() {
        return "La cuenta debe estar activa y tener datos completos";
    }
}
