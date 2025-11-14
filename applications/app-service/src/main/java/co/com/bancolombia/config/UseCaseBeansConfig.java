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

@Configuration
public class UseCaseBeansConfig {

    @Bean
    public AccountManagementUseCase accountManagementUseCase(
            AccountRepository accountRepository) {
        return new AccountManagementUseCase(accountRepository);
    }

    @Bean
    public TransferUseCase transferUseCase(
            AccountRepository accountRepository) {
        return new TransferUseCase(accountRepository);
    }

    @Bean
    public AccountSearchUseCase accountSearchUseCase(
            AccountRepository accountRepository) {
        return new AccountSearchUseCase(accountRepository);
    }

    @Bean
    public TransactionHistoryUseCase transactionHistoryUseCase() {
        return new TransactionHistoryUseCase();
    }

    @Bean
    public AccountValidationUseCase accountValidationUseCase() {
        // Crear lista de estrategias de validación
        List<ValidationStrategy> strategies = new ArrayList<>();
        strategies.add(new MinimumBalanceValidation());
        strategies.add(new ActiveAccountValidation());
        strategies.add(new OwnerExistsValidation());

        return new AccountValidationUseCase(strategies);
    }

    @Bean
    public AccountEventUseCase accountEventUseCase() {
        return new AccountEventUseCase();
    }
}
