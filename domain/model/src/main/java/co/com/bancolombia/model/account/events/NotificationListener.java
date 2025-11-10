package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;
import lombok.extern.slf4j.Slf4j;

/**
 * Observer concreto: Envía notificaciones cuando ocurren eventos
 */
@Slf4j
public class NotificationListener implements AccountEventListener {

    @Override
    public void onAccountCreated(Account account) {
        log.info("NOTIFICATION: New account created - ID: {}, Owner: {}, Balance: {}",
            account.getId(), account.getOwnerId(), account.getBalance());
        // Aquí se podría integrar con un servicio de email/SMS
    }

    @Override
    public void onBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        log.info("NOTIFICATION: Balance changed for account {} - From: {} To: {}",
            account.getId(), oldBalance, newBalance);

        // Notificar si el cambio es significativo
        double change = Math.abs(newBalance - oldBalance);
        if (change > 1000) {
            log.warn("ALERT: Large balance change detected: ${}", change);
        }
    }

    @Override
    public void onAccountDeleted(Long accountId) {
        log.info("NOTIFICATION: Account deleted - ID: {}", accountId);
    }
}

