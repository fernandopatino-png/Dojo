package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Observer concreto: Registra eventos para auditoría
 */
@Slf4j
public class AuditListener implements AccountEventListener {

    private final List<String> auditLog = new ArrayList<>();

    @Override
    public void onAccountCreated(Account account) {
        String entry = String.format("[%s] ACCOUNT_CREATED - ID: %d, Owner: %d, Balance: %.2f",
            LocalDateTime.now(), account.getId(), account.getOwnerId(), account.getBalance());
        auditLog.add(entry);
        log.info("AUDIT: {}", entry);
    }

    @Override
    public void onBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        String entry = String.format("[%s] BALANCE_CHANGED - Account: %d, From: %.2f, To: %.2f",
            LocalDateTime.now(), account.getId(), oldBalance, newBalance);
        auditLog.add(entry);
        log.info("AUDIT: {}", entry);
    }

    @Override
    public void onAccountDeleted(Long accountId) {
        String entry = String.format("[%s] ACCOUNT_DELETED - ID: %d",
            LocalDateTime.now(), accountId);
        auditLog.add(entry);
        log.info("AUDIT: {}", entry);
    }

    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}

