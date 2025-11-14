package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationListener implements AccountEventListener {

    @Override
    public void onAccountCreated(Account account) {
        log.info("📧 NOTIFICACIÓN: Nueva cuenta creada");
        log.info("   → ID Cuenta: {}", account.getId());
        log.info("   → Propietario: {}", account.getOwnerId());
        log.info("   → Saldo inicial: ${}", account.getBalance());
    }

    @Override
    public void onBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        log.info("📧 NOTIFICACIÓN: Cambio de saldo en cuenta {}", account.getId());
        log.info("   → Saldo anterior: ${}", oldBalance);
        log.info("   → Saldo nuevo: ${}", newBalance);

        //Calcular el cambio
        double change = Math.abs(newBalance - oldBalance);

        //Si el cambio es mayor a $1,000, enviar alerta especial
        if (change > 1000) {
            log.warn("⚠️ ALERTA: Cambio significativo detectado en cuenta {}", account.getId());
            log.warn("   → Monto del cambio: ${}", change);
        }
    }

    @Override
    public void onAccountDeleted(Long accountId) {
        log.info("📧 NOTIFICACIÓN: Cuenta eliminada");
        log.info("   → ID Cuenta: {}", accountId);
    }
}
