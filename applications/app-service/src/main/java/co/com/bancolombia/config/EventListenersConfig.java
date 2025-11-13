package co.com.bancolombia.config;

import co.com.bancolombia.model.account.events.AuditListener;
import co.com.bancolombia.model.account.events.NotificationListener;
import co.com.bancolombia.usecase.account.AccountEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * CONFIGURACIÓN DE LISTENERS DE EVENTOS
 *
 * PATRÓN OBSERVER - Configuración
 *
 * ¿Qué hace esta clase?
 * Registra los "observadores" (listeners) cuando la aplicación inicia.
 *
 * Es como suscribir personas a un canal:
 * - AccountEventUseCase es el canal
 * - NotificationListener y AuditListener son los suscriptores
 *
 * Cuando el canal publique algo, ambos lo recibirán.
 *
 * @EventListener(ApplicationReadyEvent.class):
 * Significa "ejecuta este método cuando la aplicación esté lista"
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventListenersConfig {

    /** Use Case de eventos (inyectado automáticamente por Spring) */
    private final AccountEventUseCase accountEventUseCase;

    /**
     * REGISTRA LOS LISTENERS AL INICIAR LA APLICACIÓN
     *
     * Se ejecuta automáticamente cuando Spring termina de iniciar.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerEventListeners() {
        log.info("📢 Registrando listeners de eventos de cuenta...");

        // LISTENER 1: Notificaciones
        // Este listener envía notificaciones al usuario
        accountEventUseCase.addListener(new NotificationListener());
        log.info("   ✓ NotificationListener registrado");

        // LISTENER 2: Auditoría
        // Este listener guarda logs para auditoría legal
        accountEventUseCase.addListener(new AuditListener());
        log.info("   ✓ AuditListener registrado");

        log.info("✅ Total de listeners activos: {}", accountEventUseCase.getListenerCount());
    }
}
