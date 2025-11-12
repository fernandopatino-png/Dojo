package co.com.bancolombia.model.account.validation;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Mono;

/**
 * PATRÓN STRATEGY - Interfaz de Estrategia de Validación
 *
 * ¿Qué es el PATRÓN STRATEGY?
 * Es un patrón de diseño que permite definir una FAMILIA DE ALGORITMOS,
 * encapsular cada uno, y hacerlos INTERCAMBIABLES.
 *
 * ANALOGÍA:
 * Imagina que tienes diferentes formas de validar una cuenta:
 * - Validar saldo mínimo
 * - Validar que esté activa
 * - Validar que tenga propietario
 *
 * En vez de escribir todo en un IF gigante, creas una ESTRATEGIA para cada validación.
 *
 * VENTAJAS:
 * 1. Fácil agregar nuevas validaciones sin modificar código existente
 * 2. Cada validación está en su propia clase (Responsabilidad Única)
 * 3. Puedes combinar validaciones como quieras
 * 4. El código queda organizado y fácil de entender
 *
 * EJEMPLO DE USO:
 * ValidationStrategy validacion1 = new MinimumBalanceValidation();
 * ValidationStrategy validacion2 = new ActiveAccountValidation();
 *
 * boolean esValida = validacion1.validate(cuenta) && validacion2.validate(cuenta);
 */
public interface ValidationStrategy {

    /**
     * Valida una cuenta según la regla específica de esta estrategia
     *
     * @param account Cuenta a validar
     * @return Mono<Boolean> - true si pasa la validación, false si no
     */
    Mono<Boolean> validate(Account account);

    /**
     * Obtiene el mensaje de error si la validación falla
     *
     * @return Mensaje descriptivo del error
     */
    String getErrorMessage();
}
