package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * USE CASE: HISTORIAL DE TRANSACCIONES
 *
 * ESTRUCTURA DE DATOS: Deque (Double-Ended Queue = Cola de Doble Extremo)
 *
 * ¿Qué es un Deque?
 * - Es una cola donde puedes agregar y quitar elementos de ambos extremos
 * - Imagina una fila donde puedes entrar por adelante O por atrás
 * - También puedes salir por adelante O por atrás
 *
 * ¿Por qué usamos Deque aquí?
 * - Queremos mantener las últimas 100 transacciones por cuenta
 * - Agregamos nuevas transacciones al FINAL (O(1) = instantáneo)
 * - Eliminamos transacciones viejas del INICIO (O(1) = instantáneo)
 * - Es MUY eficiente para este caso de uso
 *
 * COMPLEJIDAD ALGORÍTMICA:
 * - Agregar transacción: O(1) - tiempo constante
 * - Obtener últimas N: O(n) - proporcional a N
 * - Limpiar historial: O(1) - tiempo constante
 */
public class TransactionHistoryUseCase {

    /** Máximo de transacciones que guardamos en memoria por cuenta */
    private static final int MAX_HISTORY_SIZE = 100;

    /**
     * Almacén en memoria de historiales por cuenta
     *
     * Map<Long, Deque<Transaction>> significa:
     * - Clave (Long): ID de la cuenta
     * - Valor (Deque): Cola con las transacciones de esa cuenta
     *
     * ConcurrentHashMap es thread-safe (seguro para múltiples usuarios)
     */
    private final Map<Long, Deque<Transaction>> accountHistories = new ConcurrentHashMap<>();

    /**
     * AGREGA UNA TRANSACCIÓN AL HISTORIAL
     *
     * Algoritmo FIFO (First In, First Out = Primero en Entrar, Primero en Salir)
     *
     * ¿Cómo funciona?
     * 1. Busca el historial de la cuenta (o crea uno nuevo)
     * 2. Agrega la transacción al FINAL de la cola
     * 3. Si ya hay 100 transacciones, elimina la MÁS ANTIGUA del INICIO
     *
     * COMPLEJIDAD: O(1) - súper rápido, siempre toma el mismo tiempo
     *
     * Ejemplo:
     * Historial actual: [Trans1, Trans2, Trans3, ..., Trans100]
     * Agregar Trans101:
     * 1. Agrega Trans101 al final → [Trans1, Trans2, ..., Trans100, Trans101]
     * 2. Elimina Trans1 del inicio → [Trans2, Trans3, ..., Trans100, Trans101]
     *
     * @param transaction Transacción a agregar
     * @return Mono<Void> - operación reactiva que no retorna valor
     */
    public Mono<Void> addTransaction(Transaction transaction) {
        return Mono.fromRunnable(() -> {
            // Paso 1: Obtener o crear el historial de esta cuenta
            Deque<Transaction> history = accountHistories.computeIfAbsent(
                transaction.getAccountId(),  // Clave: ID de la cuenta
                k -> new LinkedList<>()      // Si no existe, crear nueva cola
            );

            // Paso 2: Agregar la transacción al FINAL
            history.addLast(transaction);

            // Paso 3: Si excede el límite, eliminar la MÁS ANTIGUA (del inicio)
            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeFirst(); // Elimina la primera (más vieja)
            }
        });
    }

    /**
     * OBTIENE LAS ÚLTIMAS N TRANSACCIONES
     *
     * Algoritmo LIFO parcial (Last In, First Out para lectura)
     * Lee desde el final hacia atrás.
     *
     * ¿Cómo funciona?
     * 1. Busca el historial de la cuenta
     * 2. Lee desde el FINAL hacia el INICIO
     * 3. Retorna hasta 'limit' transacciones
     *
     * COMPLEJIDAD: O(n) donde n = limit (número de transacciones pedidas)
     *
     * Ejemplo:
     * Historial: [Trans1, Trans2, Trans3, Trans4, Trans5]
     * getLastTransactions(123, 3) retorna: [Trans5, Trans4, Trans3]
     *
     * @param accountId ID de la cuenta
     * @param limit Cantidad máxima de transacciones a retornar
     * @return Flux con las últimas transacciones (más recientes primero)
     */
    public Flux<Transaction> getLastTransactions(Long accountId, int limit) {
        return Mono.fromSupplier(() -> accountHistories.get(accountId))
            .flatMapMany(history -> {
                // Si no hay historial, retornar vacío
                if (history == null) {
                    return Flux.empty();
                }

                // Crear lista para las últimas transacciones
                LinkedList<Transaction> result = new LinkedList<>();

                // Iterator que lee desde el FINAL hacia el INICIO
                var iterator = history.descendingIterator();
                int count = 0;

                // Agregar hasta 'limit' transacciones
                while (iterator.hasNext() && count < limit) {
                    result.add(iterator.next());
                    count++;
                }

                // Retornar como Flux (stream reactivo)
                return Flux.fromIterable(result);
            });
    }

    /**
     * OBTIENE TODAS LAS TRANSACCIONES DE UNA CUENTA
     *
     * Retorna el historial completo (hasta 100 transacciones).
     *
     * COMPLEJIDAD: O(n) donde n = cantidad de transacciones guardadas
     *
     * @param accountId ID de la cuenta
     * @return Flux con todas las transacciones guardadas
     */
    public Flux<Transaction> getAllTransactions(Long accountId) {
        return Mono.fromSupplier(() -> accountHistories.get(accountId))
            .flatMapMany(history -> {
                if (history == null) {
                    return Flux.empty();
                }
                return Flux.fromIterable(history);
            });
    }

    /**
     * LIMPIA EL HISTORIAL DE UNA CUENTA
     *
     * Elimina todas las transacciones guardadas de una cuenta.
     *
     * COMPLEJIDAD: O(1) - tiempo constante
     *
     * @param accountId ID de la cuenta
     * @return Mono<Void> - operación sin retorno
     */
    public Mono<Void> clearHistory(Long accountId) {
        return Mono.fromRunnable(() -> accountHistories.remove(accountId));
    }
}
