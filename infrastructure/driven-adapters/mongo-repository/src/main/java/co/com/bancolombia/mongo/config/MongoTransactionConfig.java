package co.com.bancolombia.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;

/**
 * Configuración para habilitar transacciones ACID en MongoDB
 *
 * REQUISITOS:
 * 1. MongoDB debe estar ejecutándose como Replica Set (no standalone)
 * 2. MongoDB versión 4.0 o superior
 *
 * Para habilitar transacciones, descomentar el bean y asegurar que MongoDB
 * esté configurado correctamente como Replica Set.
 *
 * Para desarrollo local:
 * - Usar Docker con replica set
 * - O configurar MongoDB local como replica set
 *
 * Sin esta configuración, las operaciones en TransactionalAccountRepository
 * funcionarán pero NO serán atómicas.
 */
@Configuration
public class MongoTransactionConfig {

    /**
     * Descomentar este bean para habilitar transacciones
     * Solo funciona si MongoDB está en modo Replica Set
     */
    /*
    @Bean
    public ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }
    */

    // Para usar transacciones, el código quedaría así:
    /*
    public Mono<UserAccountResult> createUserWithAccountTransactional(UserData user, AccountData account) {
        return mongoTemplate.inTransaction()
            .execute(action ->
                action.insert(user)
                    .flatMap(savedUser ->
                        action.insert(account)
                            .map(savedAccount -> new UserAccountResult(savedUser, savedAccount))
                    )
            )
            .singleOrEmpty();
    }
    */
}

