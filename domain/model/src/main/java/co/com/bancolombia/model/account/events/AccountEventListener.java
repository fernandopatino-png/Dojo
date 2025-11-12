package co.com.bancolombia.model.account.events;

import co.com.bancolombia.model.account.Account;

/**
 * PATRÓN OBSERVER - Interfaz de Observador de Eventos
 *
 * ¿Qué es el PATRÓN OBSERVER?
 * Es un patrón donde un objeto (Subject) mantiene una lista de "observadores"
 * y les NOTIFICA automáticamente cuando algo cambia.
 *
 * ANALOGÍA:
 * Es como suscribirse a un canal de YouTube:
 * - El canal (Subject) publica un video
 * - Todos los suscriptores (Observers) reciben notificación
 *
 * VENTAJAS:
 * 1. Desacoplamiento: El código principal no sabe quién escucha los eventos
 * 2. Extensibilidad: Puedes agregar nuevos listeners sin modificar el código
 * 3. Múltiples reacciones: Varios listeners pueden reaccionar al mismo evento
 *
 * EJEMPLO EN ESTE PROYECTO:
 * Cuando se crea una cuenta:
 * - NotificationListener envía un email
 * - AuditListener guarda un registro
 * - Ambos se ejecutan automáticamente sin que el código principal lo sepa
 *
 * ¿Cómo funciona?
 * 1. Defines esta interfaz (el contrato)
 * 2. Creas clases que implementan la interfaz (los listeners)
 * 3. Registras los listeners en el Subject
 * 4. Cuando ocurre un evento, el Subject llama a todos los listeners
 */
public interface AccountEventListener {

    /**
     * Se llama cuando se crea una nueva cuenta
     *
     * @param account La cuenta que fue creada
     */
    void onAccountCreated(Account account);

    /**
     * Se llama cuando cambia el saldo de una cuenta
     *
     * @param account La cuenta modificada
     * @param oldBalance Saldo anterior
     * @param newBalance Saldo nuevo
     */
    void onBalanceChanged(Account account, Double oldBalance, Double newBalance);

    /**
     * Se llama cuando se elimina una cuenta
     *
     * @param accountId ID de la cuenta eliminada
     */
    void onAccountDeleted(Long accountId);
}
