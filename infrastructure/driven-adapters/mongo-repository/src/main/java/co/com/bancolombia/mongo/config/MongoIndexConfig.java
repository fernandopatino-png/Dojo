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
 * CONFIGURACIÓN DE ÍNDICES EN MONGODB
 *
 * ¿Qué son los ÍNDICES en una base de datos?
 * Son como el ÍNDICE de un libro:
 * - Sin índice: Tienes que leer TODO el libro para encontrar algo (lento)
 * - Con índice: Vas directo a la página que necesitas (rápido)
 *
 * ANALOGÍA:
 * Imagina buscar un contacto en tu teléfono:
 * - Sin índice: Revisas contacto por contacto (O(n) - lento)
 * - Con índice: El teléfono te muestra resultados instantáneos (O(log n) - rápido)
 *
 * IMPACTO EN RENDIMIENTO:
 * - SIN índice en campo "ownerId": Buscar 1 cuenta entre 1,000,000 = 1,000,000 comparaciones
 * - CON índice en "ownerId": Buscar 1 cuenta entre 1,000,000 = ~20 comparaciones
 *
 * ¿Cuándo crear índices?
 * - Campos que usas frecuentemente en WHERE/filtros
 * - Campos que usas para ordenar (ORDER BY)
 * - Campos únicos (como email)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoIndexConfig {

    private final ReactiveMongoTemplate mongoTemplate;

    /**
     * CREA LOS ÍNDICES AL INICIAR LA APLICACIÓN
     *
     * @EventListener(ApplicationReadyEvent.class) significa:
     * "Ejecuta este método cuando la aplicación esté lista"
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initIndexes() {
        log.info("🔧 Creando índices en MongoDB para optimizar consultas...");

        createAccountIndexes();
        createUserIndexes();

        log.info("✅ Índices creados exitosamente");
    }

    /**
     * ÍNDICES PARA LA COLECCIÓN DE CUENTAS
     */
    private void createAccountIndexes() {
        // ÍNDICE 1: Por ownerId (propietario)
        // ¿Por qué? Porque frecuentemente buscamos "todas las cuentas de un usuario"
        mongoTemplate.indexOps(AccountData.class)
            .ensureIndex(new Index().on("ownerId", Sort.Direction.ASC))
            .doOnSuccess(index ->
                log.info("   ✓ Índice creado: AccountData.ownerId")
            )
            .subscribe();

        // ÍNDICE 2: Compuesto por ownerId + balance
        // ¿Por qué? Para consultas como "cuentas de un usuario ordenadas por saldo"
        // Más eficiente que usar dos índices separados
        mongoTemplate.indexOps(AccountData.class)
            .ensureIndex(new Index()
                .on("ownerId", Sort.Direction.ASC)
                .on("balance", Sort.Direction.DESC))
            .doOnSuccess(index ->
                log.info("   ✓ Índice compuesto creado: AccountData.ownerId+balance")
            )
            .subscribe();
    }

    /**
     * ÍNDICES PARA LA COLECCIÓN DE USUARIOS
     */
    private void createUserIndexes() {
        // ÍNDICE 1: Por type (tipo de usuario)
        // ¿Por qué? Para filtrar usuarios por tipo (BASIC, PREMIUM, VIP)
        mongoTemplate.indexOps(UserData.class)
            .ensureIndex(new Index().on("type", Sort.Direction.ASC))
            .doOnSuccess(index ->
                log.info("   ✓ Índice creado: UserData.type")
            )
            .subscribe();

        // Nota: El índice de email único se creará cuando agregues el campo email a UserData
    }
}
