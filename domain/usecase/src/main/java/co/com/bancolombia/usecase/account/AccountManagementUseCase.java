package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AccountManagementUseCase {

    private final AccountRepository accountRepository;

    public Mono<Account> createAccount(Account account) {
        log.info("📝 Creando nueva cuenta para el usuario {}", account.getOwnerId());

        //Validación 1: Saldo inicial no negativo
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

        //Guardar la cuenta en la base de datos
        return accountRepository.save(account)
                .doOnSuccess(saved ->
                        log.info("✅ Cuenta creada exitosamente con ID: {}", saved.getId())
                )
                .doOnError(error ->
                        log.error("❌ Error al crear cuenta: {}", error.getMessage())
                );
    }

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

    public Flux<Account> getAccountsByOwner(Long ownerId) {
        log.info("🔍 Buscando cuentas del usuario {}", ownerId);

        return accountRepository.findByOwnerId(ownerId)
                .doOnComplete(() ->
                        log.info("✅ Búsqueda completada para usuario {}", ownerId)
                );
    }

    public Flux<Account> getAllAccounts() {
        log.info("📋 Listando todas las cuentas del sistema");

        return accountRepository.findAll();
    }

    public Mono<Account> updateBalance(Long accountId, Double newBalance) {
        log.info("💰 Actualizando saldo de cuenta {} a ${}", accountId, newBalance);

        //Validar que el nuevo saldo sea válido
        if (newBalance < 0) {
            return Mono.error(new IllegalArgumentException(
                    "El saldo no puede ser negativo"
            ));
        }

        //Buscar la cuenta, actualizar y guardar
        return accountRepository.getAccountById(accountId)
                .flatMap(account -> {
                    Double oldBalance = account.getBalance();

                    //Crear cuenta actualizada
                    Account updatedAccount = Account.builder()
                            .id(account.getId())
                            .ownerId(account.getOwnerId())
                            .balance(newBalance)
                            .build();

                    //Guardar cambios
                    return accountRepository.update(updatedAccount)
                            .doOnSuccess(updated ->
                                    log.info("✅ Saldo actualizado: ${} → ${}",
                                            oldBalance, newBalance)
                            );
                });
    }

    public Mono<Void> deleteAccount(Long accountId) {
        log.info("🗑️ Intentando eliminar cuenta {}", accountId);

        return accountRepository.getAccountById(accountId)
                .flatMap(account -> {
                    //Validar que el saldo sea 0
                    if (account.getBalance() > 0) {
                        return Mono.error(new IllegalStateException(
                                "No se puede eliminar una cuenta con saldo positivo. " +
                                        "Saldo actual: $" + account.getBalance()
                        ));
                    }
                    //Si el saldo es 0, eliminar
                    return accountRepository.delete(accountId)
                            .doOnSuccess(v ->
                                    log.info("✅ Cuenta {} eliminada exitosamente", accountId)
                            );
                });
    }

    public Mono<Boolean> accountExists(Long accountId) {
        log.info("🔍 Verificando existencia de cuenta {}", accountId);
        return accountRepository.exists(accountId);
    }
}

