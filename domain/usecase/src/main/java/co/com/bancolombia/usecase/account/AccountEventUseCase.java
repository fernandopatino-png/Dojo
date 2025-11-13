package co.com.bancolombia.usecase.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.events.AccountEventListener;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * USE CASE: GESTIÓN DE EVENTOS DE CUENTA
 *
 * Implementa el PATRÓN OBSERVER (también llamado Publisher-Subscriber).
 *
 * ¿Cómo funciona?
 * 1. Mantiene una LISTA de listeners (observadores)
 * 2. Cuando ocurre un evento, NOTIFICA a TODOS los listeners
 * 3. Cada listener hace lo que quiera con la notificación
 *
 * ANALOGÍA:
 * Es como un sistema de alertas:
 * - Este Use Case es la torre de control
 * - Los listeners son las estaciones que escuchan
 * - Cuando hay una noticia, la torre la transmite a todas las estaciones
 *
 * LISTENERS ACTUALES:
 * - NotificationListener: Envía notificaciones al usuario
 * - AuditListener: Registra para auditoría legal
 *
 * Puedes agregar más sin modificar este código.
 */
public class AccountEventUseCase {

    /**
     * Lista de observadores (listeners) registrados
     * Todos ellos serán notificados cuando ocurra un evento
     */
    private final List<AccountEventListener> listeners = new ArrayList<>();

    /**
     * REGISTRAR UN NUEVO LISTENER
     *
     * Agrega un observador a la lista.
     * A partir de ahora, recibirá notificaciones de todos los eventos.
     *
     * Ejemplo:
     * eventUseCase.addListener(new NotificationListener());
     * eventUseCase.addListener(new AuditListener());
     *
     * @param listener Listener a agregar
     */
    public void addListener(AccountEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * REMOVER UN LISTENER
     *
     * Elimina un observador de la lista.
     * Ya no recibirá más notificaciones.
     *
     * @param listener Listener a remover
     */
    public void removeListener(AccountEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * NOTIFICAR: CUENTA CREADA
     *
     * Cuando se crea una cuenta, este método notifica a TODOS los listeners.
     *
     * Cada listener reacciona de forma diferente:
     * - NotificationListener → Envía email de bienvenida
     * - AuditListener → Guarda registro en log de auditoría
     *
     * @param account La cuenta que fue creada
     * @return Mono<Void> - operación reactiva sin retorno
     */
    public Mono<Void> notifyAccountCreated(Account account) {
        return Mono.fromRunnable(() -> {
            // Notificar a TODOS los listeners
            listeners.forEach(listener -> listener.onAccountCreated(account));
        });
    }

    /**
     * NOTIFICAR: SALDO MODIFICADO
     *
     * Cuando cambia el saldo de una cuenta, notifica a todos los listeners.
     *
     * @param account Cuenta modificada
     * @param oldBalance Saldo anterior
     * @param newBalance Saldo nuevo
     * @return Mono<Void>
     */
    public Mono<Void> notifyBalanceChanged(Account account, Double oldBalance, Double newBalance) {
        return Mono.fromRunnable(() -> {
            // Notificar a TODOS los listeners
            listeners.forEach(listener ->
                listener.onBalanceChanged(account, oldBalance, newBalance)
            );
        });
    }

    /**
     * NOTIFICAR: CUENTA ELIMINADA
     *
     * Cuando se elimina una cuenta, notifica a todos los listeners.
     *
     * @param accountId ID de la cuenta eliminada
     * @return Mono<Void>
     */
    public Mono<Void> notifyAccountDeleted(Long accountId) {
        return Mono.fromRunnable(() -> {
            // Notificar a TODOS los listeners
            listeners.forEach(listener -> listener.onAccountDeleted(accountId));
        });
    }

    /**
     * OBTENER CANTIDAD DE LISTENERS REGISTRADOS
     *
     * Útil para debugging o monitoreo.
     *
     * @return Cantidad de listeners activos
     */
    public int getListenerCount() {
        return listeners.size();
    }
}

