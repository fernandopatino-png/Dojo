package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * ESTRATEGIA CONCRETA: Validación de Cuenta Activa
 *
 * ¿Qué hace?
 * Verifica que la cuenta esté activa y tenga los datos básicos completos.
 *
 * REGLA DE NEGOCIO:
 * Una cuenta activa debe tener:
 * - ID no nulo
 * - Saldo no nulo
 *
 * (En una versión real, tendríamos un campo 'active' en Account)
 */
public class ActiveAccountValidation implements ValidationStrategy {

    /**
     * Valida que la cuenta tenga datos básicos completos
     *
     * @param account Cuenta a validar
     * @return Mono<Boolean> - true si está activa, false si no
     */
    @Override
    public Mono<Boolean> validate(Account account) {
        // Verificar que tenga ID y saldo
        boolean hasId = account.getId() != null;
        boolean hasBalance = account.getBalance() != null;

        boolean isActive = hasId && hasBalance;
        return Mono.just(isActive);
    }

    /**
     * Mensaje de error cuando la validación falla
     *
     * @return Mensaje descriptivo
     */
    @Override
    public String getErrorMessage() {
        return "La cuenta debe estar activa y tener datos completos";
    }
}
