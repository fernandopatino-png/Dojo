package co.com.bancolombia.config;

import co.com.bancolombia.model.account.gateways.AccountRepository;
import co.com.bancolombia.model.account.validation.ActiveAccountValidation;
import co.com.bancolombia.model.account.validation.MinimumBalanceValidation;
import co.com.bancolombia.model.account.validation.OwnerExistsValidation;
import co.com.bancolombia.model.account.validation.ValidationStrategy;
import co.com.bancolombia.usecase.account.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * CONFIGURACIÓN DE BEANS DE USE CASES
 *
 * ¿Qué es un BEAN en Spring?
 * Un objeto que Spring crea y administra automáticamente.
 *
 * ¿Qué es INYECCIÓN DE DEPENDENCIAS?
 * En vez de crear objetos con "new", le pides a Spring que te los dé.
 *
 * VENTAJAS:
 * 1. Fácil testing (puedes inyectar mocks)
 * 2. Desacoplamiento (no dependes de implementaciones concretas)
 * 3. Configuración centralizada (todo en un lugar)
 *
 * EJEMPLO SIN inyección de dependencias:
 * AccountManagementUseCase useCase = new AccountManagementUseCase(
 *     new MongoAccountRepository(),
 *     new MongoUserRepository(),
 *     new AccountValidationUseCase(...),
 *     new AccountEventUseCase()
 * );
 *
 * CON inyección de dependencias:
 * @Autowired
 * AccountManagementUseCase useCase; // Spring lo crea automáticamente
 */
@Configuration
public class UseCaseBeansConfig {

    /**
     * BEAN: Use Case de Gestión de Cuentas
     *
     * Spring ejecuta este método y guarda el resultado.
     * Cuando alguien necesita AccountManagementUseCase, Spring usa este objeto.
     *
     * @param accountRepository Inyectado automáticamente por Spring
     * @return Instancia configurada del Use Case
     */
    @Bean
    public AccountManagementUseCase accountManagementUseCase(
            AccountRepository accountRepository) {
        return new AccountManagementUseCase(accountRepository);
    }

    /**
     * BEAN: Use Case de Transferencias
     *
     * Implementa la lógica de transferir dinero entre cuentas.
     */
    @Bean
    public TransferUseCase transferUseCase(
            AccountRepository accountRepository) {
        return new TransferUseCase(accountRepository);
    }

    /**
     * BEAN: Use Case de Búsqueda
     *
     * Implementa diferentes algoritmos de búsqueda con sus complejidades.
     */
    @Bean
    public AccountSearchUseCase accountSearchUseCase(
            AccountRepository accountRepository) {
        return new AccountSearchUseCase(accountRepository);
    }

    /**
     * BEAN: Use Case de Historial de Transacciones
     *
     * Usa estructura de datos Deque para mantener historial.
     */
    @Bean
    public TransactionHistoryUseCase transactionHistoryUseCase() {
        return new TransactionHistoryUseCase();
    }

    /**
     * BEAN: Use Case de Validación
     *
     * Usa el PATRÓN STRATEGY para aplicar múltiples validaciones.
     *
     * Aquí configuramos QUÉ validaciones usar:
     * - MinimumBalanceValidation: Saldo >= 0
     * - ActiveAccountValidation: Cuenta con datos completos
     * - OwnerExistsValidation: Propietario válido
     */
    @Bean
    public AccountValidationUseCase accountValidationUseCase() {
        // Crear lista de estrategias de validación
        List<ValidationStrategy> strategies = new ArrayList<>();
        strategies.add(new MinimumBalanceValidation());
        strategies.add(new ActiveAccountValidation());
        strategies.add(new OwnerExistsValidation());

        return new AccountValidationUseCase(strategies);
    }

    /**
     * BEAN: Use Case de Eventos
     *
     * Usa el PATRÓN OBSERVER para notificar cambios.
     * Los listeners se registran en EventListenersConfig.
     */
    @Bean
    public AccountEventUseCase accountEventUseCase() {
        return new AccountEventUseCase();
    }
}
