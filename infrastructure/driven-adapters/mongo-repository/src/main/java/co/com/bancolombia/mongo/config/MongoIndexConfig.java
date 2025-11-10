package co.com.bancolombia.mongo.config;

import co.com.bancolombia.mongo.account.AccountData;
import co.com.bancolombia.mongo.user.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

/**
 * Configuración de índices MongoDB para optimizar consultas
 * Los índices mejoran la complejidad de O(n) a O(log n) en búsquedas
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoIndexConfig {

    private final ReactiveMongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndexes() {
        log.info("Creating MongoDB indexes...");

        // Índice en AccountData por ownerId para búsquedas rápidas
        mongoTemplate.indexOps(AccountData.class)
            .ensureIndex(new Index().on("ownerId", Sort.Direction.ASC))
            .doOnSuccess(index -> log.info("Index created on AccountData.ownerId: {}", index))
            .subscribe();

        // Índice compuesto en AccountData por ownerId y balance para consultas complejas
        mongoTemplate.indexOps(AccountData.class)
            .ensureIndex(new Index()
                .on("ownerId", Sort.Direction.ASC)
                .on("balance", Sort.Direction.DESC))
            .doOnSuccess(index -> log.info("Compound index created on AccountData: {}", index))
            .subscribe();

        // Índice en UserData por email (si se agrega el campo)
        mongoTemplate.indexOps(UserData.class)
            .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique())
            .doOnSuccess(index -> log.info("Unique index created on UserData.email: {}", index))
            .subscribe();

        // Índice en UserData por type para filtrar por tipo de usuario
        mongoTemplate.indexOps(UserData.class)
            .ensureIndex(new Index().on("type", Sort.Direction.ASC))
            .doOnSuccess(index -> log.info("Index created on UserData.type: {}", index))
            .subscribe();

        log.info("MongoDB indexes creation completed");
    }
}

