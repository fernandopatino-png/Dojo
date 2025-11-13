package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;
import lombok.extern.slf4j.Slf4j;


/**
 * OBSERVER CONCRETO: Listener de Notificaciones
 *
 * Esta clase IMPLEMENTA la interfaz AccountEventListener.
 * Su responsabilidad es NOTIFICAR al usuario sobre eventos importantes.
 *
 * ¿Qué hace?
 * - Envía logs (en producción serían emails/SMS/push notifications)
 * - Alerta sobre cambios importantes
 * - Detecta operaciones sospechosas
 *
 * Ejemplo de uso:
 * - Usuario crea cuenta → Envía "¡Bienvenido!"
 * - Usuario transfiere $5,000 → Envía "Transferencia grande detectada"
 */
@Slf4j
public class NotificationListener implements AccountEventListener {

    /**
     * Notifica cuando se crea una nueva cuenta
     * En producción, aquí enviarías un email de bienvenida
     */
    @Override
    public void onAccountCreated(Account account) {
        log.info("📧 NOTIFICACIÓN: Nueva cuenta creada");
        log.info("   → ID Cuenta: {}", account.getId());
        log.info("   → Propietario: {}", account.getOwnerId());
        log.info("   → Saldo inicial: ${}", account.getBalance());

        // TODO: En producción, enviar email/SMS al usuario
        // emailService.send(user.getEmail(), "Bienvenido", "Tu cuenta está lista");
    }

    /**
     * Notifica cuando cambia el saldo de una cuenta
     * Alerta si el cambio es significativo (> $1,000)
     */
    @Override
    public void onBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        log.info("📧 NOTIFICACIÓN: Cambio de saldo en cuenta {}", account.getId());
        log.info("   → Saldo anterior: ${}", oldBalance);
        log.info("   → Saldo nuevo: ${}", newBalance);

        // Calcular el cambio
        double change = Math.abs(newBalance - oldBalance);

        // Si el cambio es mayor a $1,000, enviar alerta especial
        if (change > 1000) {
            log.warn("⚠️ ALERTA: Cambio significativo detectado en cuenta {}", account.getId());
            log.warn("   → Monto del cambio: ${}", change);

            // TODO: Enviar notificación de seguridad
        }
    }

    /**
     * Notifica cuando se elimina una cuenta
     */
    @Override
    public void onAccountDeleted(Long accountId) {
        log.info("📧 NOTIFICACIÓN: Cuenta eliminada");
        log.info("   → ID Cuenta: {}", accountId);

        // TODO: Enviar email de confirmación de cierre
    }
}
