package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use Case que demuestra diferentes complejidades algorítmicas
 */
@RequiredArgsConstructor
public class AccountSearchUseCase {

    private final AccountRepository accountRepository;

    // Cache con HashMap para búsquedas O(1)
    private final Map<Long, Account> accountCache = new ConcurrentHashMap<>();

    /**
     * Búsqueda con cache - Complejidad: O(1)
     * Primera búsqueda: O(n) al consultar BD
     * Búsquedas posteriores: O(1) desde cache
     */
    public Mono<Account> findByIdWithCache(Long id) {
        // Primero buscar en cache
        Account cached = accountCache.get(id);
        if (cached != null) {
            return Mono.just(cached);
        }

        // Si no está en cache, buscar en BD y cachear
        return accountRepository.getAccountById(id)
            .doOnNext(account -> accountCache.put(id, account));
    }

    /**
     * Búsqueda binaria en lista ordenada - Complejidad: O(log n)
     * Asume que la lista viene ordenada por ID
     */
    public Mono<Account> binarySearchById(List<Account> sortedAccounts, Long id) {
        return Mono.fromCallable(() -> {
            int left = 0;
            int right = sortedAccounts.size() - 1;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                Account midAccount = sortedAccounts.get(mid);

                if (midAccount.getId().equals(id)) {
                    return midAccount;
                } else if (midAccount.getId() < id) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return null;
        });
    }

    /**
     * Ordenamiento de cuentas por saldo - Complejidad: O(n log n)
     * Usa QuickSort de Java (Dual-Pivot Quicksort)
     */
    public Flux<Account> sortAccountsByBalance(List<Account> accounts) {
        return Flux.fromIterable(accounts)
            .collectList()
            .flatMapMany(list -> {
                // Ordenar usando Comparator - O(n log n)
                list.sort(Comparator.comparing(Account::getBalance).reversed());
                return Flux.fromIterable(list);
            });
    }

    /**
     * Búsqueda lineal simple - Complejidad: O(n)
     */
    public Mono<Account> linearSearchByOwner(List<Account> accounts, Long ownerId) {
        return Flux.fromIterable(accounts)
            .filter(account -> account.getOwnerId().equals(ownerId))
            .next();
    }

    /**
     * Encontrar top N cuentas con mayor saldo - Complejidad: O(n log k)
     * Usa un MinHeap de tamaño k
     */
    public Flux<Account> findTopNAccountsByBalance(List<Account> accounts, int n) {
        return Mono.fromCallable(() -> {
            // PriorityQueue como MinHeap
            PriorityQueue<Account> minHeap = new PriorityQueue<>(
                Comparator.comparing(Account::getBalance)
            );

            for (Account account : accounts) {
                minHeap.offer(account);
                if (minHeap.size() > n) {
                    minHeap.poll(); // Remueve el mínimo
                }
            }

            // Convertir a lista ordenada descendente
            List<Account> result = new ArrayList<>(minHeap);
            result.sort(Comparator.comparing(Account::getBalance).reversed());
            return result;
        }).flatMapMany(Flux::fromIterable);
    }

    /**
     * Invalida el cache para un ID específico - Complejidad: O(1)
     */
    public Mono<Void> invalidateCache(Long id) {
        return Mono.fromRunnable(() -> accountCache.remove(id));
    }

    /**
     * Limpia todo el cache - Complejidad: O(1)
     */
    public Mono<Void> clearCache() {
        return Mono.fromRunnable(accountCache::clear);
    }
}

