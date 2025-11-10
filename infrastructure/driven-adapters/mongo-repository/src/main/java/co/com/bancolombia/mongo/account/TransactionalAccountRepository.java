package co.com.bancolombia.mongo.account;

import co.com.bancolombia.mongo.user.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Repositorio que demuestra el uso de transacciones reactivas en MongoDB
 * Las transacciones garantizan ACID (Atomicity, Consistency, Isolation, Durability)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionalAccountRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    /**
     * Crea un usuario y una cuenta en una transacción
     * Si falla alguna operación, se hace rollback de ambas
     */
    public Mono<UserAccountResult> createUserWithAccount(UserData user, AccountData account) {
        log.info("Creating user {} with account in transaction", user.getId());

        return mongoTemplate.inTransaction()
            .execute(action ->
                action.insert(user)
                    .flatMap(savedUser ->
                        action.insert(account)
                            .map(savedAccount -> new UserAccountResult(savedUser, savedAccount))
                    )
            )
            .next()
            .doOnSuccess(result -> log.info("Transaction completed successfully"))
            .doOnError(error -> log.error("Transaction failed and rolled back: {}", error.getMessage()));
    }

    /**
     * Transfiere saldo entre dos cuentas de forma transaccional
     * Ambas operaciones se ejecutan o ninguna
     */
    public Mono<TransferTransactionResult> transferBetweenAccountsTransactional(
            Long fromAccountId, Long toAccountId, Double amount) {

        log.info("Starting transactional transfer from {} to {} amount {}",
            fromAccountId, toAccountId, amount);

        return mongoTemplate.inTransaction()
            .execute(action ->
                action.findById(fromAccountId, AccountData.class)
                    .zipWith(action.findById(toAccountId, AccountData.class))
                    .flatMap(accounts -> {
                        AccountData from = accounts.getT1();
                        AccountData to = accounts.getT2();

                        if (from.getBalance() < amount) {
                            return Mono.error(new IllegalStateException("Insufficient balance"));
                        }

                        from.setBalance(from.getBalance() - amount);
                        to.setBalance(to.getBalance() + amount);

                        return action.save(from)
                            .then(action.save(to))
                            .thenReturn(new TransferTransactionResult(
                                fromAccountId, toAccountId, amount, true, "Success"
                            ));
                    })
            )
            .next()
            .doOnSuccess(result -> log.info("Transfer transaction completed: {}", result))
            .onErrorResume(error -> {
                log.error("Transfer transaction failed: {}", error.getMessage());
                return Mono.just(new TransferTransactionResult(
                    fromAccountId, toAccountId, amount, false, error.getMessage()
                ));
            });
    }

    /**
     * Resultado de crear usuario con cuenta
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class UserAccountResult {
        private UserData user;
        private AccountData account;
    }

    /**
     * Resultado de transferencia transaccional
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class TransferTransactionResult {
        private Long fromAccountId;
        private Long toAccountId;
        private Double amount;
        private boolean success;
        private String message;
    }
}

