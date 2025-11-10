package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use Case completo para gestión de cuentas siguiendo Clean Architecture
 */
@Slf4j
@RequiredArgsConstructor
public class AccountManagementUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountValidationUseCase validationUseCase;
    private final AccountEventUseCase eventUseCase;

    /**
     * Crea una nueva cuenta validando que el usuario exista
     */
    public Mono<Account> createAccount(Account account) {
        log.info("Creating new account for owner: {}", account.getOwnerId());

        return validationUseCase.validateAccount(account)
            .flatMap(isValid -> {
                if (!isValid) {
                    return validationUseCase.validateWithErrors(account)
                        .flatMap(errors -> Mono.error(new IllegalArgumentException(
                            "Account validation failed: " + String.join(", ", errors))));
                }

                return userRepository.validateUserExists(account.getOwnerId())
                    .flatMap(exists -> {
                        if (!exists) {
                            return Mono.error(new IllegalStateException("User does not exist"));
                        }
                        return accountRepository.save(account)
                            .flatMap(savedAccount ->
                                eventUseCase.notifyAccountCreated(savedAccount)
                                    .thenReturn(savedAccount)
                            );
                    });
            });
    }

    /**
     * Obtiene una cuenta por ID
     */
    public Mono<Account> getAccountById(Long id) {
        return accountRepository.getAccountById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Account not found: " + id)));
    }

    /**
     * Obtiene todas las cuentas de un propietario
     */
    public Flux<Account> getAccountsByOwner(Long ownerId) {
        return accountRepository.findByOwnerId(ownerId);
    }

    /**
     * Actualiza el saldo de una cuenta
     */
    public Mono<Account> updateBalance(Long accountId, Double newBalance) {
        return accountRepository.getAccountById(accountId)
            .flatMap(account -> {
                Double oldBalance = account.getBalance();
                Account updatedAccount = Account.builder()
                    .id(account.getId())
                    .ownerId(account.getOwnerId())
                    .balance(newBalance)
                    .build();

                return accountRepository.update(updatedAccount)
                    .flatMap(saved ->
                        eventUseCase.notifyBalanceChanged(saved, oldBalance, newBalance)
                            .thenReturn(saved)
                    );
            });
    }

    /**
     * Elimina una cuenta (solo si tiene saldo 0)
     */
    public Mono<Void> deleteAccount(Long accountId) {
        return accountRepository.getAccountById(accountId)
            .flatMap(account -> {
                if (account.getBalance() > 0) {
                    return Mono.error(new IllegalStateException(
                        "Cannot delete account with positive balance"));
                }

                return accountRepository.delete(accountId)
                    .then(eventUseCase.notifyAccountDeleted(accountId));
            });
    }

    /**
     * Lista todas las cuentas
     */
    public Flux<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}

