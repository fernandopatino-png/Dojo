package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * USE CASE: GESTIÓN DE CUENTAS (CRUD)
 *
 * CRUD = Create, Read, Update, Delete
 * Las 4 operaciones básicas en cualquier sistema.
 *
 * Este Use Case implementa la LÓGICA DE NEGOCIO para:
 * - Crear cuentas nuevas
 * - Consultar cuentas existentes
 * - Actualizar saldos
 * - Eliminar cuentas
 *
 * CLEAN ARCHITECTURE:
 * Este caso de uso NO sabe cómo se guardan las cuentas (MongoDB, SQL, etc.)
 * Solo usa la interfaz AccountRepository.
 * Eso es INVERSIÓN DE DEPENDENCIAS (la D de SOLID).
 */
@Slf4j
@RequiredArgsConstructor
public class AccountManagementUseCase {

    // Dependencia: Repositorio de cuentas (abstracción)
    private final AccountRepository accountRepository;

    /**
     * CREAR UNA NUEVA CUENTA
     *
     * Aplica validaciones básicas antes de crear:
     * - El saldo inicial debe ser >= 0
     * - Debe tener un propietario válido
     *
     * @param account Cuenta a crear
     * @return Mono con la cuenta creada (incluyendo ID generado)
     */
    public Mono<Account> createAccount(Account account) {
        log.info("📝 Creando nueva cuenta para el usuario {}", account.getOwnerId());

        // Validación 1: Saldo inicial no negativo
        if (account.getBalance() < 0) {
            return Mono.error(new IllegalArgumentException(
                "El saldo inicial no puede ser negativo"
            ));
        }

        // Validación 2: Debe tener propietario
        if (account.getOwnerId() == null || account.getOwnerId() <= 0) {
            return Mono.error(new IllegalArgumentException(
                "La cuenta debe tener un propietario válido"
            ));
        }

        // Guardar la cuenta en el repositorio
        return accountRepository.save(account)
            .doOnSuccess(saved ->
                log.info("✅ Cuenta creada exitosamente con ID: {}", saved.getId())
            )
            .doOnError(error ->
                log.error("❌ Error al crear cuenta: {}", error.getMessage())
            );
    }

    /**
     * OBTENER UNA CUENTA POR ID
     *
     * Busca una cuenta específica por su identificador.
     *
     * @param id ID de la cuenta a buscar
     * @return Mono con la cuenta encontrada
     */
    public Mono<Account> getAccountById(Long id) {
        log.info("🔍 Buscando cuenta con ID: {}", id);

        return accountRepository.getAccountById(id)
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException("No existe una cuenta con ID: " + id)
            ))
            .doOnSuccess(account ->
                log.info("✅ Cuenta encontrada: ID={}, Saldo=${}",
                    account.getId(), account.getBalance())
            );
    }

    /**
     * OBTENER TODAS LAS CUENTAS DE UN USUARIO
     *
     * Lista todas las cuentas que pertenecen a un propietario.
     * Un usuario puede tener múltiples cuentas.
     *
     * @param ownerId ID del propietario
     * @return Flux con todas las cuentas del usuario
     */
    public Flux<Account> getAccountsByOwner(Long ownerId) {
        log.info("🔍 Buscando cuentas del usuario {}", ownerId);

        return accountRepository.findByOwnerId(ownerId)
            .doOnComplete(() ->
                log.info("✅ Búsqueda completada para usuario {}", ownerId)
            );
    }

    /**
     * LISTAR TODAS LAS CUENTAS DEL SISTEMA
     *
     * Retorna todas las cuentas (útil para reportes o administración).
     *
     * @return Flux con todas las cuentas
     */
    public Flux<Account> getAllAccounts() {
        log.info("📋 Listando todas las cuentas del sistema");

        return accountRepository.findAll();
    }

    /**
     * ACTUALIZAR EL SALDO DE UNA CUENTA
     *
     * Cambia el saldo de una cuenta existente.
     * Útil para depósitos, retiros, ajustes, etc.
     *
     * @param accountId ID de la cuenta
     * @param newBalance Nuevo saldo
     * @return Mono con la cuenta actualizada
     */
    public Mono<Account> updateBalance(Long accountId, Double newBalance) {
        log.info("💰 Actualizando saldo de cuenta {} a ${}", accountId, newBalance);

        // Validar que el nuevo saldo sea válido
        if (newBalance < 0) {
            return Mono.error(new IllegalArgumentException(
                "El saldo no puede ser negativo"
            ));
        }

        // Buscar la cuenta, actualizar y guardar
        return accountRepository.getAccountById(accountId)
            .flatMap(account -> {
                Double oldBalance = account.getBalance();

                // Crear cuenta actualizada
                Account updatedAccount = Account.builder()
                    .id(account.getId())
                    .ownerId(account.getOwnerId())
                    .balance(newBalance)
                    .build();

                // Guardar cambios
                return accountRepository.update(updatedAccount)
                    .doOnSuccess(updated ->
                        log.info("✅ Saldo actualizado: ${} → ${}",
                            oldBalance, newBalance)
                    );
            });
    }

    /**
     * ELIMINAR UNA CUENTA
     *
     * Elimina una cuenta del sistema.
     *
     * REGLA DE NEGOCIO:
     * Solo se puede eliminar una cuenta si su saldo es $0.
     * Esto previene pérdida de dinero.
     *
     * @param accountId ID de la cuenta a eliminar
     * @return Mono<Void> - operación sin retorno
     */
    public Mono<Void> deleteAccount(Long accountId) {
        log.info("🗑️ Intentando eliminar cuenta {}", accountId);

        return accountRepository.getAccountById(accountId)
            .flatMap(account -> {
                // Validar que el saldo sea 0
                if (account.getBalance() > 0) {
                    return Mono.error(new IllegalStateException(
                        "No se puede eliminar una cuenta con saldo positivo. " +
                        "Saldo actual: $" + account.getBalance()
                    ));
                }

                // Si el saldo es 0, eliminar
                return accountRepository.delete(accountId)
                    .doOnSuccess(v ->
                        log.info("✅ Cuenta {} eliminada exitosamente", accountId)
                    );
            });
    }

    /**
     * VERIFICAR SI EXISTE UNA CUENTA
     *
     * Verifica si una cuenta existe sin traer todos sus datos.
     * Más eficiente que getAccountById cuando solo necesitas saber si existe.
     *
     * @param accountId ID de la cuenta
     * @return Mono<Boolean> - true si existe, false si no
     */
    public Mono<Boolean> accountExists(Long accountId) {
        log.info("🔍 Verificando existencia de cuenta {}", accountId);

        return accountRepository.exists(accountId);
    }
}

