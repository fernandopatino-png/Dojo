package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * ESTRATEGIA CONCRETA: Validación de Saldo Mínimo
 *
 * Esta es una IMPLEMENTACIÓN del patrón Strategy.
 *
 * ¿Qué hace?
 * Verifica que la cuenta tenga al menos el saldo mínimo permitido.
 *
 * REGLA DE NEGOCIO:
 * Una cuenta no puede tener saldo negativo (debe ser >= 0)
 *
 * Ejemplo:
 * - Cuenta con $500 → VÁLIDA ✓
 * - Cuenta con $0 → VÁLIDA ✓
 * - Cuenta con -$100 → INVÁLIDA ✗
 */
public class MinimumBalanceValidation implements ValidationStrategy {

    /** Saldo mínimo permitido en una cuenta */
    private static final double MINIMUM_BALANCE = 0.0;

    /**
     * Valida que la cuenta tenga saldo >= 0
     *
     * @param account Cuenta a validar
     * @return Mono<Boolean> - true si el saldo es válido, false si no
     */
    @Override
    public Mono<Boolean> validate(Account account) {
        // Verificar que el saldo sea mayor o igual al mínimo
        boolean isValid = account.getBalance() >= MINIMUM_BALANCE;
        return Mono.just(isValid);
    }

    /**
     * Mensaje que se muestra cuando falla la validación
     *
     * @return Mensaje de error descriptivo
     */
    @Override
    public String getErrorMessage() {
        return "El saldo de la cuenta no puede ser menor a $" + MINIMUM_BALANCE;
    }
}
