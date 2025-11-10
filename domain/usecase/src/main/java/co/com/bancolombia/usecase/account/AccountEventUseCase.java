package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.events.AccountEventListener;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case que implementa Observer Pattern
 * Gestiona listeners y notifica eventos de cuentas
 */
public class AccountEventUseCase {

    private final List<AccountEventListener> listeners = new ArrayList<>();

    /**
     * Registra un nuevo listener
     */
    public void addListener(AccountEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remueve un listener
     */
    public void removeListener(AccountEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Notifica a todos los listeners que se creó una cuenta
     */
    public Mono<Void> notifyAccountCreated(Account account) {
        return Mono.fromRunnable(() ->
            listeners.forEach(listener -> listener.onAccountCreated(account))
        );
    }

    /**
     * Notifica a todos los listeners que cambió el saldo
     */
    public Mono<Void> notifyBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        return Mono.fromRunnable(() ->
            listeners.forEach(listener -> listener.onBalanceChanged(account, oldBalance, newBalance))
        );
    }

    /**
     * Notifica a todos los listeners que se eliminó una cuenta
     */
    public Mono<Void> notifyAccountDeleted(Long accountId) {
        return Mono.fromRunnable(() ->
            listeners.forEach(listener -> listener.onAccountDeleted(accountId))
        );
    }
}

