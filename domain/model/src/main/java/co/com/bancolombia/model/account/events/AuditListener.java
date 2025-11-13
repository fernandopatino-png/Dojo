package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER CONCRETO: Listener de Auditoría
 *
 * Esta clase IMPLEMENTA la interfaz AccountEventListener.
 * Su responsabilidad es REGISTRAR todos los eventos para auditoría y compliance.
 *
 * ¿Qué es Auditoría?
 * Es un registro PERMANENTE de todas las operaciones que ocurren en el sistema.
 * Necesario para:
 * - Cumplimiento legal (compliance)
 * - Investigación de fraudes
 * - Resolución de disputas
 * - Análisis de uso
 *
 * En producción, esto se guardaría en una base de datos especial.
 */
@Slf4j
public class AuditListener implements AccountEventListener {

    /**
     * Lista en memoria de eventos de auditoría
     * En producción, esto iría a una base de datos
     */
    private final List<String> auditLog = new ArrayList<>();

    /**
     * Registra la creación de una cuenta
     */
    @Override
    public void onAccountCreated(Account account) {
        // Crear registro de auditoría con todos los detalles
        String entry = String.format(
            "[%s] CUENTA_CREADA - ID: %d, Propietario: %d, Saldo: $%.2f",
            LocalDateTime.now(),
            account.getId(),
            account.getOwnerId(),
            account.getBalance()
        );

        // Guardar en el log
        auditLog.add(entry);
        log.info("📝 AUDITORÍA: {}", entry);
    }

    /**
     * Registra cambios en el saldo
     */
    @Override
    public void onBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        // Crear registro detallado del cambio
        String entry = String.format(
            "[%s] SALDO_MODIFICADO - Cuenta: %d, Saldo Anterior: $%.2f, Saldo Nuevo: $%.2f",
            LocalDateTime.now(),
            account.getId(),
            oldBalance,
            newBalance
        );

        // Guardar en el log
        auditLog.add(entry);
        log.info("📝 AUDITORÍA: {}", entry);
    }

    /**
     * Registra la eliminación de una cuenta
     */
    @Override
    public void onAccountDeleted(Long accountId) {
        // Crear registro de eliminación
        String entry = String.format(
            "[%s] CUENTA_ELIMINADA - ID: %d",
            LocalDateTime.now(),
            accountId
        );

        // Guardar en el log
        auditLog.add(entry);
        log.info("📝 AUDITORÍA: {}", entry);
    }

    /**
     * Obtiene todo el log de auditoría
     * Útil para reportes o investigaciones
     *
     * @return Lista con todos los eventos registrados
     */
    public List<String> getAuditLog() {
        return new ArrayList<>(auditLog); // Retornar copia para seguridad
    }
}
