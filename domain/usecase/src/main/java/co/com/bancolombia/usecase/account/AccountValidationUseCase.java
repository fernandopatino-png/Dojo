package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.validation.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * USE CASE: VALIDACIÓN DE CUENTAS
 *
 * Usa el PATRÓN STRATEGY para aplicar múltiples validaciones.
 *
 * ¿Cómo funciona?
 * 1. Recibe una lista de ESTRATEGIAS de validación
 * 2. Aplica TODAS las estrategias a la cuenta
 * 3. Si TODAS pasan → cuenta válida ✓
 * 4. Si alguna falla → cuenta inválida ✗
 *
 * VENTAJA:
 * Puedes agregar/quitar validaciones sin modificar este código.
 * Solo cambias la configuración de qué estrategias usar.
 *
 * Ejemplo:
 * List<ValidationStrategy> validaciones = Arrays.asList(
 *     new MinimumBalanceValidation(),
 *     new ActiveAccountValidation(),
 *     new OwnerExistsValidation()
 * );
 * AccountValidationUseCase useCase = new AccountValidationUseCase(validaciones);
 */
@RequiredArgsConstructor
public class AccountValidationUseCase {

    /**
     * Lista de estrategias de validación a aplicar
     * Se inyecta desde la configuración (ver UseCaseBeansConfig)
     */
    private final List<ValidationStrategy> strategies;

    /**
     * VALIDA UNA CUENTA USANDO TODAS LAS ESTRATEGIAS
     *
     * Algoritmo:
     * 1. Para cada estrategia de validación
     * 2. Ejecutar la validación
     * 3. Si TODAS retornan true → válida
     * 4. Si ALGUNA retorna false → inválida
     *
     * Es como un AND lógico: todas deben ser true.
     *
     * @param account Cuenta a validar
     * @return Mono<Boolean> - true si pasa todas las validaciones
     */
    public Mono<Boolean> validateAccount(Account account) {
        // Aplicar todas las estrategias
        return Flux.fromIterable(strategies)
            .flatMap(strategy -> strategy.validate(account))
            .all(result -> result); // AND lógico: todas deben ser true
    }

    /**
     * VALIDA Y RETORNA LOS ERRORES ESPECÍFICOS
     *
     * En vez de solo retornar true/false, retorna la LISTA DE ERRORES.
     * Útil para mostrar al usuario QUÉ validaciones fallaron.
     *
     * Ejemplo de retorno:
     * [
     *   "El saldo no puede ser negativo",
     *   "La cuenta debe tener un propietario válido"
     * ]
     *
     * @param account Cuenta a validar
     * @return Mono<List<String>> - lista de mensajes de error (vacía si todo OK)
     */
    public Mono<List<String>> validateWithErrors(Account account) {
        List<String> errors = new ArrayList<>();

        // Para cada estrategia
        return Flux.fromIterable(strategies)
            .flatMap(strategy ->
                // Ejecutar validación
                strategy.validate(account)
                    .map(isValid -> {
                        // Si falla, agregar mensaje de error
                        if (!isValid) {
                            errors.add(strategy.getErrorMessage());
                        }
                        return isValid;
                    })
            )
            .then(Mono.just(errors)); // Retornar lista de errores
    }

    /**
     * AGREGAR UNA NUEVA ESTRATEGIA DE VALIDACIÓN
     *
     * Permite agregar validaciones dinámicamente en tiempo de ejecución.
     *
     * @param strategy Nueva estrategia a agregar
     */
    public void addValidationStrategy(ValidationStrategy strategy) {
        this.strategies.add(strategy);
    }

    /**
     * REMOVER UNA ESTRATEGIA DE VALIDACIÓN
     *
     * Permite quitar validaciones dinámicamente.
     *
     * @param strategy Estrategia a remover
     */
    public void removeValidationStrategy(ValidationStrategy strategy) {
        this.strategies.remove(strategy);
    }
}

