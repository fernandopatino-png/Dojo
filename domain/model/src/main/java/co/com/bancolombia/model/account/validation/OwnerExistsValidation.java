package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

public class OwnerExistsValidation implements ValidationStrategy {

    @Override
    public Mono<Boolean> validate(Account account) {
        //Verificar que ownerId exista y sea mayor a 0
        boolean hasOwner = account.getOwnerId() != null && account.getOwnerId() > 0;
        return Mono.just(hasOwner);
    }

    @Override
    public String getErrorMessage() {
        return "La cuenta debe tener un propietario válido (ID > 0)";
    }
}
