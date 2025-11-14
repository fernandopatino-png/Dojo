package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.TransferResult;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TransferUseCase {

    private final AccountRepository accountRepository;
    private static final Double MAX_TRANSFER_AMOUNT = 10000.0;

    public Mono<TransferResult> transfer(Long fromAccountId, Long toAccountId, Double amount) {
        log.info("🔄 Iniciando transferencia: ${} desde cuenta {} hacia cuenta {}",
                amount, fromAccountId, toAccountId);

        //VALIDACIÓN 1: Las cuentas deben ser diferentes
        if (fromAccountId.equals(toAccountId)) {
            log.warn("❌ Error: Intentando transferir a la misma cuenta");
            return Mono.just(TransferResult.failure(
                    fromAccountId, toAccountId, amount,
                    "No puedes transferir dinero a la misma cuenta"
            ));
        }

        //VALIDACIÓN 2: El monto debe ser positivo
        if (amount <= 0) {
            log.warn("❌ Error: Monto inválido: ${}", amount);
            return Mono.just(TransferResult.failure(
                    fromAccountId, toAccountId, amount,
                    "El monto debe ser mayor a $0"
            ));
        }

        //VALIDACIÓN 3: No exceder el límite máximo
        if (amount > MAX_TRANSFER_AMOUNT) {
            log.warn("❌ Error: Monto excede el límite máximo de ${}", MAX_TRANSFER_AMOUNT);
            return Mono.just(TransferResult.failure(
                    fromAccountId, toAccountId, amount,
                    "El monto excede el límite máximo de $" + MAX_TRANSFER_AMOUNT
            ));
        }

        //PASO 1: Buscar ambas cuentas en paralelo (más eficiente)
        Mono<Account> fromAccountMono = accountRepository.getAccountById(fromAccountId);
        Mono<Account> toAccountMono = accountRepository.getAccountById(toAccountId);

        //PASO 2: Cuando ambas se obtienen, ejecutar la transferencia
        return fromAccountMono.zipWith(toAccountMono)
                .flatMap(tuple -> {
                    Account fromAccount = tuple.getT1();
                    Account toAccount = tuple.getT2();

                    //VALIDACIÓN 4: Verificar saldo suficiente
                    if (fromAccount.getBalance() < amount) {
                        log.warn("❌ Error: Saldo insuficiente. Disponible: ${}, Requerido: ${}",
                                fromAccount.getBalance(), amount);
                        return Mono.just(TransferResult.failure(
                                fromAccountId, toAccountId, amount,
                                "Saldo insuficiente. Disponible: $" + fromAccount.getBalance()
                        ));
                    }

                    //PASO 3: Ejecutar la transferencia (actualizar saldos)
                    log.info("✓ Validaciones pasadas. Ejecutando transferencia...");

                    //Crear cuentas actualizadas con nuevos saldos
                    Account updatedFromAccount = Account.builder()
                            .id(fromAccount.getId())
                            .ownerId(fromAccount.getOwnerId())
                            .balance(fromAccount.getBalance() - amount)  //Restar
                            .build();

                    Account updatedToAccount = Account.builder()
                            .id(toAccount.getId())
                            .ownerId(toAccount.getOwnerId())
                            .balance(toAccount.getBalance() + amount)    //Sumar
                            .build();

                    //PASO 4: Guardar ambas cuentas actualizadas
                    return accountRepository.update(updatedFromAccount)
                            .then(accountRepository.update(updatedToAccount))
                            .then(Mono.fromCallable(() -> {
                                // PASO 5: Generar resultado exitoso
                                String transferId = UUID.randomUUID().toString();
                                log.info("✅ Transferencia completada exitosamente. ID: {}", transferId);

                                return TransferResult.success(
                                        transferId,
                                        fromAccountId,
                                        toAccountId,
                                        amount
                                );
                            }));
                })
                .onErrorResume(error -> {
                    //Manejar cualquier error inesperado
                    log.error("❌ Error inesperado durante la transferencia: {}", error.getMessage());
                    return Mono.just(TransferResult.failure(
                            fromAccountId, toAccountId, amount,
                            "Error del sistema: " + error.getMessage()
                    ));
                });
    }
}

