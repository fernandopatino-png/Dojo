package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class AccountSearchUseCase {

    private final AccountRepository accountRepository;
    private final Map<Long, Account> accountCache = new ConcurrentHashMap<>();

    public Mono<Account> findByIdWithCache(Long id) {
        //Paso 1: Buscar en el cache (memoria)
        Account cached = accountCache.get(id); // O(1) - instantáneo

        if (cached != null) {
            //¡Encontrado en cache! Retornar inmediatamente
            return Mono.just(cached);
        }

        //Paso 2: No está en cache, buscar en base de datos
        return accountRepository.getAccountById(id)
                .doOnNext(account -> {
                    //Paso 3: Guardar en cache para la próxima vez
                    accountCache.put(id, account); // O(1)
                });
    }

    public Flux<Account> sortAccountsByBalance(List<Account> accounts) {
        return Flux.fromIterable(accounts)
                .collectList()
                .flatMapMany(list -> {
                    //Ordenar de mayor a menor saldo
                    list.sort(Comparator.comparing(Account::getBalance).reversed());
                    return Flux.fromIterable(list);
                });
    }

    public Mono<Void> clearCache() {
        return Mono.fromRunnable(accountCache::clear);
    }

    public Mono<Void> invalidateCache(Long id) {
        return Mono.fromRunnable(() -> accountCache.remove(id));
    }
}
