package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

public interface ValidationStrategy {

    Mono<Boolean> validate(Account account);

    String getErrorMessage();
}
