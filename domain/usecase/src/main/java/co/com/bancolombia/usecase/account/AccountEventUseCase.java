package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.events.AccountEventListener;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class AccountEventUseCase {

    private final List<AccountEventListener> listeners = new ArrayList<>();

    public void addListener(AccountEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AccountEventListener listener) {
        this.listeners.remove(listener);
    }

    public Mono<Void> notifyAccountCreated(Account account) {
        return Mono.fromRunnable(() -> {
            //Notificar
            listeners.forEach(listener -> listener.onAccountCreated(account));
        });
    }

    public Mono<Void> notifyBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        return Mono.fromRunnable(() -> {
            //Notificar
            listeners.forEach(listener ->
                    listener.onBalanceChanged(account, oldBalance, newBalance)
            );
        });
    }

    public Mono<Void> notifyAccountDeleted(Long accountId) {
        return Mono.fromRunnable(() -> {
            //Notificar
            listeners.forEach(listener -> listener.onAccountDeleted(accountId));
        });
    }

    public int getListenerCount() {
        return listeners.size();
    }
}

