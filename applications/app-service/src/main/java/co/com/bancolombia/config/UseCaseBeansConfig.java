package co.com.bancolombia.config;

import co.com.bancolombia.model.account.gateways.AccountRepository;
import co.com.bancolombia.model.account.validation.ActiveAccountValidation;
import co.com.bancolombia.model.account.validation.MinimumBalanceValidation;
import co.com.bancolombia.model.account.validation.OwnerExistsValidation;
import co.com.bancolombia.model.account.validation.ValidationStrategy;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.account.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuración de beans para todos los Use Cases
 * Demuestra Inyección de Dependencias y Clean Architecture
 */
@Configuration
public class UseCaseBeansConfig {

    /**
     * Use Case principal de gestión de cuentas
     */
    @Bean
    public AccountManagementUseCase accountManagementUseCase(
            AccountRepository accountRepository,
            UserRepository userRepository,
            AccountValidationUseCase accountValidationUseCase,
            AccountEventUseCase accountEventUseCase) {
        return new AccountManagementUseCase(
            accountRepository,
            userRepository,
            accountValidationUseCase,
            accountEventUseCase
        );
    }

    /**
     * Use Case de transferencias
     */
    @Bean
    public TransferUseCase transferUseCase(
            AccountRepository accountRepository,
            UserRepository userRepository,
            AccountEventUseCase accountEventUseCase) {
        return new TransferUseCase(accountRepository, userRepository, accountEventUseCase);
    }

    /**
     * Use Case de búsqueda con diferentes complejidades algorítmicas
     */
    @Bean
    public AccountSearchUseCase accountSearchUseCase(AccountRepository accountRepository) {
        return new AccountSearchUseCase(accountRepository);
    }

    /**
     * Use Case de historial de transacciones (Estructura de datos: Deque)
     */
    @Bean
    public TransactionHistoryUseCase transactionHistoryUseCase() {
        return new TransactionHistoryUseCase();
    }

    /**
     * Use Case de validación con Strategy Pattern
     */
    @Bean
    public AccountValidationUseCase accountValidationUseCase() {
        List<ValidationStrategy> strategies = new ArrayList<>();
        strategies.add(new MinimumBalanceValidation());
        strategies.add(new ActiveAccountValidation());
        strategies.add(new OwnerExistsValidation());
        return new AccountValidationUseCase(strategies);
    }

    /**
     * Use Case de eventos con Observer Pattern
     */
    @Bean
    public AccountEventUseCase accountEventUseCase() {
        return new AccountEventUseCase();
    }
}

