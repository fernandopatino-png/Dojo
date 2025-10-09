package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository repository;

    public Mono<Account> getAccountById(Long id) {
        return repository.getAccountById(id);
    }
}
