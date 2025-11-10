package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.TransferResult;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.UUID;

/**
 * Use Case de transferencia con reglas de negocio complejas
 * Demuestra Clean Architecture: lógica de negocio independiente de frameworks
 */
@Slf4j
@RequiredArgsConstructor
public class TransferUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountEventUseCase eventUseCase;

    private static final Double MAX_TRANSFER_AMOUNT = 10000.0;
    private static final Double MIN_BALANCE_AFTER_TRANSFER = 0.0;

    /**
     * Ejecuta una transferencia entre dos cuentas con validaciones completas
     */
    public Mono<TransferResult> transfer(Long fromAccountId, Long toAccountId, Double amount) {
        log.info("Initiating transfer from {} to {} amount: {}", fromAccountId, toAccountId, amount);

        // Validaciones iniciales
        if (fromAccountId.equals(toAccountId)) {
            return Mono.just(TransferResult.failure(fromAccountId, toAccountId, amount,
                "Cannot transfer to the same account"));
        }

        if (amount <= 0) {
            return Mono.just(TransferResult.failure(fromAccountId, toAccountId, amount,
                "Transfer amount must be positive"));
        }

        if (amount > MAX_TRANSFER_AMOUNT) {
            return Mono.just(TransferResult.failure(fromAccountId, toAccountId, amount,
                "Transfer amount exceeds maximum limit of " + MAX_TRANSFER_AMOUNT));
        }

        // Buscar ambas cuentas
        return accountRepository.getAccountById(fromAccountId)
            .zipWith(accountRepository.getAccountById(toAccountId))
            .flatMap(tuple -> validateAndExecuteTransfer(tuple, amount))
            .flatMap(result -> saveAccountsAndNotify(result))
            .onErrorResume(error -> {
                log.error("Transfer failed: {}", error.getMessage());
                return Mono.just(TransferResult.failure(fromAccountId, toAccountId, amount,
                    "Transfer failed: " + error.getMessage()));
            });
    }

    /**
     * Valida las reglas de negocio y ejecuta la transferencia
     */
    private Mono<TransferContext> validateAndExecuteTransfer(Tuple2<Account, Account> accounts, Double amount) {
        Account fromAccount = accounts.getT1();
        Account toAccount = accounts.getT2();

        // Validar saldo suficiente
        if (fromAccount.getBalance() < amount) {
            return Mono.error(new IllegalStateException(
                "Insufficient balance. Available: " + fromAccount.getBalance()));
        }

        // Validar saldo mínimo después de la transferencia
        if (fromAccount.getBalance() - amount < MIN_BALANCE_AFTER_TRANSFER) {
            return Mono.error(new IllegalStateException(
                "Transfer would leave balance below minimum required: " + MIN_BALANCE_AFTER_TRANSFER));
        }

        // Validar que ambas cuentas tengan propietarios
        return userRepository.validateUserExists(fromAccount.getOwnerId())
            .zipWith(userRepository.validateUserExists(toAccount.getOwnerId()))
            .flatMap(userValidations -> {
                if (!userValidations.getT1()) {
                    return Mono.error(new IllegalStateException("Source account owner not found"));
                }
                if (!userValidations.getT2()) {
                    return Mono.error(new IllegalStateException("Destination account owner not found"));
                }

                // Ejecutar la transferencia
                Double oldFromBalance = fromAccount.getBalance();
                Double oldToBalance = toAccount.getBalance();

                Account updatedFromAccount = Account.builder()
                    .id(fromAccount.getId())
                    .ownerId(fromAccount.getOwnerId())
                    .balance(fromAccount.getBalance() - amount)
                    .build();

                Account updatedToAccount = Account.builder()
                    .id(toAccount.getId())
                    .ownerId(toAccount.getOwnerId())
                    .balance(toAccount.getBalance() + amount)
                    .build();

                return Mono.just(new TransferContext(
                    updatedFromAccount, updatedToAccount,
                    oldFromBalance, oldToBalance, amount
                ));
            });
    }

    /**
     * Guarda las cuentas actualizadas y notifica eventos
     */
    private Mono<TransferResult> saveAccountsAndNotify(TransferContext context) {
        return accountRepository.update(context.fromAccount)
            .then(accountRepository.update(context.toAccount))
            .then(Mono.defer(() -> {
                // Notificar eventos de cambio de saldo
                return eventUseCase.notifyBalanceChanged(
                    context.fromAccount, context.oldFromBalance, context.fromAccount.getBalance()
                ).then(eventUseCase.notifyBalanceChanged(
                    context.toAccount, context.oldToBalance, context.toAccount.getBalance()
                ));
            }))
            .then(Mono.just(TransferResult.success(
                UUID.randomUUID().toString(),
                context.fromAccount.getId(),
                context.toAccount.getId(),
                context.amount
            )));
    }

    /**
     * Clase interna para mantener el contexto de la transferencia
     */
    private static class TransferContext {
        final Account fromAccount;
        final Account toAccount;
        final Double oldFromBalance;
        final Double oldToBalance;
        final Double amount;

        TransferContext(Account fromAccount, Account toAccount,
                       Double oldFromBalance, Double oldToBalance, Double amount) {
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.oldFromBalance = oldFromBalance;
            this.oldToBalance = oldToBalance;
            this.amount = amount;
        }
    }
}

