package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * USE CASE: BÚSQUEDA DE CUENTAS CON DIFERENTES COMPLEJIDADES
 *
 * Este Use Case demuestra diferentes ALGORITMOS DE BÚSQUEDA y sus complejidades.
 *
 * COMPLEJIDAD ALGORÍTMICA - ¿Qué es?
 * Es el tiempo que tarda un algoritmo dependiendo de la cantidad de datos (n).
 *
 * Notación Big O:
 * - O(1): Constante - Siempre tarda lo mismo (ej: acceder a un array por índice)
 * - O(log n): Logarítmica - Muy rápido incluso con muchos datos (ej: búsqueda binaria)
 * - O(n): Lineal - Tiempo proporcional a cantidad de datos (ej: recorrer una lista)
 * - O(n log n): Casi lineal - Algoritmos eficientes de ordenamiento
 *
 * Ejemplo con 1,000,000 de cuentas:
 * - O(1): 1 operación
 * - O(log n): ~20 operaciones
 * - O(n): 1,000,000 operaciones
 * - O(n log n): ~20,000,000 operaciones
 */
@RequiredArgsConstructor
public class AccountSearchUseCase {

    private final AccountRepository accountRepository;

    /**
     * ESTRUCTURA DE DATOS: HashMap para cache
     *
     * HashMap es como un diccionario:
     * - Clave: ID de cuenta
     * - Valor: Objeto Account
     *
     * Ventaja: Búsqueda INSTANTÁNEA O(1)
     */
    private final Map<Long, Account> accountCache = new ConcurrentHashMap<>();

    /**
     * BÚSQUEDA CON CACHE - COMPLEJIDAD: O(1) después de la primera vez
     *
     * ¿Cómo funciona?
     * 1. Primera vez: Busca en BD (lento - O(n))
     * 2. Guarda en cache (memoria RAM)
     * 3. Siguientes veces: Lee del cache (súper rápido - O(1))
     *
     * Es como recordar algo en vez de buscarlo en un libro cada vez.
     *
     * COMPLEJIDAD:
     * - Primera búsqueda: O(n) - busca en base de datos
     * - Búsquedas siguientes: O(1) - lee de memoria
     *
     * @param id ID de la cuenta a buscar
     * @return Mono con la cuenta encontrada
     */
    public Mono<Account> findByIdWithCache(Long id) {
        // Paso 1: Buscar en el cache (memoria)
        Account cached = accountCache.get(id); // O(1) - instantáneo

        if (cached != null) {
            // ¡Encontrado en cache! Retornar inmediatamente
            return Mono.just(cached);
        }

        // Paso 2: No está en cache, buscar en base de datos
        return accountRepository.getAccountById(id)
            .doOnNext(account -> {
                // Paso 3: Guardar en cache para la próxima vez
                accountCache.put(id, account); // O(1)
            });
    }

    /**
     * BÚSQUEDA BINARIA - COMPLEJIDAD: O(log n)
     *
     * ¿Qué es búsqueda binaria?
     * Es como buscar en un diccionario:
     * 1. Abres en la mitad
     * 2. ¿Es mayor o menor? Eliges una mitad
     * 3. Repites hasta encontrar
     *
     * REQUISITO: La lista DEBE estar ordenada por ID
     *
     * ¿Por qué es rápida?
     * En cada paso ELIMINA LA MITAD de las opciones.
     * Con 1,000,000 elementos, solo necesitas ~20 comparaciones!
     *
     * COMPLEJIDAD: O(log n) - muy eficiente
     *
     * Ejemplo con [1, 3, 5, 7, 9, 11, 13, 15], buscar 7:
     * 1. Mitad = 7 ✓ ¡Encontrado!
     *
     * Ejemplo buscar 3:
     * 1. Mitad = 7, 3 < 7, buscar en mitad izquierda [1,3,5]
     * 2. Mitad = 3 ✓ ¡Encontrado!
     *
     * @param sortedAccounts Lista ORDENADA de cuentas por ID
     * @param id ID a buscar
     * @return Mono con la cuenta encontrada o null
     */
    public Mono<Account> binarySearchById(List<Account> sortedAccounts, Long id) {
        return Mono.fromCallable(() -> {
            int left = 0;                          // Inicio de la búsqueda
            int right = sortedAccounts.size() - 1; // Final de la búsqueda

            // Mientras queden elementos por revisar
            while (left <= right) {
                // Calcular el punto medio
                int mid = left + (right - left) / 2;
                Account midAccount = sortedAccounts.get(mid);

                // ¿Es el que buscamos?
                if (midAccount.getId().equals(id)) {
                    return midAccount; // ¡Encontrado!
                }
                // ¿El que buscamos es mayor? Buscar en mitad derecha
                else if (midAccount.getId() < id) {
                    left = mid + 1;
                }
                // El que buscamos es menor, buscar en mitad izquierda
                else {
                    right = mid - 1;
                }
            }

            return null; // No encontrado
        });
    }

    /**
     * ORDENAMIENTO POR SALDO - COMPLEJIDAD: O(n log n)
     *
     * ¿Qué hace?
     * Ordena todas las cuentas de mayor a menor saldo.
     *
     * Usa el algoritmo Dual-Pivot Quicksort de Java (muy eficiente).
     *
     * COMPLEJIDAD: O(n log n) - buena para ordenar
     *
     * Con 1,000 cuentas: ~10,000 operaciones (muy rápido)
     *
     * @param accounts Lista de cuentas a ordenar
     * @return Flux con cuentas ordenadas por saldo (mayor a menor)
     */
    public Flux<Account> sortAccountsByBalance(List<Account> accounts) {
        return Flux.fromIterable(accounts)
            .collectList()
            .flatMapMany(list -> {
                // Ordenar de mayor a menor saldo
                list.sort(Comparator.comparing(Account::getBalance).reversed());
                return Flux.fromIterable(list);
            });
    }

    /**
     * BÚSQUEDA LINEAL - COMPLEJIDAD: O(n)
     *
     * ¿Qué hace?
     * Recorre TODA la lista uno por uno hasta encontrar el elemento.
     *
     * Es como buscar un libro en una biblioteca sin orden:
     * Tienes que revisar libro por libro.
     *
     * COMPLEJIDAD: O(n) - tiempo proporcional a cantidad de datos
     *
     * Con 1,000 cuentas: hasta 1,000 comparaciones
     *
     * ¿Cuándo usarla?
     * - Cuando la lista es pequeña
     * - Cuando no está ordenada
     * - Cuando buscas por un campo que no es clave
     *
     * @param accounts Lista de cuentas
     * @param ownerId ID del propietario a buscar
     * @return Mono con la primera cuenta encontrada
     */
    public Mono<Account> linearSearchByOwner(List<Account> accounts, Long ownerId) {
        return Flux.fromIterable(accounts)
            .filter(account -> account.getOwnerId().equals(ownerId))
            .next(); // Retorna la primera coincidencia
    }

    /**
     * LIMPIAR CACHE
     *
     * Elimina todas las cuentas guardadas en memoria.
     * Útil para liberar memoria o forzar recarga desde BD.
     *
     * COMPLEJIDAD: O(1)
     *
     * @return Mono<Void>
     */
    public Mono<Void> clearCache() {
        return Mono.fromRunnable(accountCache::clear);
    }

    /**
     * INVALIDAR CACHE DE UNA CUENTA ESPECÍFICA
     *
     * Elimina una cuenta del cache.
     * Útil cuando se actualiza la cuenta y necesitas recargarla.
     *
     * COMPLEJIDAD: O(1)
     *
     * @param id ID de la cuenta a invalidar
     * @return Mono<Void>
     */
    public Mono<Void> invalidateCache(Long id) {
        return Mono.fromRunnable(() -> accountCache.remove(id));
    }
}
