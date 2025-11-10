package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Transaction;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use Case para gestionar el historial de transacciones usando estructuras de datos eficientes
 * Usa Deque (Double-ended queue) para mantener un historial limitado en memoria
 * Complejidad: O(1) para agregar, O(1) para obtener las últimas N
 */
@RequiredArgsConstructor
public class TransactionHistoryUseCase {

    private static final int MAX_HISTORY_SIZE = 100;

    // Almacenamiento en memoria por cuenta (en producción esto estaría en BD)
    private final Map<Long, Deque<Transaction>> accountHistories = new ConcurrentHashMap<>();

    /**
     * Agrega una transacción al historial
     * Implementa FIFO: elimina la más antigua si excede el límite
     * Complejidad: O(1)
     */
    public Mono<Void> addTransaction(Transaction transaction) {
        return Mono.fromRunnable(() -> {
            Deque<Transaction> history = accountHistories.computeIfAbsent(
                transaction.getAccountId(),
                k -> new LinkedList<>()
            );

            history.addLast(transaction);

            // Si excede el límite, eliminar la más antigua
            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeFirst();
            }
        });
    }

    /**
     * Obtiene las últimas N transacciones (LIFO - Last In First Out)
     * Complejidad: O(n) donde n = limit
     */
    public Flux<Transaction> getLastTransactions(Long accountId, int limit) {
        return Mono.fromSupplier(() -> accountHistories.get(accountId))
            .flatMapMany(history -> {
                if (history == null) {
                    return Flux.empty();
                }

                // Obtener las últimas 'limit' transacciones
                LinkedList<Transaction> result = new LinkedList<>();
                var iterator = history.descendingIterator();
                int count = 0;

                while (iterator.hasNext() && count < limit) {
                    result.add(iterator.next());
                    count++;
                }

                return Flux.fromIterable(result);
            });
    }

    /**
     * Obtiene todas las transacciones de una cuenta
     */
    public Flux<Transaction> getAllTransactions(Long accountId) {
        return Mono.fromSupplier(() -> accountHistories.get(accountId))
            .flatMapMany(history -> history == null ? Flux.empty() : Flux.fromIterable(history));
    }

    /**
     * Limpia el historial de una cuenta
     */
    public Mono<Void> clearHistory(Long accountId) {
        return Mono.fromRunnable(() -> accountHistories.remove(accountId));
    }
}

