package co.com.bancolombia.model.account.gateways;

import co.com.bancolombia.model.account.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {

    Mono<Account> getAccountById(Long id);
    Mono<Account> getAccountByOwner(Long ownerId);

    // Operaciones CRUD completas
    Mono<Account> save(Account account);
    Mono<Account> update(Account account);
    Mono<Void> delete(Long id);
    Flux<Account> findAll();
    Flux<Account> findByOwnerId(Long ownerId);
    Mono<Boolean> exists(Long id);
}
