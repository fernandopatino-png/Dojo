package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * ESTRATEGIA CONCRETA: Validación de Propietario Existente
 *
 * ¿Qué hace?
 * Verifica que la cuenta tenga un propietario válido asignado.
 *
 * REGLA DE NEGOCIO:
 * Toda cuenta debe pertenecer a un usuario existente.
 * El ownerId debe ser un número positivo válido.
 */
public class OwnerExistsValidation implements ValidationStrategy {

    /**
     * Valida que la cuenta tenga un propietario válido
     *
     * @param account Cuenta a validar
     * @return Mono<Boolean> - true si tiene propietario válido, false si no
     */
    @Override
    public Mono<Boolean> validate(Account account) {
        // Verificar que ownerId exista y sea mayor a 0
        boolean hasOwner = account.getOwnerId() != null && account.getOwnerId() > 0;
        return Mono.just(hasOwner);
    }

    /**
     * Mensaje de error cuando la validación falla
     *
     * @return Mensaje descriptivo
     */
    @Override
    public String getErrorMessage() {
        return "La cuenta debe tener un propietario válido (ID > 0)";
    }
}
