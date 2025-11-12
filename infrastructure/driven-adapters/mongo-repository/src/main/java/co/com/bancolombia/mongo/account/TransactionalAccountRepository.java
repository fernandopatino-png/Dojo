package co.com.bancolombia.mongo.account;

import co.com.bancolombia.mongo.user.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Repositorio que demuestra operaciones coordinadas en MongoDB
 * Nota: Para transacciones ACID reales, MongoDB requiere un Replica Set configurado
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionalAccountRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    /**
     * Crea un usuario y una cuenta de forma coordinada
     * Si falla alguna operación, no se completa ninguna
     */
    public Mono<UserAccountResult> createUserWithAccount(UserData user, AccountData account) {
        log.info("Creating user {} with account", user.getId());

        return mongoTemplate.insert(user)
            .flatMap(savedUser ->
                mongoTemplate.insert(account)
                    .map(savedAccount -> new UserAccountResult(savedUser, savedAccount))
            )
            .doOnSuccess(result -> log.info("User and account created successfully"))
            .doOnError(error -> log.error("Failed to create user with account: {}", error.getMessage()));
    }

    /**
     * Transfiere saldo entre dos cuentas
     * Nota: Sin transacciones, esto no es atómico. Para producción, usar transacciones reales.
     */
    public Mono<TransferTransactionResult> transferBetweenAccountsTransactional(
            Long fromAccountId, Long toAccountId, Double amount) {

        log.info("Starting transfer from {} to {} amount {}",
            fromAccountId, toAccountId, amount);

        return mongoTemplate.findById(fromAccountId, AccountData.class)
            .zipWith(mongoTemplate.findById(toAccountId, AccountData.class))
            .flatMap(accounts -> {
                AccountData from = accounts.getT1();
                AccountData to = accounts.getT2();

                if (from == null || to == null) {
                    return Mono.error(new IllegalStateException("One or both accounts not found"));
                }

                if (from.getBalance() < amount) {
                    return Mono.error(new IllegalStateException("Insufficient balance"));
                }

                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);

                return mongoTemplate.save(from)
                    .then(mongoTemplate.save(to))
                    .thenReturn(new TransferTransactionResult(
                        fromAccountId, toAccountId, amount, true, "Success"
                    ));
            })
            .doOnSuccess(result -> log.info("Transfer completed: {}", result))
            .onErrorResume(error -> {
                log.error("Transfer failed: {}", error.getMessage());
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
