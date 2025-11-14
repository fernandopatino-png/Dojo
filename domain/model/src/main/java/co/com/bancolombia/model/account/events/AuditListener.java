package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AuditListener implements AccountEventListener {

    private final List<String> auditLog = new ArrayList<>();

    @Override
    public void onAccountCreated(Account account) {
        String entry = String.format(
                "[%s] CUENTA_CREADA - ID: %d, Propietario: %d, Saldo: $%.2f",
                LocalDateTime.now(),
                account.getId(),
                account.getOwnerId(),
                account.getBalance()
        );

        auditLog.add(entry);
        log.info("📝 AUDITORÍA: {}", entry);
    }

    @Override
    public void onBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        //Crear registro detallado del cambio
        String entry = String.format(
                "[%s] SALDO_MODIFICADO - Cuenta: %d, Saldo Anterior: $%.2f, Saldo Nuevo: $%.2f",
                LocalDateTime.now(),
                account.getId(),
                oldBalance,
                newBalance
        );

        //Guardar en el log
        auditLog.add(entry);
        log.info("📝 AUDITORÍA: {}", entry);
    }

    @Override
    public void onAccountDeleted(Long accountId) {
        //Crear registro de eliminación
        String entry = String.format(
                "[%s] CUENTA_ELIMINADA - ID: %d",
                LocalDateTime.now(),
                accountId
        );

        //Guardar en el log
        auditLog.add(entry);
        log.info("📝 AUDITORÍA: {}", entry);
    }

    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
