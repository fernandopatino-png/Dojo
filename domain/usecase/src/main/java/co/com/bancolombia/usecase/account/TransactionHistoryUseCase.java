package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionHistoryUseCase {

    private static final int MAX_HISTORY_SIZE = 100;

    private final Map<Long, Deque<Transaction>> accountHistories = new ConcurrentHashMap<>();

    public Mono<Void> addTransaction(Transaction transaction) {
        return Mono.fromRunnable(() -> {
            //Paso 1: Obtener o crear el historial de esta cuenta
            Deque<Transaction> history = accountHistories.computeIfAbsent(
                transaction.getAccountId(),  //Clave: ID de la cuenta
                k -> new LinkedList<>()      //Si no existe, crear nueva cola
            );

            //Paso 2: Agregar la transacción al FINAL
            history.addLast(transaction);

            //Paso 3: Si excede el límite, eliminar la MÁS ANTIGUA (del inicio)
            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeFirst(); //Elimina la primera (más vieja)
            }
        });
    }

    public Flux<Transaction> getLastTransactions(Long accountId, int limit) {
        return Mono.fromSupplier(() -> accountHistories.get(accountId))
            .flatMapMany(history -> {
                //Si no hay historial, retornar vacío
                if (history == null) {
                    return Flux.empty();
                }

                //Crear lista para las últimas transacciones
                LinkedList<Transaction> result = new LinkedList<>();

                //Iterator que lee desde el FINAL hacia el INICIO
                var iterator = history.descendingIterator();
                int count = 0;

                //Agregar hasta 'limit' transacciones
                while (iterator.hasNext() && count < limit) {
                    result.add(iterator.next());
                    count++;
                }
                //Retornar como Flux (stream reactivo)
                return Flux.fromIterable(result);
            });
    }

    public Flux<Transaction> getAllTransactions(Long accountId) {
        return Mono.fromSupplier(() -> accountHistories.get(accountId))
            .flatMapMany(history -> {
                if (history == null) {
                    return Flux.empty();
                }
                return Flux.fromIterable(history);
            });
    }

    public Mono<Void> clearHistory(Long accountId) {
        return Mono.fromRunnable(() -> accountHistories.remove(accountId));
    }
}
